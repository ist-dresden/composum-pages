package com.composum.pages.commons.service;

import com.composum.pages.commons.model.Model;
import com.composum.pages.commons.model.Site;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;

import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface SiteManager {

    /**
     * A list of Sites with unique names to collect all templates available in the resolvers search paths.
     */
    class UniqueSiteList extends ArrayList<Site> {

        @Override
        public boolean add(Site site) {
            return !containsSiteByName(site) && super.add(site);
        }

        @Override
        public boolean addAll(Collection<? extends Site> sites) {
            boolean result = false;
            for (Site site : sites) {
                result = add(site) || result;
            }
            return result;
        }

        public boolean containsSiteByName(Site site) {
            for (Site element : this) {
                if (element.getName().equals(site.getName())) {
                    return true;
                }
            }
            return false;
        }

        public void sort() {
            Collections.sort(this); // the Site is a Comparable
        }
    }

    Site createBean(BeanContext context, Resource resource);

    Site getContainingSite(Model element);

    Site getContainingSite(BeanContext context, Resource resource);

    Resource getContainingSiteResource(Resource resource);

    Resource getSiteBase(BeanContext context, String tenant)
            throws PersistenceException;

    List<Site> getSites(BeanContext context, Resource siteBase);

    List<Site> getSites(BeanContext context, String tenant);

    List<Site> getSiteTemplates(BeanContext context, String tenant);

    Site createSite(BeanContext context, String tenant, String siteName, String homepageType)
            throws RepositoryException, PersistenceException;

    Site createSite(BeanContext context, Resource siteBase, String siteName, String homepageType)
            throws RepositoryException, PersistenceException;

    Site createSite(BeanContext context, Resource siteBase, String siteName, Resource siteTemplate)
            throws RepositoryException, PersistenceException;
}
