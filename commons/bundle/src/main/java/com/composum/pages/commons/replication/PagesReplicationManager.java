package com.composum.pages.commons.replication;

import com.composum.sling.core.BeanContext;
import com.composum.sling.platform.security.AccessMode;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
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
@Deprecated
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
                return null;
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
