package com.composum.pages.commons.replication;

import com.composum.pages.commons.filter.SitePageFilter;
import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.service.SiteManager;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.logging.MessageContainer;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.core.util.SlingResourceUtil;
import com.composum.sling.platform.security.AccessMode;
import com.composum.sling.platform.staging.ReleaseChangeEventListener;
import com.composum.sling.platform.staging.ReleaseChangeEventPublisher;
import com.composum.sling.platform.staging.ReleaseChangeProcess;
import com.composum.sling.platform.staging.StagingReleaseManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.composum.sling.platform.staging.ReleaseChangeProcess.ReleaseChangeProcessorState.idle;

/**
 * Augments the {@link com.composum.pages.commons.servlet.ReleaseServlet} and callback from platform about replicated pages
 * - triggers the various replication strategies in Pages.
 */
@Component(
        service = {PagesReleaseService.class, ReleaseChangeEventListener.class},
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Replication Service"
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
                return; // not published - nothing to do in any case
            }

            BeanContext beanContext = new BeanContext.Service(release.getReleaseRoot().getResourceResolver());
            Site site = siteManager.getContainingSite(beanContext, release.getReleaseRoot());
            if (!Site.PUBLIC_MODE_IN_PLACE.equals(site.getPublicMode())) {
                return; // this is just for inPlace replication
            }
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

    @Nullable
    @Override
    public Collection<InPlaceReleasePublishingProcess> processesFor(@Nonnull StagingReleaseManager.Release release) {
        Collection<InPlaceReleasePublishingProcess> result = processesFor(release.getReleaseRoot()).stream()
                .filter(p -> StringUtils.equals(release.getReleaseRoot().getPath(), p.releaseRootPath))
                .filter(p -> StringUtils.equals(release.getPath(), p.releaseNodePath))
                .collect(Collectors.toList());
        return result;
    }

    @Nonnull
    @Override
    public Collection<InPlaceReleasePublishingProcess> processesFor(@Nullable Resource resource) {
        try {
            StagingReleaseManager.Release release = event.release();
            List<AccessMode> accessModes = release.getMarks().stream().map(AccessMode::accessModeValue).filter(Objects::nonNull).collect(Collectors.toList());
            if (accessModes.isEmpty()) {
                return; // not published - nothing to do in any case
            }

            BeanContext beanContext = new BeanContext.Service(release.getReleaseRoot().getResourceResolver());
            Site site = siteManager.getContainingSite(beanContext, release.getReleaseRoot());
            if (!Site.PUBLIC_MODE_IN_PLACE.equals(site.getPublicMode())) {
                return; // this is just for inPlace replication
            }
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

    protected class InPlaceReleasePublishingProcess implements ReleaseChangeProcess {
        @Nonnull
        protected volatile String releaseRootPath;
        @Nonnull
        protected volatile String releaseNodePath;
        protected volatile MessageContainer messages = new MessageContainer(LOG);

        protected final Object changedPathsChangeLock = new Object();
        @Nonnull
        protected volatile Set<String> changedPaths = new LinkedHashSet<>();
        protected volatile String releaseUuid;
        protected volatile ReleaseChangeProcessorState state = idle;
        protected volatile Long finished;
        protected volatile Long startedAt;
        protected volatile Thread runningThread;

        @Override
        public String getId() {
            return releaseNodePath;
        }

        @Override
        public String getName() {
            // FIXME(hps,27.01.20) use better name!
            return getId();
        }

        @Override
        public String getDescription() {
            return "In-place replication";
        }

        @Override
        public void triggerProcessing(@Nonnull ReleaseChangeEvent event) {
            LOG.error("InPlaceReleasePublishingProcess.triggerProcessing");
            if (0 == 0) {
                throw new UnsupportedOperationException("Not implemented yet: InPlaceReleasePublishingProcess.triggerProcessing");
            }
            // FIXME hps 27.01.20 implement InPlaceReleasePublishingProcess.triggerProcessing

        }

        @Nonnull
        @Override
        public ReleaseChangeProcessorState getState() {
            return state;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public boolean isActive() {
            return true;
        }

        @Override
        public int getCompletionPercentage() {
            return completionPercentage;
        }

        @Nullable
        @Override
        public Long getRunStartedAt() {
            return startedAt;
        }

        @Nullable
        @Override
        public Long getRunFinished() {
            return finished;
        }

        @Nonnull
        @Override
        public MessageContainer getMessages() {
            return messages;
        }

        @Nullable
        @Override
        public Long getLastReplicationTimestamp() {
            xxx;
        }

        @Nullable
        @Override
        public Boolean isSynchronized(@Nonnull ResourceResolver resolver) {
            xxx;
        }

        @Override
        public void updateSynchronized() {
            xxx;
        }

        @Override
        public void run() {
            xxx;
        }

        @Nullable
        @Override
        public ReleaseChangeEventPublisher.CompareResult compareTree(@Nonnull ResourceHandle resource, boolean returnDetails) throws ReplicationFailedException {
            xxx;
        }
    }
}
