package com.composum.pages.commons.replication;

import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.service.SiteManager;
import com.composum.sling.core.BeanContext;
import com.composum.sling.platform.staging.ReleaseChangeEventListener;
import com.composum.sling.platform.staging.ReleaseChangeEventPublisher;
import com.composum.sling.platform.staging.StagingConstants;
import com.composum.sling.platform.staging.StagingReleaseManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.jetbrains.annotations.NotNull;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Pattern;

/** Watches for replication strategy changes and triggers the appropriate events. */
@Component(
        service = {ResourceChangeListener.class},
        property = {
                Constants.SERVICE_DESCRIPTION + "=Pages Resource Change Listener",
                ResourceChangeListener.CHANGES + "=CHANGED",
                ResourceChangeListener.CHANGES + "=ADDED",
                ResourceChangeListener.PATHS + "=glob:/content/**/jcr:content",
                ResourceChangeListener.PROPERTY_NAMES_HINT + "=" + Site.PROP_PUBLIC_MODE
        }
)
public class PagesChangeEventListener implements ResourceChangeListener {

    private static final Logger LOG = LoggerFactory.getLogger(PagesChangeEventListener.class);

    /** Property on the release node that saves the last {@link Site#PROP_PUBLIC_MODE} to recognize changes. */
    public static final String PROP_LAST_PUBLIC_MODE = "last-publicMode";

    /**
     * Resourced pattern where we might need to retrigger a refresh - see {@link #onChange(List)}. This needs to be consistent
     * with the {@link ResourceChangeListener#PATHS} configuration property of this service.
     */
    private static final Pattern RESOURCE_PATTERN = Pattern.compile("/content/.*/jcr:content");

    @Reference
    protected ReleaseChangeEventPublisher releaseChangeEventPublisher;

    @Reference
    protected SiteManager siteManager;

    @Reference
    protected StagingReleaseManager releaseManager;

    @Reference
    protected ResourceResolverFactory resolverFactory;

    /**
     * We cache a resolver since there can be many events and we'd have to create a service resolver on each jcr:content
     * change otherwise. We synchronize over this in its usages.
     */
    @SuppressWarnings("ThreadSafeField")
    private volatile ResourceResolver cachedServiceResolver;

    private final Object lockobject = new Object();

    /**
     * Implements {@link ResourceChangeListener#onChange(List)} to trigger a replication when a site is newly configured for
     * in place replication via the dialog, since that's done via the normal Sling POST servlet.
     */
    @Override
    public void onChange(@NotNull List<ResourceChange> changes) {
        LOG.debug("Received change event {}", changes);
        // OUCH. We seem to receive way more than the paths we registered for, and we've got to create a service resolver
        // each time we are called. :-(
        for (ResourceChange change : changes) {
            //noinspection EnumSwitchStatementWhichMissesCases
            switch (change.getType()) {
                case ADDED:
                case CHANGED:
                    if (RESOURCE_PATTERN.matcher(change.getPath()).matches()) {
                        LOG.info("Received change event {}", change);
                        try {
                            checkWhetherPublicationIsNeccesary(change);
                        } catch (RuntimeException | LoginException | PersistenceException | ReleaseChangeEventListener.ReplicationFailedException e) {
                            LOG.error("When processing resource change event " + change.toString(), e);
                        }
                    }
            }
        }
    }

    @Activate
    @Modified
    @Deactivate
    public void reset() {
        synchronized (lockobject) {
            ResourceResolver resolver = this.cachedServiceResolver;
            cachedServiceResolver = null;
            if (resolver != null) { resolver.close(); }
        }
    }

    protected ResourceResolver giveServiceResolver() throws LoginException {
        ResourceResolver resolver = this.cachedServiceResolver;
        if (resolver == null) {
            synchronized (lockobject) {
                resolver = this.cachedServiceResolver;
                if (resolver == null) {
                    resolver = resolverFactory.getServiceResourceResolver(null);
                    this.cachedServiceResolver = resolver;
                }
            }
        }
        return resolver;
    }

    /**
     * Checks whether this is a site resource and whether the {@link Site#PROP_PUBLIC_MODE} property was changed to {@link Site#PUBLIC_MODE_IN_PLACE}.
     * If so we trigger a release change event.
     * We save in the property last-publicMode
     */
    private void checkWhetherPublicationIsNeccesary(ResourceChange change) throws LoginException, PersistenceException, ReleaseChangeEventListener.ReplicationFailedException {
        ResourceResolver resolver = giveServiceResolver();
        boolean doUpdate = false;
        String currentlyInPlace = null;
        synchronized (resolver) {
            resolver.revert();
            resolver.refresh();
            Resource resource = resolver.getResource(change.getPath());
            if (resource == null) {
                LOG.error("Cannot find resource {} with resolver {} - permissions wrong?", change.getPath(), resolver.getUserID());
                return;
            }
            if (Site.isSiteConfiguration(resource)) {
                Resource releaseNode = resource.getChild(StagingConstants.NODE_RELEASES);
                if (releaseNode == null) { return; }
                String previouslyInPlace = releaseNode.getValueMap().get(PROP_LAST_PUBLIC_MODE, String.class);

                BeanContext beanContext = new BeanContext.Service(resolver);
                Site site = siteManager.getContainingSite(beanContext, resource.getParent());
                currentlyInPlace = site.getPublicMode();

                if (!StringUtils.equals(previouslyInPlace, currentlyInPlace)) {
                    doUpdate = true;
                }
            }
        }

        if (doUpdate) {
            triggerReleaseEventsIfNeccesary(change.getPath(), currentlyInPlace);

            synchronized (resolver) {
                resolver.revert();
                resolver.refresh();
                Resource releaseNode = resolver.getResource(change.getPath()).getChild(StagingConstants.NODE_RELEASES);
                ModifiableValueMap modVm = releaseNode.adaptTo(ModifiableValueMap.class);
                modVm.put(PROP_LAST_PUBLIC_MODE, currentlyInPlace);
                resolver.commit();
            }
        }
    }

    protected void triggerReleaseEventsIfNeccesary(String path, String currentlyInPlace) throws LoginException, ReleaseChangeEventListener.ReplicationFailedException, PersistenceException {
        // do this on a new resolver to avoid any surprises and synchronize over the cached resolver as short as possible
        try (ResourceResolver resolver = resolverFactory.getServiceResourceResolver(null)) {
            Resource resource = resolver.getResource(path);
            if (resource == null) { return; }
            List<StagingReleaseManager.Release> releases = releaseManager.getReleases(resource);
            for (StagingReleaseManager.Release release : releases) {
                if (!release.getMarks().isEmpty()) {
                    LOG.info("We simulate a full release change event for replication since the " + Site.PROP_PUBLIC_MODE +
                            " property of {} was changed to {}", path, currentlyInPlace);
                    releaseChangeEventPublisher.publishActivation(ReleaseChangeEventListener.ReleaseChangeEvent.fullUpdate(release));
                    resolver.commit();
                    LOG.info("Processing generated full release change event finished for {}", path);
                }
            }
        }
    }

}
