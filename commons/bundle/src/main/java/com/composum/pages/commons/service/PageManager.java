package com.composum.pages.commons.service;

import com.composum.pages.commons.model.Model;
import com.composum.pages.commons.model.Page;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public interface PageManager {

    /**
     * A list of Pages with unique names to collect all templates available in the resolvers search paths.
     */
    class UniquePageList extends ArrayList<Page> {

        @Override
        public boolean add(Page page) {
            return !containsPageByName(page) && super.add(page);
        }

        @Override
        public boolean addAll(Collection<? extends Page> pages) {
            boolean result = false;
            for (Page page : pages) {
                result = add(page) || result;
            }
            return result;
        }

        public boolean containsPageByName(Page page) {
            for (Page element : this) {
                if (element.getName().equals(page.getName())) {
                    return true;
                }
            }
            return false;
        }

        public void sort() {
            Collections.sort(this); // the Page is a Comparable
        }
    }

    Page createBean(BeanContext context, Resource resource);

    Page getContainingPage(Model element);

    Resource getContainingPageResource(Resource resource);

    /**
     * Determines all usable page templates in the context of a specific tenant.
     * This implementation uses the resolvers search path to find all page resources in a tenants application context.
     * Overlayed page resources are not in the result.
     *
     * @param context the current request context
     * @param tenant  the tenants name (key); maybe &gt;null&lt; if tenants are not supported
     * @return the collection of all page templates found (not &gt;null&lt;)
     */
    Collection<Page> getPageTemplates(@Nonnull BeanContext context, @Nullable String tenant);

    Page createPage(BeanContext context, Resource parent, String pageName, String pageType, boolean commit)
            throws RepositoryException, PersistenceException;

    Page createPage(@Nonnull BeanContext context, @Nonnull Resource parent, @Nonnull String pageName,
                    @Nullable String pageTitle, @Nullable String description,
                    @Nonnull Resource pageTemplate, boolean commit)
            throws RepositoryException, PersistenceException;

    boolean deletePage(@Nonnull BeanContext context, @Nonnull String pagePath, boolean commit)
            throws PersistenceException;

    boolean deletePage(@Nonnull BeanContext context, @Nullable Resource pageResource, boolean commit)
            throws PersistenceException;
}
