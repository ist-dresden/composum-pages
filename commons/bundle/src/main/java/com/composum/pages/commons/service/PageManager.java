package com.composum.pages.commons.service;

import com.composum.pages.commons.model.Model;
import com.composum.pages.commons.model.Page;
import com.composum.sling.core.BeanContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

public interface PageManager extends ResourceManager<Page> {

    /**
     * A list of Pages with unique names to collect all templates available in the resolvers search paths.
     */
    class PageTemplateList extends ArrayList<Page> {

        protected final ResourceResolver resolver;
        protected final Pattern pathPattern;

        public PageTemplateList(ResourceResolver resolver) {
            this.resolver = resolver;
            pathPattern = Pattern.compile("^(" + StringUtils.join(resolver.getSearchPath(), "|") + ")");
        }

        @Override
        public boolean add(Page page) {
            return !containsPageByResolverPath(page) && super.add(page);
        }

        @Override
        public boolean addAll(Collection<? extends Page> pages) {
            boolean result = false;
            for (Page page : pages) {
                result = add(page) || result;
            }
            return result;
        }

        public boolean containsPageByResolverPath(Page page) {
            for (Page element : this) {
                if (pathPattern.matcher(element.getPath()).replaceAll("").equals(
                        pathPattern.matcher(page.getPath()).replaceAll(""))) {
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
     * @param parent  the designated parent page
     * @return the collection of all appropriate page templates found (not &gt;null&lt;)
     */
    Collection<Page> getPageTemplates(@Nonnull BeanContext context, @Nonnull Resource parent);

    @Nonnull
    Page createPage(@Nonnull BeanContext context, @Nonnull Resource parent, @Nonnull String pageType,
                    @Nonnull String pageName, @Nullable String pageTitle, @Nullable String description,
                    boolean commit)
            throws RepositoryException, PersistenceException;

    @Nonnull
    Page createPage(@Nonnull BeanContext context, @Nonnull Resource parent, @Nonnull Resource pageTemplate,
                    @Nonnull String pageName, @Nullable String pageTitle, @Nullable String description,
                    boolean commit)
            throws RepositoryException, PersistenceException;

    boolean deletePage(@Nonnull BeanContext context, @Nonnull String pagePath, boolean commit)
            throws PersistenceException;

    boolean deletePage(@Nonnull BeanContext context, @Nullable Resource pageResource, boolean commit)
            throws PersistenceException;
}
