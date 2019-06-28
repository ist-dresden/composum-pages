package com.composum.pages.commons.service;

import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.SiteRelease;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.RepositoryException;
import java.util.Collection;

/**
 *
 */
public interface VersionsService {

    boolean isModified(Page page);

    void restoreVersion(BeanContext context, String path, String versionName)
            throws RepositoryException;

    /**
     * @return a collection of all versionables which are changed in a release in comparision to the release before
     */
    Collection<Page> findReleaseChanges(@Nonnull BeanContext context, @Nonnull Resource root,
                                        @Nullable SiteRelease release) throws RepositoryException;

    /**
     * Returns all pages that are modified wrt. the current release (that is, either not there, have a new version
     * that isn't in the current release or are modified wrt. the last version).
     */
    Collection<Page> findModifiedPages(BeanContext context, Resource root);

}
