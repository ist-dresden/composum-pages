package com.composum.pages.commons.service;

import com.composum.pages.commons.model.PageVersion;
import com.composum.pages.commons.model.SiteRelease;
import com.composum.platform.commons.util.ExceptionThrowingFunction;
import com.composum.sling.core.BeanContext;
import com.composum.sling.platform.staging.StagingReleaseManager;
import com.composum.sling.platform.staging.versions.PlatformVersionsService;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.sling.api.SlingHttpServletRequest;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;
import javax.jcr.version.VersionManager;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Component(
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Versions Service"
        }
)
public class PagesVersionsService implements VersionsService {

    private static final Logger LOG = LoggerFactory.getLogger(PagesVersionsService.class);

    @Reference
    protected PageManager pageManager;

    @Reference
    protected StagingReleaseManager releaseManager;

    @Reference
    protected PlatformVersionsService platformVersionsService;

    @Override
    public void rollbackVersion(final BeanContext context, String path, String versionName)
            throws RepositoryException {
        VersionManager manager = getVersionManager(context);
        if (LOG.isInfoEnabled()) {
            LOG.info("rollbackVersion({},{})", path, versionName);
        }
        manager.restore(path, versionName, false);
        // Unfortunately, the VersionManager does not offer any way to copy out an old version, and if we just
        // restore an old version, we'll another branch when checking in again, which would be ... inconvenient. ...
        VersionHistory history = manager.getVersionHistory(path);
        final VersionIterator allVersions = history.getAllVersions();
        while (allVersions.hasNext()) {
            final Version version = allVersions.nextVersion();
            if (version.getName().equals(versionName)) {
                break;
            }
        }
        while (allVersions.hasNext()) {
            final Version version = allVersions.nextVersion();
            if (LOG.isDebugEnabled()) {
                LOG.debug("rollbackVersion.remove({},{})", path, version.getName());
            }
            history.removeVersion(version.getName());
        }
        manager.checkout(path);
    }

    /**
     * @return a collection of all versionables which are changed in a release in comparision to the release before
     */
    @Nonnull
    @Override
    public List<PageVersion> findReleaseChanges(@Nonnull final BeanContext context,
                                                @Nullable final SiteRelease siteRelease,
                                                @Nullable final List<PlatformVersionsService.ActivationState> filter)
            throws RepositoryException {
        ExceptionThrowingFunction<StagingReleaseManager.Release, List<PlatformVersionsService.Status>, RepositoryException> getChanges =
                platformVersionsService::findReleaseChanges;
        return findPageVersions(siteRelease, getChanges, filter);
    }

    @Override
    @Nonnull
    public List<PageVersion> findModifiedPages(@Nonnull final BeanContext context, final SiteRelease siteRelease,
                                               @Nullable final List<PlatformVersionsService.ActivationState> filter)
            throws RepositoryException {
        ExceptionThrowingFunction<StagingReleaseManager.Release, List<PlatformVersionsService.Status>, RepositoryException> getChanges =
                platformVersionsService::findWorkspaceChanges;
        return findPageVersions(siteRelease, getChanges, filter);
    }

    @Nonnull
    protected List<PageVersion> findPageVersions(@Nullable SiteRelease siteRelease,
                                                 @Nonnull ExceptionThrowingFunction<StagingReleaseManager.Release, List<PlatformVersionsService.Status>, RepositoryException> getChanges,
                                                 @Nullable final List<PlatformVersionsService.ActivationState> filter)
            throws RepositoryException {
        List<PageVersion> result = new ArrayList<>();
        if (siteRelease != null) {
            StagingReleaseManager.Release release = siteRelease.getStagingRelease();
            List<PlatformVersionsService.Status> changes = getChanges.apply(release);
            for (PlatformVersionsService.Status status : changes) {
                PageVersion pv = new PageVersion(siteRelease, status);
                if (filter == null || filter.size() < 1 || filter.contains(pv.getPageActivationState())) {
                    result.add(pv);
                }
            }
            result.sort(Comparator.comparing(PageVersion::getPath));
        }
        return result;
    }

    protected VersionManager getVersionManager(final BeanContext context)
            throws RepositoryException {
        SlingHttpServletRequest request = context.getRequest();
        VersionManager versionManager = (VersionManager) request.getAttribute(VersionManager.class.getName());
        if (versionManager == null) {
            final JackrabbitSession session = Objects.requireNonNull(
                    (JackrabbitSession) context.getResolver().adaptTo(Session.class));
            versionManager = session.getWorkspace().getVersionManager();
            request.setAttribute(VersionManager.class.getName(), versionManager);
        }
        return versionManager;
    }

}
