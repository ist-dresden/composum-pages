package com.composum.pages.commons.replication;

import com.composum.sling.core.BeanContext;
import com.composum.sling.platform.security.AccessMode;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
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
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

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

    protected BundleContext bundleContext;

    /**
     * A sorted map of the {@link ReplicationStrategy}'s sorted by the service references - that is, by priority.
     */
    protected Map<ServiceReference<ReplicationStrategy>, ReplicationStrategy> instances =
            Collections.synchronizedMap(new TreeMap<>());

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
            for (ReplicationStrategy strategy : instances.values()) {
                if (!replicationDone && strategy.canReplicate(context, resource, isReference)) {
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
    protected synchronized void addReplicationStrategy(@Nonnull final ServiceReference<ReplicationStrategy> strategyReference) {
        if (config != null) {
            ReplicationStrategy strategy = bundleContext.getService(strategyReference);
            LOG.info("addReplicationStrategy: {}", strategy.getClass().getSimpleName());
            strategy.activate(this);
            instances.put(strategyReference, strategy);
        }
    }

    protected synchronized void removeReplicationStrategy(@Nonnull final ServiceReference<ReplicationStrategy> strategyReference) {
        LOG.info("removeReplicationStrategy: {}", strategyReference.getClass().getSimpleName());
        instances.remove(strategyReference);
    }

    @Activate
    @Modified
    public synchronized void activate(BundleContext bundleContext, PagesReplicationConfig config) throws InvalidSyntaxException {
        LOG.info("activate");
        this.bundleContext = bundleContext;
        this.config = config;
        for (ServiceReference<ReplicationStrategy> serviceReference :
                bundleContext.getServiceReferences(ReplicationStrategy.class, null)) {
            addReplicationStrategy(serviceReference);
        }
    }

    @Deactivate
    public synchronized void deactivate() {
        LOG.info("deactivate");
        for (ReplicationStrategy strategy : instances.values()) {
            strategy.deactivate(this);
        }
        instances.clear();
        this.config = null;
        this.bundleContext = null;
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
