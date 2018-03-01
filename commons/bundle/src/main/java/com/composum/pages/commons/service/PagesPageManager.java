package com.composum.pages.commons.service;

import com.composum.pages.commons.PagesConstants;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.RepositoryException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.composum.pages.commons.PagesConstants.NODE_TYPE_PAGE;
import static com.composum.pages.commons.model.Page.isPage;

@Component(
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Page Manager"
        }
)
public class PagesPageManager extends ResourceManager<Page> implements PageManager {

    public static final Map<String, Object> PAGE_PROPERTIES;
    public static final Map<String, Object> PAGE_CONTENT_PROPERTIES;

    static {
        PAGE_PROPERTIES = new HashMap<>();
        PAGE_PROPERTIES.put(JcrConstants.JCR_PRIMARYTYPE, PagesConstants.NODE_TYPE_PAGE);
        PAGE_CONTENT_PROPERTIES = new HashMap<>();
        PAGE_CONTENT_PROPERTIES.put(JcrConstants.JCR_PRIMARYTYPE, PagesConstants.NODE_TYPE_PAGE_CONTENT);
    }

    @Override
    public Page createBean(BeanContext context, Resource resource) {
        return new Page(this, context, resource);
    }

    public Page getContainingPage(Model element) {
        Page page = null;
        Resource pageResource = findContainingPageResource(element.getResource());
        if (pageResource != null) {
            page = createBean(element.getContext(), pageResource);
        }
        return page;
    }

    public Resource getContainingPageResource(Resource resource) {
        Resource pageResource = findContainingPageResource(resource);
        // fallback to resource itself if no 'page' found
        return pageResource != null ? pageResource : resource;
    }

    protected Resource findContainingPageResource(Resource resource) {
        if (resource != null) {
            if (isPage(resource)) {
                return resource;
            } else {
                return findContainingPageResource(resource.getParent());
            }
        }
        return null;
    }

    @Override
    public Collection<Page> getPageTemplates(@Nonnull BeanContext context, @Nullable String tenant) {
        UniquePageList result = new UniquePageList();
        ResourceResolver resolver = context.getResolver();
        for (String root : resolver.getSearchPath()) {
            Resource searchRoot = resolver.getResource(StringUtils.isNotBlank(tenant) ? root + tenant : root);
            Collection<Page> templates = getModels(context, NODE_TYPE_PAGE, searchRoot);
            result.addAll(templates);
        }
        result.sort();
        return result;
    }

    @Override
    public Page createPage(BeanContext context, Resource parent, String pageName, String pageType, boolean commit)
            throws RepositoryException, PersistenceException {

        Resource pageResource;
        ResourceResolver resolver = context.getResolver();
        checkExistence(resolver, parent, pageName);

        pageResource = resolver.create(parent, pageName, PAGE_PROPERTIES);
        Map<String, Object> contentProperties = new HashMap<>(PAGE_CONTENT_PROPERTIES);
        contentProperties.put(ResourceUtil.PROP_RESOURCE_TYPE, pageType);
        resolver.create(pageResource, JcrConstants.JCR_CONTENT, contentProperties);

        if (commit) {
            resolver.commit();
        }

        return instanceCreated(context, pageResource);
    }

    @Override
    public Page createPage(@Nonnull BeanContext context, @Nonnull Resource parent, @Nonnull String pageName,
                           @Nullable String pageTitle, @Nullable String description,
                           @Nonnull Resource pageTemplate, boolean commit)
            throws RepositoryException, PersistenceException {

        Resource pageResource;
        ResourceResolver resolver = context.getResolver();
        checkExistence(resolver, parent, pageName);

        pageResource = resolver.copy(pageTemplate.getPath(), parent.getPath() + "/" + pageName);

        ModifiableValueMap values = pageResource.adaptTo(ModifiableValueMap.class);
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
