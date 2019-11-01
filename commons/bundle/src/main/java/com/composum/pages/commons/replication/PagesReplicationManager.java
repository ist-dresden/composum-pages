package com.composum.pages.commons.replication;

import com.composum.sling.core.BeanContext;
import com.composum.sling.platform.security.AccessMode;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component(
        service = {ReplicationManager.class},
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Replication Manager"
        },
        immediate = true
)
@Designate(ocd = PagesReplicationConfig.class)
public class PagesReplicationManager implements ReplicationManager {

    private static final Logger LOG = LoggerFactory.getLogger(PagesReplicationManager.class);

    protected PagesReplicationConfig config;

    protected List<ReplicationStrategy> instances = Collections.synchronizedList(new ArrayList<>());

    @Override
    public PagesReplicationConfig getConfig() {
        return config;
    }

    @Override
    public String getReplicationPath(@Nonnull final AccessMode accessMode, @Nonnull final String path) {
        switch (accessMode) {
            case PUBLIC:
                return config.inPlacePublicPath() + "/" + path.replaceAll("^/content/", "");
            case PREVIEW:
                return config.inPlacePreviewPath() + "/" + path.replaceAll("^/content/", "");
            case AUTHOR:
            default:
                return path;
        }
    }

    @Override
    public void replicateResource(ReplicationContext context, Resource resource, boolean recursive)
            throws Exception {
        replicateResource(context, resource, false, recursive);
    }

    @Override
    public void replicateReferences(ReplicationContext context)
            throws Exception {
        int maxLoops = 50;
        do {
            String[] references = context.references.toArray(new String[0]);
            context.references.clear();
            ResourceResolver resolver = context.getResolver();
            for (String referencedPath : references) {
                Resource resource = resolver.getResource(referencedPath);
                if (resource != null) {
                    replicateResource(context, resource, true, false);
                } else {
                    LOG.warn("can't resolve reference '{}'", referencedPath);
                }
            }
        } while (--maxLoops > 0 && context.references.size() > 0);
    }

    protected void replicateResource(ReplicationContext context, Resource resource,
                                     boolean isReference, boolean recursive)
            throws Exception {
        boolean replicationDone = false;
        String resourcePath = resource.getPath();
        if (!context.done.contains(resourcePath)) {
            // perform replication only of not already done
            for (ReplicationStrategy strategy : instances) {
                if (strategy.canReplicate(context, resource, isReference)) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("replicate" + (isReference ? " reference" : "") + " '{}' using {}",
                                resourcePath, strategy.getClass().getSimpleName());
                    }
                    strategy.replicate(context, resource, recursive);
                    replicationDone = true;
                }
            }
            if (replicationDone) {
                // register successful replication
                context.done.add(resourcePath);
            } else {
                if (isReference) {
                    LOG.warn("referenced resource '{}' not replicated", resource.getPath());
                } else if (LOG.isDebugEnabled()) {
                    LOG.debug("no strategy for replicating resource '{}'", resource.getPath());
                }
            }
        }
    }

    @Override
    public Resource getOrigin(BeanContext context, Resource replicate, AccessMode accessMode) {
        Resource origin = null;
        String replicatePath = replicate.getPath();
        String relativePath = null;
        String replicateRoot;
        switch (accessMode) {
            case PREVIEW:
                if (replicatePath.startsWith((replicateRoot = config.inPlacePreviewPath()) + "/")) {
                    relativePath = replicatePath.substring(replicateRoot.length());
                }
                break;
            case PUBLIC:
            default:
                if (replicatePath.startsWith((replicateRoot = config.inPlacePublicPath()) + "/")) {
                    relativePath = replicatePath.substring(replicateRoot.length());
                }
                break;
        }
        if (StringUtils.isNotBlank(relativePath)) {
            origin = context.getResolver().getResource(config.contentPath() + relativePath);
        }
        return origin;
    }

    @Reference(service = ReplicationStrategy.class, policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MULTIPLE)
    protected void addReplicationStrategy(@Nonnull final ReplicationStrategy strategy) {
        LOG.info("addReplicationStrategy: {}", strategy.getClass().getSimpleName());
        if (config != null) {
            strategy.activate(this);
        }
        instances.add(strategy);
    }

    protected void removeReplicationStrategy(@Nonnull final ReplicationStrategy strategy) {
        LOG.info("removeReplicationStrategy: {}", strategy.getClass().getSimpleName());
        instances.remove(strategy);
    }

    @Activate
    @Modified
    public void activate(PagesReplicationConfig config) {
        this.config = config;
        for (ReplicationStrategy strategy : instances) {
            strategy.activate(this);
        }
    }

    @Deactivate
    public void deactivate() {
        for (ReplicationStrategy strategy : instances) {
            strategy.deactivate(this);
        }
    }

    @Override
    @Deprecated
    public boolean releaseMappingAllowed(String path, String uri) {
        return releaseMappingAllowed(path);
    }

    @Override
    public boolean releaseMappingAllowed(String path) {
        return path.startsWith(getConfig().contentPath());
    }

}
