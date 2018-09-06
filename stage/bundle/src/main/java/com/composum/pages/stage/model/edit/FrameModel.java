package com.composum.pages.stage.model.edit;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.Component;
import com.composum.pages.commons.model.GenericModel;
import com.composum.pages.commons.util.ResolverUtil;
import com.composum.pages.commons.util.ResourceTypeUtil;
import com.composum.pages.commons.util.TagCssClasses;
import com.composum.sling.core.BeanContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import static com.composum.pages.commons.model.AbstractModel.CSS_BASE_TYPE_RESTRICTION;
import static com.composum.pages.commons.util.ResourceTypeUtil.EDIT_TILE_PATH;
import static com.composum.sling.core.servlet.AbstractServiceServlet.PARAM_TYPE;

/**
 * a model of a frame component which is an editing component of an element; this model is a wrapper for the element to edit
 */
public class FrameModel extends GenericModel {

    // probably preset during resource determination
    protected transient String resourceType;

    private transient Resource typeResource;
    private transient PagesConstants.ComponentType componentType;
    private transient Component component;

    public PagesConstants.ComponentType getComponentType() {
        if (componentType == null) {
            BeanContext context = delegate.getContext();
            componentType = PagesConstants.ComponentType.typeOf(context.getResolver(), getResource(), getType());
        }
        return componentType;
    }

    /**
     * @return the resource of the frame element itself instead of the element to edit
     */
    public Resource getFrameResource() {
        return getContext().getResource();
    }

    /**
     * @return the CSS base of the frame element itself not of the delegate
     */
    @Override
    public String getCssBase() {
        Resource resource = getFrameResource();
        String type = CSS_BASE_TYPE_RESTRICTION.accept(resource) ? resource.getResourceType() : null;
        return StringUtils.isNotBlank(type) ? TagCssClasses.cssOfType(type) : null;
    }

    /**
     * @return the resource type retrieved from the URL 'type' parameter ot the resource type element to edit
     */
    @Override
    public String getType() {
        if (resourceType == null) {
            BeanContext context = delegate.getContext();
            SlingHttpServletRequest request = context.getRequest();
            resourceType = request.getParameter(PARAM_TYPE);
            if (StringUtils.isBlank(resourceType)) {
                resourceType = super.getType();
            }
        }
        return resourceType;
    }

    @Override
    public Component getComponent() {
        if (component == null) {
            Resource typeResource = getTypeResource();
            component = new Component(getContext(), typeResource != null ? typeResource : getResource());
        }
        return component;
    }

    /**
     * retrieves the element resource focused by the frame element for editing
     */
    @Override
    protected Resource determineDelegateResource(BeanContext context, Resource resource) {
        String path = getDelegatePath(context);
        return context.getResolver().resolve(path);
    }

    /**
     * retrieves the path of the element to handle by the frame element using the suffix of the request
     */
    public static String getDelegatePath(BeanContext context) {
        SlingHttpServletRequest request = context.getRequest();
        String delegatePath = request.getRequestPathInfo().getSuffix();
        if (StringUtils.isBlank(delegatePath)) {
            delegatePath = "/";
        }
        return delegatePath;
    }

    public Resource getTypeResource() {
        if (typeResource == null) {
            Resource resource = getResource();
            typeResource = ResolverUtil.getResourceType(resource, getType());
        }
        return typeResource;
    }

    public String getTypePath() {
        Resource type = getTypeResource();
        return type != null ? type.getPath() : "";
    }

    // Tile rendering

    public String getTileResourceType() {
        return ResourceTypeUtil.getSubtypePath(getContext().getResolver(), getResource(), getPath(), EDIT_TILE_PATH, null);
    }
}
