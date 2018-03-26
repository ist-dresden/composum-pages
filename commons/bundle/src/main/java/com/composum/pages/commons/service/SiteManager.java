package com.composum.pages.commons.service;

import com.composum.pages.commons.model.Model;
import com.composum.pages.commons.model.Site;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public interface SiteManager extends ContentManager<Site> {

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

    /**
     * Adapts an appropriate site resource into a Site bean.
     *
     * @param context  the current request context
     * @param resource the site resource
     * @return the bean/model object
     */
    Site createBean(BeanContext context, Resource resource);

    /**
     * Adapts any model instance into the bean of the containing site.
     *
     * @param element the element bean which containing site has to be used
     * @return the bean/model object; maybe &gt;null&lt; if no parent resource represents a site
     */
    Site getContainingSite(Model element);

    /**
     * Adapts any resource into the bean of the containing site.
     *
     * @param resource the resource which containing site has to be used
     * @return the bean/model object; maybe &gt;null&lt; if no parent resource represents a site
     */
    Site getContainingSite(BeanContext context, Resource resource);

    /**
     * Determines the resource of the containing site of any resource
     *
     * @param resource the resource which containing site is searched
     * @return the resource of the site if found; maybe &gt;null&lt; if no parent resource represents a site
     */
    Resource getContainingSiteResource(Resource resource);

    /**
     * Returns the sites root resource to place new sites for a specific tenant.
     *
     * @param context the current request context
     * @param tenant  the tenants name (key); maybe &gt;null&lt; if tenants are not supported
     * @return the root resource to create site child resources
     * @throws PersistenceException if the root has to be created and an error has occurred during creation
     */
    @Nonnull
    Resource getSiteBase(@Nonnull BeanContext context, @Nullable String tenant)
            throws PersistenceException;

    /**
     * Determines all sites of a specific tenant.
     *
     * @param context the current request context
     * @param tenant  the tenants name (key); maybe &gt;null&lt; if tenants are not supported
     * @return the collection of all sites found (not &gt;null&lt;)
     */
    Collection<Site> getSites(@Nonnull BeanContext context, @Nullable String tenant);

    /**
     * Determines all usable site templates in the context of a specific tenant.
     * This implementation uses the resolvers search path to find all site resources in a tenants application context.
     * Overlayed site resources are not in the result.
     *
     * @param context the current request context
     * @param tenant  the tenants name (key); maybe &gt;null&lt; if tenants are not supported
     * @return the collection of all site templates found (not &gt;null&lt;)
     */
    Collection<Site> getSiteTemplates(@Nonnull BeanContext context, @Nullable String tenant);

    /**
     * Determines all Sites which a children of a search root in any depth of the hierarchy.
     *
     * @param context    the current request context
     * @param searchRoot the search root path
     * @param filter     the filter applied to the result set
     * @return the collection of all sites found (not &gt;null&lt;)
     */
    Collection<Site> getSites(@Nonnull BeanContext context, @Nullable Resource searchRoot, @Nonnull ResourceFilter filter);

    Site createSite(@Nonnull BeanContext context, @Nullable String tenant, @Nonnull String siteName,
                    @Nullable String homepageType, boolean commit)
            throws RepositoryException, PersistenceException;

    Site createSite(@Nonnull BeanContext context, @Nonnull Resource siteBase, @Nonnull String siteName,
                    @Nullable String homepageType, boolean commit)
            throws RepositoryException, PersistenceException;

    Site createSite(@Nonnull BeanContext context, @Nullable String tenant, @Nonnull String siteName,
                    @Nullable String siteTitle, @Nullable String description,
                    @Nullable Resource siteTemplate, boolean commit)
            throws RepositoryException, PersistenceException;

    Site createSite(@Nonnull BeanContext context, @Nonnull Resource siteBase, @Nonnull String siteName,
                    @Nullable String siteTitle, @Nullable String description,
                    @Nullable Resource siteTemplate, boolean commit)
            throws RepositoryException, PersistenceException;

    boolean deleteSite(@Nonnull BeanContext context, @Nonnull String sitePath, boolean commit)
            throws RepositoryException, PersistenceException;

    boolean deleteSite(@Nonnull BeanContext context, @Nullable Resource siteResource, boolean commit)
            throws PersistenceException;
}
