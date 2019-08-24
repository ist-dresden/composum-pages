package com.composum.pages.commons.service;

import com.composum.pages.commons.PagesConfiguration;
import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.PagesConstants.ReferenceType;
import com.composum.pages.commons.filter.TemplateFilter;
import com.composum.pages.commons.model.ContentTypeFilter;
import com.composum.pages.commons.model.Model;
import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.PageContent;
import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.replication.ReplicationManager;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.filter.StringFilter;
import com.composum.sling.core.util.CoreConstants;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.platform.staging.versions.PlatformVersionsService;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.composum.pages.commons.PagesConstants.NODE_TYPE_PAGE;

@Component(
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Page Manager"
        }
)
public class PagesPageManager extends PagesContentManager<Page> implements PageManager {

    public static final String APPS_RESOLVER_ROOT = "/apps/";

    public static final String REF_PATH_ROOT = "/content/";
    public static final Pattern REF_VALUE_PATTERN = Pattern.compile("^" + REF_PATH_ROOT + ".+$");
    public static final Pattern REF_LINK_PATTERN = Pattern.compile("(href|src)=\"(" + REF_PATH_ROOT + "[^\"]+)?\"");

    public static final Map<String, Object> PAGE_PROPERTIES;
    public static final Map<String, Object> PAGE_CONTENT_PROPERTIES;

    static {
        PAGE_PROPERTIES = new HashMap<>();
        PAGE_PROPERTIES.put(JcrConstants.JCR_PRIMARYTYPE, NODE_TYPE_PAGE);
        PAGE_CONTENT_PROPERTIES = new HashMap<>();
        PAGE_CONTENT_PROPERTIES.put(JcrConstants.JCR_PRIMARYTYPE, PagesConstants.NODE_TYPE_PAGE_CONTENT);
    }

    @Reference
    protected PagesConfiguration pagesConfig;

    @Reference(cardinality = ReferenceCardinality.OPTIONAL)
    protected volatile PagesTenantSupport tenantSupport;

    @Reference
    protected ResourceManager resourceManager;

    @Reference
    protected ReplicationManager replicationManager;

    @Reference
    protected PlatformVersionsService versionsService;

    @Override
    @Nullable
    public Page getPage(@Nonnull BeanContext context, @Nonnull String absPath) {
        Resource resource = context.getResolver().getResource(absPath);
        return (Page.isPage(resource) || Page.isPageContent(resource)) ? createBean(context, resource) : null;
    }

    @Nonnull
    @Override
    public Page createBean(@Nonnull BeanContext context, @Nonnull Resource resource) {
        return new Page(this, context, resource);
    }

    @Override
    @Nullable
    public Page getContainingPage(Model element) {
        return getContainingPage(element.getContext(), element.getResource());
    }

    @Override
    @Nullable
    public Page getContainingPage(@Nonnull final BeanContext context, @Nullable final Resource resource) {
        Page page = null;
        Resource pageResource = resourceManager.findContainingPageResource(resource);
        if (pageResource != null) {
            page = createBean(context, pageResource);
        }
        return page;
    }

    @Override
    public Resource getContainingPageResource(@Nonnull final Resource resource) {
        Resource pageResource = resourceManager.findContainingPageResource(resource);
        // fallback to resource itself if no 'page' found
        return pageResource != null ? pageResource : resource;
    }

    @Override
    public Collection<Page> getPageTemplates(@Nonnull BeanContext context, @Nonnull Resource parent) {
        ResourceResolver resolver = context.getResolver();
        PageTemplateList result = new PageTemplateList(resolver);
        String tenantId = tenantSupport != null ? tenantSupport.getTenantId(parent) : null;
        for (String root : resolver.getSearchPath()) {
            String searchRootPath = root;
            if (APPS_RESOLVER_ROOT.equals(root) && StringUtils.isNotBlank(tenantId)) {
                searchRootPath = tenantSupport.getApplicationRoot(context, tenantId);
            }
            Resource searchRoot;
            if (StringUtils.isNotBlank(searchRootPath) && (searchRoot = resolver.getResource(searchRootPath)) != null) {
                Collection<Page> templates = getModels(context, NODE_TYPE_PAGE, searchRoot, TemplateFilter.INSTANCE);
                result.addAll(templates);
            }
            if (APPS_RESOLVER_ROOT.equals(root)) {
                for (String additionalRoot : pagesConfig.getConfig().sharedTemplates()) {
                    if (StringUtils.isNotBlank(additionalRoot)
                            && (!additionalRoot.startsWith(APPS_RESOLVER_ROOT) || StringUtils.isNotBlank(tenantId))
                            && (searchRoot = resolver.getResource(additionalRoot)) != null) {
                        Collection<Page> templates = getModels(context, NODE_TYPE_PAGE, searchRoot, TemplateFilter.INSTANCE);
                        result.addAll(templates);
                    }
                }
            }
        }
        ContentTypeFilter filter = new ContentTypeFilter(resourceManager, parent);
        String candidatePath = parent.getPath();
        for (int i = result.size(); --i >= 0; ) {
            Page templatePage = result.get(i);
            ResourceManager.Template template = resourceManager.toTemplate(templatePage.getResource());
            if (!filter.isAllowedChild(template,
                    resourceManager.getReference(resolver, candidatePath, template.getResourceType()))) {
                result.remove(i);
            }
        }
        result.sort();
        return result;
    }

    @Override
    @Nonnull
    public Page createPage(@Nonnull BeanContext context, @Nonnull Resource parent, @Nonnull String pageType,
                           @Nonnull String pageName, @Nullable String pageTitle, @Nullable String description,
                           boolean commit)
            throws RepositoryException, PersistenceException {

        Resource pageResource;
        ResourceResolver resolver = context.getResolver();
        checkExistence(resolver, parent, pageName);

        pageResource = resolver.create(parent, pageName, PAGE_PROPERTIES);
        Map<String, Object> contentProperties = new HashMap<>(PAGE_CONTENT_PROPERTIES);
        contentProperties.put(ResourceUtil.PROP_RESOURCE_TYPE, pageType);
        if (StringUtils.isNotBlank(pageTitle)) {
            contentProperties.put(ResourceUtil.PROP_TITLE, pageTitle);
        }
        if (StringUtils.isNotBlank(description)) {
            contentProperties.put(ResourceUtil.PROP_DESCRIPTION, description);
        }
        resolver.create(pageResource, JcrConstants.JCR_CONTENT, contentProperties);

        if (commit) {
            resolver.commit();
        }

        return instanceCreated(context, pageResource);
    }

    @Override
    @Nonnull
    @SuppressWarnings("Duplicates")
    public Page createPage(@Nonnull BeanContext context, @Nonnull Resource parent, @Nonnull Resource pageTemplate,
                           @Nonnull String pageName, @Nullable String pageTitle, @Nullable String description,
                           boolean commit)
            throws RepositoryException, PersistenceException {

        Resource pageResource;
        final ResourceResolver resolver = context.getResolver();
        checkExistence(resolver, parent, pageName);
        final SiteManager siteManager = context.getService(SiteManager.class);

        pageResource = resourceManager.createFromTemplate(new ResourceManager.TemplateContext() {

            @Override
            public ResourceResolver getResolver() {
                return resolver;
            }

            @Override
            public String applyTemplatePlaceholders(@Nonnull final Resource target, @Nonnull final String value) {
                Resource siteResource = siteManager.getContainingSiteResource(target);
                Resource pageResource = getContainingPageResource(target);
                if (pageResource != null) {
                    String result = value.replaceAll("\\$\\{path}", target.getPath());
                    result = result.replaceAll("\\$\\{page}", pageResource.getPath());
                    if (siteResource != null) {
                        result = result.replaceAll("\\$\\{site}", siteResource.getPath());
                    }
                    if (!value.equals(result)) {
                        result = result.replaceAll("/[^/]+/\\.\\./", "/");
                    }
                    return result;
                }
                return value;
            }

        }, parent, pageName, pageTemplate, true);

        Resource content = Objects.requireNonNull(pageResource.getChild(JcrConstants.JCR_CONTENT));
        ModifiableValueMap values = Objects.requireNonNull(content.adaptTo(ModifiableValueMap.class));

        if (StringUtils.isNotBlank(pageTitle)) {
            values.put(ResourceUtil.PROP_TITLE, pageTitle);
        }
        if (StringUtils.isNotBlank(description)) {
            values.put(ResourceUtil.PROP_DESCRIPTION, description);
        }

        if (commit) {
            resolver.commit();
        }
        return instanceCreated(context, pageResource);
    }

    @Override
    public boolean deletePage(@Nonnull BeanContext context, @Nonnull String pagePath, boolean commit)
            throws PersistenceException {
        return deletePage(context, context.getResolver().getResource(pagePath), commit);
    }

    @Override
    public boolean deletePage(@Nonnull BeanContext context, @Nullable Resource pageResource, boolean commit)
            throws PersistenceException {

        if (pageResource != null) {
            ResourceResolver resolver = context.getResolver();
            if (LOG.isInfoEnabled()) {
                LOG.info("deletePage({})", pageResource.getPath());
            }

            resolver.delete(pageResource);

            if (commit) {
                resolver.commit();
            }
            return true;
        }
        return false;
    }

    @Override
    public void touch(@Nonnull BeanContext context, @Nonnull Resource resource, @Nullable Calendar time) {
        Page page = getContainingPage(context, resource);
        if (page != null) {
            touch(context, page, time);
        }
    }

    @Override
    public void touch(@Nonnull BeanContext context, @Nonnull Page page, @Nullable Calendar time) {
        PageContent content = page.getContent();
        if (content != null) {
            if (time == null) {
                time = new GregorianCalendar();
                time.setTimeInMillis(System.currentTimeMillis());
            }
            Resource contentResource = content.getResource();
            ModifiableValueMap values = Objects.requireNonNull(contentResource.adaptTo(ModifiableValueMap.class));
            values.put(PagesConstants.PROP_LAST_MODIFIED, time);
            Session session = contentResource.getResourceResolver().adaptTo(Session.class);
            if (session != null) {
                String userId = session.getUserID();
                if (StringUtils.isNotBlank(userId)) {
                    values.put(PagesConstants.PROP_LAST_MODIFIED_BY, userId);
                }
            }
        }
    }

    @Override
    public void touch(@Nonnull BeanContext context, @Nonnull Collection<Resource> resources, @Nullable Calendar time) {
        resources.stream()
                .map((r) -> getContainingPage(context, r))
                .filter(Objects::nonNull)
                .distinct()
                .forEach((page) -> touch(context, page, time));
    }

    /**
     * retrieve the collection of referrers of a content page
     *
     * @param page       the page
     * @param searchRoot the root in the repository for searching referrers
     * @param resolved   if 'true' only active references (in the same release) are determined
     * @return the collection of found resources
     */
    @Override
    @Nonnull
    public Collection<Resource> getReferrers(@Nonnull final Page page, @Nonnull final Resource searchRoot, boolean resolved) {
        Map<String, Resource> referrers = new TreeMap<>();
        StringFilter propertyFilter = StringFilter.ALL;
        ResourceFilter resourceFilter = ResourceFilter.ALL;
        List<Resource> referringResources = new ArrayList<>();
        try {
            resourceManager.changeReferences(resourceFilter, propertyFilter, searchRoot, referringResources,
                    true, page.getPath(), "");
        } catch (PersistenceException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        ResourceFilter referringPageFilter = PlatformVersionsService.ACTIVATABLE_FILTER;
        if (resolved) {
            ResourceFilter.ContentNodeFilter contentNodeFilter = new ResourceFilter.ContentNodeFilter(true, pagesConfig.getReferenceFilter(ReferenceType.page), ResourceFilter.ALL);
            referringPageFilter =
                    ResourceFilter.FilterSet.Rule.and.of(referringPageFilter, versionsService.releaseAsResourceFilter(searchRoot, null, replicationManager, contentNodeFilter));
        }
        for (Resource resource : referringResources) {
            Resource referringPage = getContainingPageResource(resource);
            // this filtering does not work when pages are renamed wrt. to the release. But there is currently no good way to handle this - you'll run into path problems,
            // anyway. :-(  We also have to do this filtering after the reference finding, since otherwise it stops on pages not yet in the release.
            if (referringPage != null && !StringUtils.equals(page.getPath(), referringPage.getPath()) && referringPageFilter.accept(referringPage)) {
                referrers.putIfAbsent(referringPage.getPath(), referringPage);
            }
        }
        return referrers.values();
    }

    /**
     * retrieve the collection of target resources of the content elements referenced by the page
     *
     * @param page       the page
     * @param type       the type of references; if 'null' all types are retrieved
     * @param unresolved if 'true' only unresolved references (not in the same release) are determined
     * @param transitive if 'true' we also look for references of the references
     * @return the collection of found resources
     */
    @Override
    @Nonnull
    public Collection<Resource> getReferences(@Nonnull final Page page, @Nullable final ReferenceType type,
                                              boolean unresolved, boolean transitive) {
        Map<String, Resource> references = new TreeMap<>();
        ResourceFilter contentNodeFilter = type != null
                ? new ResourceFilter.ContentNodeFilter(true, pagesConfig.getReferenceFilter(type), ResourceFilter.ALL)
                : ResourceFilter.ALL;
        ResourceFilter releaseAsResourceFilter = ResourceFilter.ALL;
        if (unresolved) {
            // this filtering does not work when pages are renamed wrt. to the release. But there is currently no good way to handle this - you'll run into path problems,
            // anyway. :-(
            releaseAsResourceFilter = versionsService.releaseAsResourceFilter(page.getResource(), null, replicationManager, contentNodeFilter);
        }
        ResourceFilter unresolvedFilter = ResourceFilter.FilterSet.Rule.none.of(releaseAsResourceFilter);
        ResourceFilter resourceFilter = type != null
                ? ResourceFilter.FilterSet.Rule.and.of(pagesConfig.getReferenceFilter(type), unresolvedFilter)
                : unresolvedFilter;
        resourceFilter = ResourceFilter.FilterSet.Rule.and.of(resourceFilter, PlatformVersionsService.ACTIVATABLE_FILTER);
        StringFilter propertyFilter = StringFilter.ALL;
        Resource content = page.getContent().getResource();
        retrieveReferences(references, resourceFilter, propertyFilter, content, page.getSite(), new HashSet<>(), transitive);
        return references.values();
    }

    /**
     * Retrieves references of a resource recursively.
     *
     * @param references     collection where we put all references - maps the property value, which references, to the referenced resource
     * @param resourceFilter restricts the resources we accept as references
     * @param propertyFilter restricts the properties that are checked for containing references
     * @param resource       the resource to check for references
     * @param site           the site to which we restrict the ancestor search
     * @param visited        keeps track of which resources we already checked
     * @param transitive     if 'true' we also look for references of the references
     */
    protected void retrieveReferences(@Nonnull Map<String, Resource> references,
                                      @Nonnull final ResourceFilter resourceFilter,
                                      @Nonnull final StringFilter propertyFilter,
                                      @Nonnull final Resource resource,
                                      @Nonnull Site site,
                                      @Nonnull final Set<String> visited,
                                      boolean transitive) {
        if (visited.contains(resource.getPath())) return;
        visited.add(resource.getPath());

        ResourceResolver resolver = resource.getResourceResolver();
        ValueMap values = resource.getValueMap();
        List<Resource> foundReferences = new ArrayList<>();
        for (Map.Entry<String, Object> entry : values.entrySet()) {

            String key = entry.getKey();
            // check property by name
            if (propertyFilter.accept(key)) {

                Object value = entry.getValue();
                if (value instanceof String) {
                    List<Resource> found = retrieveReferencesFromValue(references, resolver, resourceFilter, (String) value);
                    foundReferences.addAll(found);
                } else if (value instanceof String[]) {
                    for (String val : (String[]) value) {
                        List<Resource> found = retrieveReferencesFromValue(references, resolver, resourceFilter, val);
                        foundReferences.addAll(found);
                    }
                }
            }
        }
        // recursive traversal
        for (Resource child : resource.getChildren()) {
            retrieveReferences(references, resourceFilter, propertyFilter, child, site, visited, transitive);
        }
        // check transitive references, too
        if (transitive) {
            for (Resource foundReference : foundReferences) {
                Resource contentNode = foundReference.getChild(CoreConstants.CONTENT_NODE);
                if (contentNode != null)
                    retrieveReferences(references, resourceFilter, propertyFilter, contentNode, site, visited, true);
            }
        }
    }


    /**
     * adds all references found in the value to the set
     */
    @Nonnull
    protected List<Resource> retrieveReferencesFromValue(@Nonnull final Map<String, Resource> references,
                                                         @Nonnull final ResourceResolver resolver,
                                                         @Nonnull final ResourceFilter filter,
                                                         @Nonnull final String value) {
        List<Resource> found = new ArrayList<>();
        Resource resource;
        if (REF_VALUE_PATTERN.matcher(value).matches()) {
            // simple path value...
            if ((resource = resolver.getResource(value)) != null && filter.accept(resource)) {
                references.putIfAbsent(value, resource);
                found.add(resource);
            }
        } else {
            // check for HTML patterns and extract all references if found
            Matcher matcher = REF_LINK_PATTERN.matcher(value);
            while (matcher.find()) {
                String path = matcher.group(2);
                if ((resource = resolver.getResource(path)) != null && filter.accept(resource)) {
                    references.putIfAbsent(path, resource);
                    found.add(resource);
                }
            }
        }
        return found;
    }
}
