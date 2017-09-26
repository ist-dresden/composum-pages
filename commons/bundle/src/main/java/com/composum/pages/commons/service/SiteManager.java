package com.composum.pages.commons.service;

import com.composum.pages.commons.model.Model;
import com.composum.pages.commons.model.Site;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;

import javax.jcr.RepositoryException;

public interface SiteManager {

    Site createBean(BeanContext context, Resource resource);

    Site getContainingSite(Model element);

    Site getContainingSite(BeanContext context, Resource resource);

    Resource getContainingSiteResource(Resource resource);

    Resource getSiteBase(BeanContext context, String tenant)
            throws PersistenceException;

    Site createSite(BeanContext context, String tenant, String siteName, String homepageType)
            throws RepositoryException, PersistenceException;

    Site createSite(BeanContext context, Resource siteBase, String siteName, String homepageType)
            throws RepositoryException, PersistenceException;

    Site createSite(BeanContext context, Resource siteBase, String siteName, Resource siteTemplate)
            throws RepositoryException, PersistenceException;
}
