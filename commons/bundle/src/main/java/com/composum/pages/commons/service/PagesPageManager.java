package com.composum.pages.commons.service;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.filter.TemplateFilter;
import com.composum.pages.commons.model.ContentTypeFilter;
import com.composum.pages.commons.model.Model;
import com.composum.pages.commons.model.Page;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.RepositoryException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.composum.pages.commons.PagesConstants.NODE_TYPE_PAGE;

@Component(
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Page Manager"
        }
)
public class PagesPageManager extends PagesContentManager<Page> implements PageManager {

    public static final Map<String, Object> PAGE_PROPERTIES;
    public static final Map<String, Object> PAGE_CONTENT_PROPERTIES;

    static {
        PAGE_PROPERTIES = new HashMap<>();
        PAGE_PROPERTIES.put(JcrConstants.JCR_PRIMARYTYPE, PagesConstants.NODE_TYPE_PAGE);
        PAGE_CONTENT_PROPERTIES = new HashMap<>();
        PAGE_CONTENT_PROPERTIES.put(JcrConstants.JCR_PRIMARYTYPE, PagesConstants.NODE_TYPE_PAGE_CONTENT);
    }

    @Reference
    protected ResourceManager resourceManager;

    @Override
    public Page createBean(BeanContext context, Resource resource) {
        return new Page(this, context, resource);
    }

    public Page getContainingPage(Model element) {
        return getContainingPage(element.getContext(), element.getResource());
    }

    public Page getContainingPage(BeanContext context, Resource resource) {
        Page page = null;
        Resource pageResource = resourceManager.findContainingPageResource(resource);
        if (pageResource != null) {
            page = createBean(context, pageResource);
        }
        return page;
    }

    public Resource getContainingPageResource(Resource resource) {
        Resource pageResource = resourceManager.findContainingPageResource(resource);
        // fallback to resource itself if no 'page' found
        return pageResource != null ? pageResource : resource;
    }

    @Override
    public Collection<Page> getPageTemplates(@Nonnull BeanContext context, @Nonnull Resource parent) {
        ResourceResolver resolver = context.getResolver();
        PageTemplateList result = new PageTemplateList(resolver);
        String tenant = null; // TODO tenant support
        for (String root : resolver.getSearchPath()) {
            Resource searchRoot = resolver.getResource(StringUtils.isNotBlank(tenant) ? root + tenant : root);
            Collection<Page> templates = getModels(context, NODE_TYPE_PAGE, searchRoot, TemplateFilter.INSTANCE);
            result.addAll(templates);
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
                String result = value.replaceAll("\\$\\{path}", target.getPath());
                result = result.replaceAll("\\$\\{page}", pageResource.getPath());
                result = result.replaceAll("\\$\\{site}", siteResource.getPath());
                if (!value.equals(result)) {
                    result = result.replaceAll("/[^/]+/\\.\\./", "/");
                }
                return result;
            }

        }, parent, pageName, pageTemplate, true);

        ModifiableValueMap values = pageResource.getChild(JcrConstants.JCR_CONTENT).adaptTo(ModifiableValueMap.class);
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
}
