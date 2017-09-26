package com.composum.pages.commons.service;

import com.composum.pages.commons.model.Model;
import com.composum.pages.commons.model.Page;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;

import javax.jcr.RepositoryException;

public interface PageManager {

    Page createBean(BeanContext context, Resource resource);

    Page getContainingPage(Model element);

    Resource getContainingPageResource(Resource resource);

    Page createPage(BeanContext context, Resource parent, String pageName, String pageType)
            throws RepositoryException, PersistenceException;

    Page createPage(BeanContext context, Resource parent, String pageName, Resource pageTemplate)
            throws RepositoryException, PersistenceException;
}
