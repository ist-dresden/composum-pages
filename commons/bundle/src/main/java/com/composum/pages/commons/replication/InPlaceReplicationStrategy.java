package com.composum.pages.commons.replication;

import com.composum.pages.commons.model.Site;
import com.composum.sling.core.JcrResource;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.platform.security.PlatformAccessFilter;
import com.composum.sling.platform.staging.service.ReleaseMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class InPlaceReplicationStrategy implements ReplicationStrategy, ReleaseMapper {

    private static final Logger LOG = LoggerFactory.getLogger(InPlaceReplicationStrategy.class);

    protected ReplicationManager replicationManager;

    protected PagesReplicationConfig config;
    protected String contentPath;
    protected Pattern contentPathPattern;
    protected Pattern contentLinkPattern;

    protected PagesReplicationConfig getConfig() {
        return replicationManager.getConfig();
    }

    @Override
    public boolean releaseMappingAllowed(String path, String uri) {
        return releaseMappingAllowed(path);
    }

    @Override
    public boolean releaseMappingAllowed(String path) {
        return path.startsWith(getConfig().contentPath());
    }

    protected String getTargetPath(ReplicationContext context) {
        switch (context.accessMode) {
            case PREVIEW:
                return config.inPlacePreviewPath();
            case PUBLIC:
                return config.inPlacePublicPath();
            default:
                return null;
        }
    }

    /**
     * @return the resolver to use for traversing the released content (the replication source)
     */
    protected abstract ResourceResolver getReleaseResolver(ReplicationContext context, Resource resource);

    /**
     * the general 'canReplicate' check fpr all InPlace replication strategy implementations
     *
     * @return 'true' strategy is configured and if the resource is part of a site which is replicated 'in place'
     */
    protected boolean canReplicateSite(ReplicationContext context, Resource resource) {
        boolean result = false;
        if (config != null && config.inPlaceEnabled()
                && PlatformAccessFilter.AccessMode.AUTHOR != context.accessMode) {
            result = Site.PUBLIC_MODE_IN_PLACE.equals(context.site.getPublicMode());
        }
        return result;
    }

    @Override
    public void replicate(ReplicationContext context, Resource resource, boolean recursive)
            throws Exception {
        ResourceResolver targetResolver = context.getResolver();
        String targetPath = getTargetPath(context);
        Resource targetRoot;
        if (StringUtils.isNotBlank(targetPath) && (targetRoot = targetResolver.getResource(targetPath)) != null) {
            String relativePath = resource.getPath().replaceAll("^" + config.contentPath() + "/", "");
            ResourceResolver releaseResolver = getReleaseResolver(context, resource);
            Resource released = releaseResolver.getResource(resource.getPath());
            replicate(context, targetRoot, released, relativePath, recursive, false);        }
    }

    /**
     * extension hook to control replication in subclasses
     */
    protected void replicate(ReplicationContext context, Resource targetRoot,
                             Resource released, String relativePath, boolean recursive, boolean merge)
            throws Exception {
        Resource replicate = targetRoot.getChild(relativePath);
        if (replicate == null) {
            ResourceResolver targetResolver = context.getResolver();
            String parentPath = getParentPath(relativePath);
            Resource replicateParent = parentPath != null
                    ? buildReplicationBase(context, targetResolver, targetRoot, released.getResourceResolver(), parentPath)
                    : targetRoot;
            if (replicateParent != null) {
                replicate = createReplicate(targetResolver, released, replicateParent);
            }
        }
        if (replicate != null) {
            copyReleasedResource(context, targetRoot.getPath(), released, replicate, recursive, false);
        } else {
            LOG.error("can't create replication target for '{}', accessMode={}", released.getPath(), context.accessMode);
        }
    }

    /**
     * build the path to a resource replicate if not always existing
     */
    protected Resource buildReplicationBase(ReplicationContext context,
                                            ResourceResolver targetResolver, Resource targetRoot,
                                            ResourceResolver releaseResolver, String relativePath)
            throws Exception {
        Resource targetResource = targetRoot.getChild(relativePath);
        if (targetResource == null) {
            String contentPath = getConfig().contentPath();
            String parentPath = getParentPath(relativePath);
            Resource replicateParent = parentPath != null
                    ? buildReplicationBase(context, targetResolver, targetRoot, releaseResolver, parentPath)
                    : targetRoot;
            Resource released = releaseResolver.getResource(contentPath + "/" + relativePath);
            if (released != null) {
                targetResource = createReplicate(targetResolver, released, replicateParent);
                copyReleasedResource(context, targetRoot.getPath(), released, targetResource, false, true);
            } else {
                LOG.error("no released resource found at '{}'", contentPath + "/" + relativePath);
            }
        }
        return targetResource;
    }

    protected void copyReleasedResource(ReplicationContext context, String targetRoot, Resource released, Resource replicate,
                                        boolean recursive, boolean merge)
            throws Exception {
        copyReleasedContent(context, targetRoot, released, replicate, false, merge);
        Resource releasedContent = released.getChild(JcrConstants.JCR_CONTENT);
        if (releasedContent != null) {
            Resource replicateContent = replicate.getChild(JcrConstants.JCR_CONTENT);
            if (replicateContent == null) {
                replicateContent = createReplicate(replicate.getResourceResolver(), releasedContent, replicate);
            }
            copyReleasedContent(context, targetRoot, releasedContent, replicateContent, true, merge);
        }
        if (recursive) {
            replicateChildren(context, released, true);
        }
    }

    protected void replicateChildren(ReplicationContext context, Resource released, boolean recursive)
            throws Exception {
        for (Resource releasedChild : released.getChildren()) {
            if (!JcrConstants.JCR_CONTENT.equals(releasedChild.getName())) {
                replicationManager.replicateResource(context, releasedChild, recursive);
            }
        }
    }

    protected void copyReleasedContent(ReplicationContext context, String targetRoot, Resource released, Resource replicate,
                                       boolean recursive, boolean merge)
            throws Exception {
        if (!merge) {
            // in case of a 'reset' remove all children from the target resource
            ResourceResolver replicateResolver = replicate.getResourceResolver();
            for (Resource child : replicate.getChildren()) {
                replicateResolver.delete(child);
            }
        }
        copyReleasedProperties(context, targetRoot, released, replicate, merge);
        if (recursive) {
            ResourceResolver replicateResolver = replicate.getResourceResolver();
            for (Resource releasedChild : released.getChildren()) {
                String name = releasedChild.getName();
                Resource replicateChild = replicate.getChild(name);
                if (replicateChild == null) {
                    replicateChild = createReplicate(replicateResolver, releasedChild, replicate);
                }
                copyReleasedContent(context, targetRoot, releasedChild, replicateChild, true, merge);
            }
        }
    }

    protected void copyReleasedProperties(ReplicationContext context, String targetRoot,
                                          Resource released, Resource replicate, boolean merge) {
        ValueMap releasedValues = released.getValueMap();
        ModifiableValueMap replicateValues = replicate.adaptTo(ModifiableValueMap.class);
        if (!merge) {
            // in case of a 'reset' remove all properties from the target resource
            for (String key : replicateValues.keySet().toArray(new String[0])) {
                if (!ReplicationManager.REPLICATE_PROPERTY_KEEP.accept(key)) {
                    replicateValues.remove(key);
                }
            }
        }
        for (Map.Entry<String, Object> entry : releasedValues.entrySet()) {
            String key = entry.getKey();
            if (ReplicationManager.REPLICATE_PROPERTY_FILTER.accept(key)) {
                // copy content properties if not always present or a 'reset' is requested
                if (replicateValues.get(key) == null ||
                        (!merge && !ReplicationManager.REPLICATE_PROPERTY_KEEP.accept(key))) {
                    Object value = entry.getValue();
                    if (value instanceof String) {
                        value = transformStringProperty(context, targetRoot, (String) value);
                    }
                    if (value != null) {
                        replicateValues.put(key, value);
                    }
                }
            }
        }
    }

    protected String transformStringProperty(ReplicationContext context, String targetRoot, String value) {
        Matcher contentPathMatcher = contentPathPattern.matcher(value);
        if (contentPathMatcher.matches()) {
            String path = contentPathMatcher.group(1);
            context.references.add(contentPath + path);
            value = targetRoot + path;
        } else {
            value = transformTextProperty(context, targetRoot, value);
        }
        return value;
    }

    public String transformTextProperty(ReplicationContext context, String targetRoot, String value) {
        StringBuilder result = new StringBuilder();
        Matcher matcher = contentLinkPattern.matcher(value);
        int len = value.length();
        int pos = 0;
        while (matcher.find(pos)) {
            String path = matcher.group(3);
            context.references.add(contentPath + path);
            result.append(value, pos, matcher.start());
            result.append(matcher.group(1));
            result.append(targetRoot);
            result.append(path);
            result.append(matcher.group(4));
            pos = matcher.end();
        }
        if (pos >= 0 && pos < len) {
            result.append(value, pos, len);
        }
        return result.toString();
    }

    @Override
    public void activate(ReplicationManager manager) {
        this.replicationManager = manager;
        config = manager.getConfig();
        contentPath = config.contentPath();
        contentPathPattern = Pattern.compile("^" + contentPath + "(/.*)$");
        contentLinkPattern = Pattern.compile("<(a\\s+(.+\\s+)?href=['\"])" + contentPath + "(/[^'\"]+)(['\"])");
    }

    @Override
    public void deactivate(ReplicationManager manager) {
        this.replicationManager = null;
        this.config = null;
    }

    // helpers

    protected Resource createReplicate(ResourceResolver replicateResolver, Resource released, Resource parent)
            throws PersistenceException {
        String primaryType = released instanceof JcrResource
                ? ((JcrResource) released).getPrimaryType()
                : ResourceHandle.use(released).getPrimaryType();
        String[] mixinTypes = released.getValueMap().get(JcrConstants.JCR_MIXINTYPES, String[].class);
        Map<String, Object> properties = new HashMap<>();
        properties.put(JcrConstants.JCR_PRIMARYTYPE, primaryType);
        if (mixinTypes != null) {
            List<String> acceptedMixins = new ArrayList<>();
            for (String mixinType : mixinTypes) {
                if (ReplicationManager.REPLICATE_MIXIN_TYPES_FILTER.accept(mixinType)) {
                    acceptedMixins.add(mixinType);
                }
            }
            if (acceptedMixins.size() > 0) {
                properties.put(JcrConstants.JCR_MIXINTYPES, acceptedMixins.toArray(new String[0]));
            }
        }
        return replicateResolver.create(parent, released.getName(), properties);
    }

    public static String getParentPath(String path) {
        int lastSlash = path.lastIndexOf('/');
        return lastSlash > 0 ? path.substring(0, lastSlash) : null;
    }
}
