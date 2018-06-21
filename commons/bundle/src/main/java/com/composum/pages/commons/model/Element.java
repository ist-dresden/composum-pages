package com.composum.pages.commons.model;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.properties.PathPatternSet;
import com.composum.pages.commons.service.ResourceManager;
import com.composum.pages.commons.util.ResolverUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import static com.composum.pages.commons.PagesConstants.NODE_TYPE_ELEMENT;
import static com.composum.pages.commons.PagesConstants.PROP_ALLOWED_CONTAINERS;
import static com.composum.pages.commons.servlet.EditServlet.EDIT_RESOURCE_TYPE_KEY;

public class Element extends AbstractModel {

    // static resource type determination

    /**
     * check the 'cpp:Element' type for a resource with an optional overloaded type
     *
     * @param resolver the resolver to use for type check (if resource is null)
     * @param resource the resource (can be 'null' if type is available)
     * @param type     the optional resource type (necessary if resource is 'null')
     */
    public static boolean isElement(ResourceResolver resolver, Resource resource, String type) {
        return (resource != null && (resource.isResourceType(NODE_TYPE_ELEMENT) ||
                NODE_TYPE_ELEMENT.equals(ResolverUtil.getTypeProperty(
                        resource, type, PagesConstants.PROP_COMPONENT_TYPE, "")))) ||
                (StringUtils.isNotBlank(type) &&
                        NODE_TYPE_ELEMENT.equals(ResolverUtil.getTypeProperty(
                                resolver, type, PagesConstants.PROP_COMPONENT_TYPE, "")));
    }

    // transient attributes

    private transient PathPatternSet allowedContainers;

    public Element() {
    }

    public Element(BeanContext context, String path, String resourceType) {
        initialize(context, path, resourceType);
    }

    public Element(BeanContext context, Resource resource) {
        initialize(context, resource);
    }

    // initialization

    /**
     * Uses an explicit resource type determined by a template if present (static include of no existing resources)
     * The type is used only if the resource is a non existing resource (in this case we need such a type hint).
     * The type must be available as attribute '${EditServlet.EDIT_RESOURCE_TYPE_KEY}' of the request.
     */
    @Override
    protected void initializeWithResource(Resource resource) {
        super.initializeWithResource(resource);
        if (StringUtils.isBlank(this.type) && ResourceUtil.isNonExistingResource(resource)) {
            SlingHttpServletRequest request = context.getRequest();
            this.type = (String) request.getAttribute(EDIT_RESOURCE_TYPE_KEY);
        }
    }

    // 'allowedContainers' property...

    public boolean isAllowedContainer(Container container) {
        return isAllowedContainer(container.getType());
    }

    public boolean isAllowedContainer(Resource resource) {
        return isAllowedContainer(resource.getResourceType());
    }

    public boolean isAllowedContainer(ResourceManager.ResourceReference container) {
        return isAllowedContainer(container.getType());
    }

    public boolean isAllowedContainer(String resourceType) {
        return getAllowedContainers().matches(resourceType);
    }

    /**
     * returns the 'allowedContainers' rule for this element (from the configuration)
     */
    public PathPatternSet getAllowedContainers() {
        if (allowedContainers == null) {
            allowedContainers = new PathPatternSet(getResourceManager().getReference(this), PROP_ALLOWED_CONTAINERS);
        }
        return allowedContainers;
    }
}
