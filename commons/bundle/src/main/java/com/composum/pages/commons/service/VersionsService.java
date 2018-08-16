package com.composum.pages.commons.service;

import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.Release;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

import javax.jcr.RepositoryException;
import java.util.Collection;

/**
 *
 */
public interface VersionsService {

    boolean isModified(Page page);

    void setVersionLabel(BeanContext context, String path, String versionName, String label)
            throws RepositoryException;

    void removeVersionLabel(BeanContext context, String path, String label)
            throws RepositoryException;

    void restoreVersion(BeanContext context, String path, String versionName)
            throws RepositoryException;

    Collection<Page> findModifiedPages(BeanContext context, Resource root);

    Collection<Page> findUnreleasedPages(BeanContext context, Resource root, Release release)
            throws RepositoryException;
}
