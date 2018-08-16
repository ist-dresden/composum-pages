package com.composum.pages.stage.model.edit;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.Component;
import com.composum.pages.commons.model.Element;
import com.composum.pages.commons.util.ResolverUtil;
import com.composum.pages.commons.util.ResourceTypeUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import static com.composum.pages.commons.util.ResourceTypeUtil.EDIT_TILE_PATH;
import static com.composum.sling.core.servlet.AbstractServiceServlet.PARAM_TYPE;

public class FrameElement extends Element {

    private transient Element element;
    private transient Resource elementResource;
    private transient Resource elementType;
    private transient String elementPath;
    private transient PagesConstants.ComponentType componentType;

    public Element getElement() {
        if (element == null) {
            element = new Element(context, getElementResource());
        }
        return element;
    }

    public PagesConstants.ComponentType getComponentType() {
        if (componentType == null) {
            SlingHttpServletRequest request = context.getRequest();
            // use type hint if available (useful on type overriding during 'include')
            String typeParam = request.getParameter(PARAM_TYPE);
            componentType = PagesConstants.ComponentType.typeOf(resolver, getElementResource(), typeParam);
        }
        return componentType;
    }

    public Resource getElementResource() {
        if (elementResource == null) {
            elementResource = determineElementResource();
        }
        return elementResource;
    }

    public Resource getElementType() {
        if (elementType == null) {
            Resource resource = getElementResource();
            elementType = ResolverUtil.getResourceType(resource, resource.getResourceType());
            if (elementType == null) {
                String type = context.getRequest().getParameter(Component.TYPE_HINT_PARAM);
                if (StringUtils.isBlank(type)) {
                    elementType = ResolverUtil.getResourceType(resource, type);
                }
            }
        }
        return elementType;
    }

    public String getElementTypePath() {
        Resource type = getElementType();
        return type != null ? type.getPath() : "";
    }

    public String getElementTypeHint() {
        return getTypeHint(getElementTypePath());
    }

    protected Resource determineElementResource() {
        String path = getElementPath();
        return resolver.resolve(path);
    }

    public String getElementPath() {
        if (elementPath == null) {
            elementPath = getElementPath(context);
        }
        return elementPath;
    }

    public static String getElementPath(BeanContext context) {
        SlingHttpServletRequest request = context.getRequest();
        String elementPath = request.getRequestPathInfo().getSuffix();
        if (StringUtils.isBlank(elementPath)) {
            elementPath = "/";
        } else {
            Resource resource = request.getResourceResolver().resolve(elementPath);
            if (!ResourceUtil.isNonExistingResource(resource)) {
                elementPath = resource.getPath();
            } else {
                if (elementPath.endsWith(".html")) {
                    elementPath = elementPath.substring(0, elementPath.length() - 5);
                }
            }
        }
        return elementPath;
    }

    // Tile rendering

    public String getTileResourceType() {
        return ResourceTypeUtil.getSubtypePath(resolver, getElementResource(), getElementPath(), EDIT_TILE_PATH, null);
    }
}
