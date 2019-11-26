package com.composum.pages.commons.service;

import com.composum.pages.commons.PagesConstants.ReferenceType;
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
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

public interface PageManager extends ContentManager<Page> {

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

    /**
     * Constructs a Page model for a path if the path points to a page or page content
     *
     * @param absPath a page resource or a child of a page resource
     */
    @Nullable
    Page getPage(@Nonnull BeanContext context, @Nonnull String absPath);

    /**
     * Constructs a Page model from the resource - if the resource not a page, but below a page, the page is looked up.
     *
     * @param resource a page resource or a child of a page resource
     */
    @Nonnull
    <T extends Page> T createBean(@Nonnull BeanContext context, @Nonnull Resource resource, Class<T> type);

    @Nonnull
    default Page createBean(@Nonnull BeanContext context, @Nonnull Resource resource) {
        return createBean(context, resource, Page.class);
    }

    @Nullable
    Page getContainingPage(Model element);

    @Nullable
    Page getContainingPage(@Nonnull BeanContext context, @Nullable Resource resource);

    /**
     * Finds the next higher parent of resource (including resource itself) that is a page respource. @see {@link Page#isPage(Resource)}
     */
    @Nullable
    Resource getContainingPageResource(@Nonnull Resource resource);

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

    /**
     * touches (adjusts 'jcr:lastModified') the containing page of a resource (e.g. during changes);
     * if 'time' is null the current time is used
     */
    void touch(@Nonnull BeanContext context, @Nonnull Resource resource, @Nullable Calendar time);

    void touch(@Nonnull BeanContext context, @Nonnull Page page, @Nullable Calendar time);

    /* Touches (adjusts 'jcr:lastModified') the containing pages of the given resources. */
    void touch(@Nonnull BeanContext context, @Nonnull Collection<Resource> resources, @Nullable Calendar time);

    /**
     * retrieve the collection of referrers of a content page
     *
     * @param page       the page
     * @param searchRoot the root in the repository for searching referrers
     * @param resolved   if 'true' only active references (in the same release) are determined
     * @return the collection of found resources
     */
    @Nonnull
    Collection<Resource> getReferrers(@Nonnull Page page, @Nonnull Resource searchRoot, boolean resolved);

    /**
     * the iterator to walk through the target resources of the content elements referenced by the page
     *
     * @param page       the page
     * @param type       the type of references; if 'null' all types are retrieved
     * @param unresolved if 'true' only unresolved references (not in the same release) are determined
     * @param transitive if 'true' we also look for references of the references
     * @return the found resources
     */
    @Nonnull
    Collection<Resource> getReferences(@Nonnull Page page, @Nullable ReferenceType type, boolean unresolved, boolean transitive);
}
