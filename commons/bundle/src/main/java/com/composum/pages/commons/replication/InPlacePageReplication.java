package com.composum.pages.commons.replication;

import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.service.SiteManager;
import com.composum.sling.platform.staging.StagingReleaseManager;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        service = {ReplicationStrategy.class},
        property = {
                Constants.SERVICE_DESCRIPTION + "=In Place Page Replication Strategy"
        },
        immediate = true
)
public class InPlacePageReplication extends InPlaceReplicationStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(InPlacePageReplication.class);

    @Reference
    protected StagingReleaseManager releaseManager;

    @Reference
    protected SiteManager siteManager;

    @Override
    public boolean canReplicate(ReplicationContext context, Resource resource, boolean isReferenced) {
        boolean result = canReplicateSite(context, resource);
        if (result) {
            if (Page.isPage(resource)) {
                result = resource != null &&
                        (Page.isPage(resource) && resource.getChild(JcrConstants.JCR_CONTENT) != null);
            } else if (Page.isPageContent(resource)) {
                return Page.isPage(resource.getParent());
            } else {
                result = (Site.isSite(resource));
            }
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("canReplicate({}): {}", resource.getPath(), result);
        }
        return result;
    }

    @Override
    public void replicate(ReplicationContext context, Resource resource, boolean recursive) throws Exception {
        if (Page.isPageContent(resource)) // the replication works on pages, not their jcr:content
            super.replicate(context, resource.getParent(), recursive);
        else
            super.replicate(context, resource, recursive);
    }
}
