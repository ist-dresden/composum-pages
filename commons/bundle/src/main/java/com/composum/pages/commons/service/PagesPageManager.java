package com.composum.pages.commons.service;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.Model;
import com.composum.pages.commons.model.Page;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;

import javax.jcr.RepositoryException;
import java.util.HashMap;
import java.util.Map;

import static com.composum.pages.commons.model.Page.isPage;

@org.osgi.service.component.annotations.Component(
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

    public Page createPage(BeanContext context, Resource parent, String pageName, Resource pageTemplate, boolean commit)
            throws RepositoryException, PersistenceException {

        Resource pageResource;
        ResourceResolver resolver = context.getResolver();
        checkExistence(resolver, parent, pageName);

        pageResource = resolver.copy(pageTemplate.getPath(), parent.getPath() + "/" + pageName);

        if (commit) {
            resolver.commit();
        }

        return instanceCreated(context, pageResource);
    }
}
