package com.composum.pages.commons.replication;

import com.composum.pages.commons.filter.SitePageFilter;
import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.service.SiteManager;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.core.util.SlingResourceUtil;
import com.composum.sling.platform.security.AccessMode;
import com.composum.sling.platform.staging.ReleaseChangeEventListener;
import com.composum.sling.platform.staging.StagingReleaseManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Augments the {@link com.composum.pages.commons.servlet.ReleaseServlet} and callback from platform about replicated pages
 * - triggers the various replication strategies in Pages.
 */
@Component(
        service = {PagesReleaseService.class, ReleaseChangeEventListener.class},
        property = {
                Constants.SERVICE_DESCRIPTION + "=Pages Replication Service"
        },
        immediate = true
)
public class PagesReleaseServiceImpl implements ReleaseChangeEventListener, PagesReleaseService {

    private static final Logger LOG = LoggerFactory.getLogger(PagesReleaseServiceImpl.class);

    @Reference
    protected ReplicationManager replicationManager;

    @Reference
    protected SiteManager siteManager;

    @Reference
    protected StagingReleaseManager releaseManager;


    @Override
    public void receive(ReleaseChangeEvent event) throws ReplicationFailedException {
        LOG.info("PagesReplicationManager.receive {}", event);
        try {
            StagingReleaseManager.Release release = event.release();
            List<AccessMode> accessModes = release.getMarks().stream().map(AccessMode::accessModeValue).filter(Objects::nonNull).collect(Collectors.toList());
            if (accessModes.isEmpty()) {
                return; // not published
            }

            BeanContext beanContext = new BeanContext.Service(release.getReleaseRoot().getResourceResolver());
            Site site = siteManager.getContainingSite(beanContext, release.getReleaseRoot());
            ResourceFilter releaseFilter = new SitePageFilter(site.getPath(), ResourceFilter.ALL);
            ResourceResolver stagedResolver = releaseManager.getResolverForRelease(release, replicationManager, false);

            List<String> pathsToReplicate = new ArrayList<>();
            pathsToReplicate.addAll(event.newOrMovedResources());
            pathsToReplicate.addAll(event.updatedResources());
            for (String removedPath : event.removedOrMovedResources()) {
                // for removed resources find next higher still existing parent and replicate that.
                Resource resource = stagedResolver.getResource(removedPath);
                while (resource == null && StringUtils.isNotBlank(removedPath)) {
                    removedPath = ResourceUtil.getParent(removedPath);
                    resource = stagedResolver.getResource(removedPath);
                }
                if (resource != null) { pathsToReplicate.add(removedPath); }
            }
            pathsToReplicate = removeRedundantPaths(pathsToReplicate);

            for (AccessMode accessMode : accessModes) {
                ReplicationContext replicationContext = new ReplicationContext(beanContext, site, accessMode, releaseFilter, stagedResolver);

                for (String path : pathsToReplicate) {
                    Resource resource = stagedResolver.getResource(path);
                    if (resource != null) {
                        replicationManager.replicateResource(replicationContext, resource, true);
                    } else {
                        LOG.warn("Could not find replicated resource {} from {}", path, event);
                    }
                }

                replicationManager.replicateReferences(replicationContext);
            }
        } catch (ReplicationFailedException e) {
            throw e;
        } catch (Exception e) {
            throw new ReplicationFailedException("Could not replicate", e, event);
        }
    }

    private List<String> removeRedundantPaths(List<String> pathsToReplicate) {
        List<String> res = new ArrayList<>();
        for (String path : pathsToReplicate) {
            if (res.stream().noneMatch(p -> SlingResourceUtil.isSameOrDescendant(p, path))) {
                res.removeIf(p -> SlingResourceUtil.isSameOrDescendant(path, p));
                res.add(path);
            }
        }
        return res;
    }


}
