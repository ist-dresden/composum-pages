package com.composum.pages.commons.replication;

import com.composum.pages.commons.filter.SitePageFilter;
import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.service.SiteManager;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.logging.Message;
import com.composum.sling.core.logging.MessageContainer;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.core.util.SlingResourceUtil;
import com.composum.sling.platform.security.AccessMode;
import com.composum.sling.platform.staging.ReleaseChangeEventListener;
import com.composum.sling.platform.staging.ReleaseChangeEventPublisher;
import com.composum.sling.platform.staging.ReleaseChangeProcess;
import com.composum.sling.platform.staging.StagingConstants;
import com.composum.sling.platform.staging.StagingReleaseManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.composum.sling.platform.staging.ReleaseChangeProcess.ReleaseChangeProcessorState.awaiting;
import static com.composum.sling.platform.staging.ReleaseChangeProcess.ReleaseChangeProcessorState.error;
import static com.composum.sling.platform.staging.ReleaseChangeProcess.ReleaseChangeProcessorState.idle;
import static com.composum.sling.platform.staging.ReleaseChangeProcess.ReleaseChangeProcessorState.processing;
import static com.composum.sling.platform.staging.ReleaseChangeProcess.ReleaseChangeProcessorState.success;
import static java.util.Objects.requireNonNull;

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

    @Reference
    protected ResourceResolverFactory resolverFactory;

    private List<String> removeRedundantPaths(Collection<String> pathsToReplicate) {
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

    /** Maps the {@link StagingReleaseManager.Release#getPath()} and one accessmode to the corresponding process. */
    // FIXME(hps,27.01.20) cleanup how?
    protected final Map<Pair<String, AccessMode>, InPlaceReleasePublishingProcess> processesCache =
            Collections.synchronizedMap(new HashMap<>());


    @Nonnull
    @Override
    public Collection<InPlaceReleasePublishingProcess> processesFor(@Nullable Resource resource) {
        if (resource == null) { return Collections.emptyList(); }
        Collection<InPlaceReleasePublishingProcess> result = new ArrayList<>();
        Resource releaseRoot;
        try {
            releaseRoot = releaseManager.findReleaseRoot(resource);
        } catch (StagingReleaseManager.ReleaseNotFoundException e) {
            LOG.debug("Not within a release root: {}", SlingResourceUtil.getPath(resource));
            return Collections.emptyList();
        }
        BeanContext beanContext = new BeanContext.Service(resource.getResourceResolver());
        Site site = siteManager.getContainingSite(beanContext, releaseRoot);
        if (!Site.PUBLIC_MODE_IN_PLACE.equals(site.getPublicMode())) {
            return Collections.emptyList(); // this is just for inPlace replication
        }

        for (AccessMode accessMode : Arrays.asList(AccessMode.PUBLIC, AccessMode.PREVIEW)) {
            StagingReleaseManager.Release release = releaseManager.findReleaseByMark(releaseRoot, accessMode.name().toLowerCase());
            if (release == null) { continue; }
            InPlaceReleasePublishingProcess process =
                    processesCache.computeIfAbsent(Pair.of(release.getPath(), accessMode), k ->
                            new InPlaceReleasePublishingProcess().updateConfig(release, accessMode)
                    );
            result.add(process);
        }
        return result;
    }

    /** Creates the service resolver used to update the content. */
    @Nonnull
    protected ResourceResolver makeResolver() throws LoginException {
        return resolverFactory.getServiceResourceResolver(null);
    }

    protected class InPlaceReleasePublishingProcess implements ReleaseChangeProcess {
        @Nonnull
        protected volatile String releaseRootPath;
        @Nonnull
        protected volatile String releaseNodePath;
        @Nonnull
        protected volatile AccessMode accessMode;

        protected volatile MessageContainer messages = new MessageContainer(LOG);
        /** Lock object whenever changes need to be serialized, and when changing {@link #changedPaths}. */
        protected final Object changeLock = new Object();
        @Nonnull
        protected volatile Set<String> changedPaths = new LinkedHashSet<>();
        protected volatile ReleaseChangeProcessorState state = idle;
        protected volatile Long finished;
        protected volatile Long startedAt;
        protected volatile int completionPercentage;
        /** Always contains the thread {@link #run()} is running in, if it is. */
        protected volatile Thread runningThread;
        protected volatile boolean abortAtNextPossibility = false;
        protected volatile boolean rescheduleNeeded = false;

        @Override
        public void run() {
            synchronized (changeLock) {
                if (changedPaths.isEmpty()) {
                    LOG.info("Nothing to do for {}", getId());
                    return;
                }
            }
            Set<String> pathsToSynchronize = null;
            try {
                rescheduleNeeded = false;
                runningThread = Thread.currentThread();
                abortAtNextPossibility = false;
                completionPercentage = 0;
                startedAt = System.currentTimeMillis();
                synchronized (changeLock) {
                    LOG.info("Starting run for {} changed paths {}", getId(), changedPaths);
                    pathsToSynchronize = changedPaths;
                    changedPaths = new LinkedHashSet<>();
                }
                state = processing;

                messages.add(Message.info("In-place replication of paths {}", pathsToSynchronize));
                executeReplication(pathsToSynchronize);
                messages.add(Message.info("In-place replication complete"));

                pathsToSynchronize = null;
                completionPercentage = 100;
                state = success;
                LOG.info("Finished run for {}", getId());
            } catch (AbortReplicationButRetryException e) {
                state = awaiting;
                rescheduleNeeded = true;
                completionPercentage = 0;
            } catch (AbortReplicationRequestedException e) {
                state = error;
                completionPercentage = 0;
            } catch (Exception e) {
                LOG.error("Exception replicating {}", getId(), e);
                state = error;
                completionPercentage = 0;
                // we do not automatically set rescheduleNeeded since this might lead to spamming the system.
                // the #executeReplication can set this, however, if it's likely to succeed next time.
            } finally {
                runningThread = null;
                finished = System.currentTimeMillis();
                if (pathsToSynchronize != null && !pathsToSynchronize.isEmpty()) {
                    // add not correctly processed paths back
                    synchronized (changeLock) {
                        pathsToSynchronize.addAll(changedPaths);
                        if (!pathsToSynchronize.isEmpty()) {
                            changedPaths = pathsToSynchronize;
                            rescheduleNeeded = true;
                            state = awaiting;
                        }
                    }
                }
            }
        }

        @Nonnull
        public InPlaceReleasePublishingProcess updateConfig(StagingReleaseManager.Release release, AccessMode newAccessMode) {
            releaseRootPath = release.getReleaseRoot().getPath();
            if (!StringUtils.equals(releaseNodePath, release.getPath()) && runningThread != null) {
                synchronized (changeLock) {
                    rescheduleNeeded = true;
                    abort(true); // change of release!
                }
            }
            releaseNodePath = release.getPath();
            this.accessMode = newAccessMode;
            return this;
        }

        @Override
        public String getId() {
            return releaseNodePath + ":" + accessMode;
        }

        @Override
        public String getName() {
            return accessMode.name().toLowerCase() + " : " + releaseRootPath;
        }

        @Override
        public String getDescription() {
            return "In-place replication";
        }

        @Override
        public void triggerProcessing(@Nonnull ReleaseChangeEvent event) {
            synchronized (changeLock) {
                changedPaths.addAll(event.newOrMovedResources());
                changedPaths.addAll(event.updatedResources());
                if (!event.removedOrMovedResources().isEmpty()) {
                    try (ResourceResolver stagedResolver =
                                 releaseManager.getResolverForRelease(event.release(), replicationManager, false)) {
                        for (String removedPath : event.removedOrMovedResources()) {
                            // for removed resources find next higher still existing parent and replicate that.
                            Resource resource = stagedResolver.getResource(removedPath);
                            while (resource == null && StringUtils.isNotBlank(removedPath)) {
                                removedPath = ResourceUtil.getParent(removedPath);
                                resource = stagedResolver.getResource(removedPath);
                            }
                            if (resource != null) { changedPaths.add(removedPath); }
                        }
                    }
                }
                if (!changedPaths.isEmpty() && state != processing) {
                    state = awaiting;
                }
            }
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
            return null; // FIXME(hps,27.01.20) implement
        }

        @Nullable
        @Override
        public Boolean isSynchronized(@Nonnull ResourceResolver resolver) {
            Resource releaseRoot = resolver.getResource(this.releaseRootPath);
            StagingReleaseManager.Release release = releaseManager.findReleaseByMark(releaseRoot, accessMode.name().toLowerCase());
            String newReleaseChangeId = release.getChangeNumber();
            String replicatedRootPath = replicationManager.getReplicationPath(accessMode, releaseRootPath);
            Resource replicatedRoot = resolver.getResource(replicatedRootPath);
            String replicatedReleaseChangeId = replicatedRoot != null ?
                    replicatedRoot.getValueMap().get(StagingConstants.PROP_REPLICATED_VERSION, String.class) : null;
            return StringUtils.equals(newReleaseChangeId, replicatedReleaseChangeId);
        }

        @Override
        public void updateSynchronized() {
            // empty - nothing to do here since it's calculated freshly on each access.
        }

        protected void executeReplication(Set<String> pathsToSynchronize) throws Exception {
            try (ResourceResolver resolver = makeResolver()) {
                BeanContext beanContext = new BeanContext.Service(resolver);
                Resource releaseRoot = resolver.getResource(this.releaseRootPath);
                StagingReleaseManager.Release release = releaseManager.findReleaseByMark(releaseRoot, accessMode.name().toLowerCase());
                String newReleaseChangeId = release.getChangeNumber();
                Site site = siteManager.getContainingSite(beanContext, release.getReleaseRoot());
                if (!Site.PUBLIC_MODE_IN_PLACE.equals(site.getPublicMode())) { return; }
                ResourceFilter releaseFilter = new SitePageFilter(site.getPath(), ResourceFilter.ALL);
                ResourceResolver stagedResolver = releaseManager.getResolverForRelease(release, replicationManager, false);

                List<String> pathsToReplicate = removeRedundantPaths(pathsToSynchronize);

                ReplicationContext replicationContext = new ReplicationContext(beanContext, site, accessMode, releaseFilter, stagedResolver);

                int count = 0;
                for (String path : pathsToReplicate) {
                    abortIfNecessary(newReleaseChangeId);
                    Resource resource = stagedResolver.getResource(path);
                    if (resource != null) {
                        replicationManager.replicateResource(replicationContext, resource, true);
                    } else {
                        LOG.warn("Could not find replicated resource {} from {}", path, getId());
                    }
                    count += 0;
                    completionPercentage = (int) (count * 80.0 / pathsToReplicate.size());
                }

                abortIfNecessary(newReleaseChangeId);
                messages.add(Message.debug("References to rewrite: {}", replicationContext.references));
                replicationManager.replicateReferences(replicationContext);
                messages.add(Message.debug("Replication done for: {}", replicationContext.done));
                messages.add(Message.debug("References rewritten: {}", replicationContext.references));

                ModifiableValueMap releaseRootVm = requireNonNull(releaseRoot.adaptTo(ModifiableValueMap.class));
                releaseRootVm.put(StagingConstants.PROP_LAST_REPLICATION_DATE, Calendar.getInstance());
                releaseRootVm.put(StagingConstants.PROP_CHANGE_NUMBER, newReleaseChangeId);

                abortIfNecessary(newReleaseChangeId);
                resolver.commit();
                messages.add(Message.debug("New release change number for {} : {}", release.getPath(), newReleaseChangeId));
            }
        }

        /** Aborts if there was a change of the release number. */
        protected void abortIfNecessary(@Nonnull String newReleaseChangeId) throws AbortReplicationRequestedException, LoginException, AbortReplicationButRetryException {
            if (abortAtNextPossibility) {
                messages.add(Message.warn("Aborting because that was requested: {}", getId()));
                throw new AbortReplicationRequestedException();
            }
            try (ResourceResolver resolver = makeResolver()) {
                Resource releaseRoot = resolver.getResource(this.releaseRootPath);
                StagingReleaseManager.Release release = releaseManager.findReleaseByMark(releaseRoot, accessMode.name().toLowerCase());
                String currentReleaseChangeId = release.getChangeNumber();
                if (!StringUtils.equals(newReleaseChangeId, currentReleaseChangeId)) {
                    messages.add(Message.warn("Retrying replication since release changed in the meantime. {}",
                            getId()));
                    rescheduleNeeded = true;
                    throw new AbortReplicationButRetryException();
                }
            }
        }

        @Nullable
        @Override
        public ReleaseChangeEventPublisher.CompareResult compareTree(@Nonnull ResourceHandle resource, boolean returnDetails) throws ReplicationFailedException {
            // FIXME(hps,28.01.20) implement this
            ReleaseChangeEventPublisher.CompareResult result = new ReleaseChangeEventPublisher.CompareResult();
            result.equal = isSynchronized(resource.getResourceResolver());
            result.changedParentNodes = new String[]{"NOT IMPLEMENTED YET"};
            return result;
        }

        /**
         * Sets a mark that leads to aborting the process at the next step - if an outside interruption is necessary
         * for some reason. If {hard} is true, we also send the running thread an {@link Thread#interrupt()}.
         */
        protected boolean abort(boolean hard) {
            Thread runningThreadCopy = runningThread;
            synchronized (changeLock) {
                if (runningThreadCopy != null) {
                    messages.add(Message.info("Abort requested"));
                    abortAtNextPossibility = true;
                    if (hard) {
                        runningThreadCopy.interrupt();
                    }
                }
            }
            return runningThreadCopy != null;
        }

    }

    /** Internally thrown if the replication has to be aborted but should be retried. */
    protected static class AbortReplicationButRetryException extends Exception {
        // empty
    }

    /** Internally thrown if an abort of the replication was requested. */
    protected static class AbortReplicationRequestedException extends Exception {
        // empty
    }
}
