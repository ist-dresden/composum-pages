package com.composum.pages.commons.service;

import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.SiteRelease;
import com.composum.sling.core.BeanContext;
import com.composum.sling.platform.staging.ReleasedVersionable;
import com.composum.sling.platform.staging.StagingReleaseManager;
import com.composum.sling.platform.staging.versions.PlatformVersionsService;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.NonExistingResource;
import org.apache.sling.api.resource.Resource;
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
import java.util.Collection;
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
    public boolean isModified(final Page page) {
        try {
            if (page.getContent() == null || page.getContent().getResource() == null)
                return false;
            PlatformVersionsService.Status status = platformVersionsService.getStatus(page.getContent().getResource(), null);
            if (status == null)
                return false;
            switch (status.getActivationState()) {
                case activated:
                case deactivated:
                    return false;
                case initial:
                case modified:
                    return true;
            }
            throw new IllegalStateException("Unknown state " + status.getActivationState()); // impossible
        } catch (RepositoryException e) {
            LOG.error("Unexpected error", e);
            return false;
        }
    }

    @Override
    public void restoreVersion(final BeanContext context, String path, String versionName)
            throws RepositoryException {
        VersionManager manager = getVersionManager(context);
        if (LOG.isInfoEnabled()) {
            LOG.info("restoreVersion(" + path + "," + versionName + ")");
        }
        manager.restore(path, versionName, false);
        // TODO(hps,2019-05-20) removing everything that came later is wrong from a users perspective.
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
                LOG.debug("restoreVersion.remove(" + path + "," + version.getName() + ")");
            }
            history.removeVersion(version.getName());
        }
        manager.checkout(path);
    }

    /**
     * @return a collection of all versionables which are changed in a release in comparision to the release before
     */
    @Override
    public Collection<Page> findReleaseChanges(@Nonnull final BeanContext context,
                                               @Nullable final SiteRelease siteRelease) throws RepositoryException {
        List<Page> result = new ArrayList<>();
        if (siteRelease != null) {
            StagingReleaseManager.Release release = siteRelease.getStagingRelease();
            List<ReleasedVersionable> changes = releaseManager.compareReleases(release, null);
            for (ReleasedVersionable releasedVersionable : changes) {
                Resource versionable = siteRelease.getResource().getChild(releasedVersionable.getRelativePath());
                if (Page.isPageContent(versionable)) {
                    final Page page = pageManager.getContainingPage(context, versionable);
                    result.add(page);
                }
                // FIXME(hps,2019-06-28) what should happen if the page doesn't exist anymore? Page on NonExistingResource?
            }
            result.sort(Comparator.comparing(Page::getPath));
        }
        return result;
    }

    @Override
    public Collection<Page> findModifiedPages(final BeanContext context, final Resource root) {
        List<Page> result = new ArrayList<>();
        findModifiedPages(context, root, result);
        result.sort(Comparator.comparing(Page::getPath));
        return result;
    }

    protected void findModifiedPages(final BeanContext context, final Resource parent,
                                     List<Page> result) {
        final Iterable<Resource> children = parent.getChildren();
        for (Resource resource : children) {
            if (Page.isPage(resource)) {
                final Page page = pageManager.createBean(context, resource);
                if (isModified(page)) {
                    result.add(page);
                }
                findModifiedPages(context, page.getResource(), result);
            } else { // folders
                findModifiedPages(context, resource, result);
            }
        }
    }

    public VersionManager getVersionManager(final BeanContext context)
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

    public VersionHistory getVersionHistory(final BeanContext context, final String path)
            throws RepositoryException {
        return getVersionManager(context).getVersionHistory(path);
    }

    protected class NoSiteRelease extends SiteRelease {

        NoSiteRelease(BeanContext context, String path) {
            super(context, new NonExistingResource(context.getResolver(), path));
        }

        @Override
        public String getLabel() {
            //no existing label
            return "w81t5l6pSYAeeN5c";
        }
    }
}
