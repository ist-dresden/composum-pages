package com.composum.pages.commons.service;

import com.composum.pages.commons.model.PageVersion;
import com.composum.pages.commons.model.SiteRelease;
import com.composum.sling.core.BeanContext;
import com.composum.sling.platform.staging.versions.PlatformVersionsService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public interface VersionsService {

    /**
     * a filter interface for the version search operations
     */
    interface PageVersionFilter {
        boolean accept(PageVersion version);
    }

    /**
     * a filter implementation using a set of activation state options
     */
    class ActivationStateFilter implements PageVersionFilter {

        private final List<PlatformVersionsService.ActivationState> options;

        public ActivationStateFilter(PlatformVersionsService.ActivationState... options) {
            this.options = new ArrayList<>();
            addOption(options);
        }

        public void addOption(PlatformVersionsService.ActivationState... options) {
            this.options.addAll(Arrays.asList(options));
        }

        @Override
        public boolean accept(PageVersion version) {
            return options.contains(version.getPageActivationState());
        }
    }

    /**
     * Resets a versionable to a version, but deletes all versions after that version.
     */
    void rollbackVersion(BeanContext context, String path, String versionName)
            throws RepositoryException;

    /**
     * Returns a collection of all versionables which are changed in a release in comparision to the release before,
     * ordered by path.
     */
    @Nonnull
    List<PageVersion> findReleaseChanges(@Nonnull BeanContext context,
                                         @Nullable SiteRelease release,
                                         @Nullable PageVersionFilter filter)
            throws RepositoryException;

    /**
     * Returns all pages that are modified wrt. the current release (that is, either not there, have a new version
     * that isn't in the current release, are modified wrt. the last version, or have been moved or deleted),
     * ordered by path.
     */
    @Nonnull
    List<PageVersion> findModifiedPages(@Nonnull BeanContext context, @Nullable SiteRelease siteRelease,
                                        @Nullable PageVersionFilter filter)
            throws RepositoryException;

    /**
     * Retrieves a historical version of a versionable / a resource within that versionable.
     *
     * @param path        path according to the workspace location of a page, may reach into the page
     * @param versionUuid the uuid of a historical version of a page
     * @return the resource ( {@link com.composum.sling.platform.staging.impl.StagingResource} ) as it was at the
     * checkin for version {versionUuid}, or null if there was no corresponding resource in the page at that time
     * or if the path doesn't resolve into the version {versionUuid} (e.g. is outside of the versionable)
     * @see com.composum.sling.platform.staging.impl.StagingResource
     * @see com.composum.sling.platform.staging.impl.VersionSelectResourceResolver
     */
    Resource historicalVersion(@Nonnull ResourceResolver resolver, @Nonnull String path,
                               @Nonnull String versionUuid) throws RepositoryException;

}
