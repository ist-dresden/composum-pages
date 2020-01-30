package com.composum.pages.commons.replication;

import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.filter.StringFilter;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.core.util.SlingResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.composum.pages.commons.replication.ReplicationManager.REPLICATE_PROPERTY_FILTER;
import static com.composum.sling.platform.staging.StagingConstants.PROP_REPLICATED_VERSION;

/**
 * Some algorithms to compare two release trees. This is a separate class mostly to avoid passing around many
 * parameters.
 */
class ReleaseTreeCompareStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(ReleaseTreeCompareStrategy.class);

    /** {@link ResourceFilter} that returns true for versionables. */
    protected final ResourceFilter VERSIONABLE_FILTER =
            new ResourceFilter.NodeTypeFilter(new StringFilter.WhiteList(ResourceUtil.MIX_VERSIONABLE));

    protected final ResourceResolver resolver1;
    protected final Resource root1;
    protected final ResourceResolver resolver2;
    protected final Resource root2;

    public ReleaseTreeCompareStrategy(ResourceResolver resolver1, Resource root1, ResourceResolver resolver2, Resource root2) {
        this.resolver1 = resolver1;
        this.root1 = root1;
        this.resolver2 = resolver2;
        this.root2 = root2;
    }

    /**
     * Returns list of paths to versionables that are different below root1 and and root2 - one of them does not
     * exist or they have a different
     * {@link com.composum.sling.platform.staging.StagingConstants#PROP_REPLICATED_VERSION}.
     */
    public List<String> compareVersionables() {
        // These versionables do not exist below root2 or have different version number:
        List<String> differences = SlingResourceUtil.descendantsStream(root1, VERSIONABLE_FILTER::accept)
                .filter(VERSIONABLE_FILTER::accept)
                .filter((r1) -> {
                    Resource r2 = corresponding2Resource(r1);
                    return !StringUtils.equals(getAttribute(r1, PROP_REPLICATED_VERSION),
                            getAttribute(r2, PROP_REPLICATED_VERSION));
                })
                .map(Resource::getPath)
                .collect(Collectors.toList());
        // These do not exist below root1 and therefore were missed by differences
        List<String> missingResource1 = SlingResourceUtil.descendantsStream(root2, VERSIONABLE_FILTER::accept)
                .filter(VERSIONABLE_FILTER::accept)
                .filter((r2) -> corresponding1Resource(r2) == null)
                .map(Resource::getPath)
                .map((p2) -> SlingResourceUtil.appendPaths(
                        root1.getPath(),
                        SlingResourceUtil.relativePath(root2.getPath(), p2)))
                .collect(Collectors.toList());
        differences.addAll(missingResource1);
        return differences;
    }

    @Nullable
    protected String getAttribute(@Nullable Resource resource, @Nonnull String attribute) {
        return resource != null ? resource.getValueMap().get(attribute, String.class) : null;
    }

    @Nullable
    protected Resource corresponding2Resource(@Nonnull Resource resource1) {
        String relpath = SlingResourceUtil.relativePath(root1.getPath(), resource1.getPath());
        String path2 = SlingResourceUtil.appendPaths(root2.getPath(), relpath);
        return resolver2.getResource(path2);
    }

    @Nullable
    protected Resource corresponding1Resource(@Nonnull Resource resource2) {
        String relpath = SlingResourceUtil.relativePath(root2.getPath(), resource2.getPath());
        String path1 = SlingResourceUtil.appendPaths(root1.getPath(), relpath);
        return resolver1.getResource(path1);
    }

    protected boolean hasOrderableChildren(@Nullable Resource resource) {
        if (resource == null) { return false; }
        Node node = resource.adaptTo(Node.class);
        if (node == null) {
            throw new IllegalArgumentException("Not a JCR-based resource: " + resource);
        }
        try {
            return node.getPrimaryNodeType().hasOrderableChildNodes();
        } catch (RepositoryException e) {
            LOG.error("Trouble determining child orderability for {}", SlingResourceUtil.getPath(resource), e);
            return true; // play safe.
        }
    }

    protected List<String> childOrder(@Nullable Resource resource) {
        List<String> result = new ArrayList<>();
        if (resource != null && hasOrderableChildren(resource)) {
            for (Resource child : resource.getChildren()) {
                result.add(child.getName());
            }
        }
        return result;
    }

    /**
     * Returns a list of paths to resources that exist on both sides, have orderable children and have a different
     * child ordering.
     */
    public List<String> compareChildrenOrderings() {
        List<String> result = SlingResourceUtil.descendantsStream(root1, VERSIONABLE_FILTER::accept)
                .filter((r) -> !VERSIONABLE_FILTER.accept(r))
                .filter((r1) -> {
                    Resource r2 = corresponding2Resource(r1);
                    return r2 != null && !Objects.equals(childOrder(r1), childOrder(r2));
                })
                .map(Resource::getPath)
                .collect(Collectors.toList());
        return result;
    }

    /**
     * Returns a list of paths to parent nodes of versionables (but below the roots) which have different attributes.
     */
    public List<String> compareParentNodes() {
        List<String> result = SlingResourceUtil.descendantsStream(root1, VERSIONABLE_FILTER::accept)
                .filter((r) -> !VERSIONABLE_FILTER.accept(r))
                .filter((r1) -> {
                    Resource r2 = corresponding2Resource(r1);
                    return r2 != null && !attributesEqual(r1, r2);
                })
                .map(Resource::getPath)
                .collect(Collectors.toList());
        return result;
    }

    protected boolean attributesEqual(@Nonnull Resource resource1, @Nonnull Resource resource2) {
        ValueMap vm1 = resource1.getValueMap();
        ValueMap vm2 = resource2.getValueMap();
        Set<String> props1 = vm1.keySet().stream()
                .filter(REPLICATE_PROPERTY_FILTER::accept)
                .collect(Collectors.toSet());
        Set<String> props2 =
                vm2.keySet().stream()
                        .filter(REPLICATE_PROPERTY_FILTER::accept)
                        .collect(Collectors.toSet());
        boolean result = props1.equals(props2);
        if (result) { // if keys are the same, compare details
            for (String key : props1) {
                result = result && attributeEqual(vm1.get(key), vm2.get(key), key, resource1.getPath());
            }
        }
        return result;
    }

    private boolean attributeEqual(@Nonnull Object val1, @Nonnull Object val2, String name, String path) {
        boolean equal;
        if (val1 instanceof String) {
            if (val2 instanceof String) {
                equal = Objects.equals(val1, val2);
                if (!equal && StringUtils.startsWith((String) val1, root1.getPath())) {
                    // check whether this is a translated path
                    String relpath = SlingResourceUtil.relativePath(root1.getPath(), (String) val1);
                    String path2 = SlingResourceUtil.appendPaths(root2.getPath(), relpath);
                    equal = StringUtils.equals(path2, (String) val2);
                }
            } else {
                equal = false;
            }
        } else if (val1.getClass().isArray()) {
            if (val2.getClass().isArray()) {
                equal = Arrays.equals((Object[]) val1, (Object[]) val2);
            } else {
                equal = false;
            }
        } else {
            equal = Objects.equals(val1, val2);
        }
        if (!equal) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Attributes not equal at {}/{} : {} vs. {}", path, name, val1, val2);
            }
        }
        return equal;
    }

}
