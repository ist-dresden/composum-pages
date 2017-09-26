package com.composum.pages.commons.service;

import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.RepositoryException;

/**
 *
 */
public interface VersionsService {

    void setVersionLabel(ResourceResolver resolver, String path, String versionName, String label)
            throws RepositoryException;

    void removeVersionLabel(ResourceResolver resolver, String path, String label)
            throws RepositoryException;

    void restoreVersion(ResourceResolver resolver, String path, String versionName)
            throws RepositoryException;
}
