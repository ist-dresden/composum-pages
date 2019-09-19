package com.composum.pages.commons.service;

import com.composum.pages.commons.model.PageVersion;
import com.composum.pages.commons.model.SiteRelease;
import com.composum.sling.core.BeanContext;
import com.composum.sling.platform.staging.versions.PlatformVersionsService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.RepositoryException;
import java.util.List;

/**
 *
 */
public interface VersionsService {

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
                                         @Nullable List<PlatformVersionsService.ActivationState> filter)
            throws RepositoryException;

    /**
     * Returns all pages that are modified wrt. the current release (that is, either not there, have a new version
     * that isn't in the current release, are modified wrt. the last version, or have been moved or deleted),
     * ordered by path.
     */
    @Nonnull
    List<PageVersion> findModifiedPages(@Nonnull BeanContext context, @Nullable SiteRelease siteRelease,
                                        @Nullable List<PlatformVersionsService.ActivationState> filter)
            throws RepositoryException;

}
