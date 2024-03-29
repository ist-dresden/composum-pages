package com.composum.pages.commons.taglib;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.util.NewResourceParent;
import com.composum.pages.commons.util.ResourceTypeUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.XSS;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.Map;

import static com.composum.pages.commons.servlet.EditServlet.EDIT_RESOURCE_KEY;
import static com.composum.pages.commons.servlet.EditServlet.EDIT_RESOURCE_TYPE_KEY;

/**
 * the EditDialogTag creates the HTML code for an edit dialog of a component
 */
@SuppressWarnings("JavaDoc")
public abstract class AbstractEditTag extends AbstractFormTag {

    protected Resource editResource;
    protected Resource referenceResource;
    protected Resource parentResource;
    protected String resourcePath;
    protected String resourceType;
    protected String primaryType;

    private transient String defaultPrimaryType;
    private transient String editPath;

    @Override
    protected void clear() {
        editPath = null;
        defaultPrimaryType = null;
        primaryType = null;
        resourcePath = null;
        resourceType = null;
        parentResource = null;
        referenceResource = null;
        editResource = null;
        super.clear();
    }

    /**
     * Determines the resource to edit by the form of the dialog to create by this tag. This resource is mainly
     * determined by a 'EDIT_RESOURCE_KEY' request attribute which is declared by the edit servlet during the dialog
     * load request; if the dialog is loaded by a dialog component URL the edit resource is expected as the URLs suffix.
     * Dialogs to create a new resource should be marked with a '*' resourcePath attribute to avoid copying of
     * parent properties.
     *
     * @param context the current request context
     * @return the resource to edit
     */
    @Override
    public Resource getModelResource(BeanContext context) {
        if (editResource == null) {
            ResourceResolver resolver = context.getResolver();
            boolean isStarResource = false;
            if (StringUtils.isNotBlank(resourcePath)) {
                if (resourcePath.endsWith("*")) {
                    isStarResource = true; // this is a 'create' dialog...
                } else {
                    editResource = resolver.getResource(resourcePath);
                }
            }
            if (editResource == null) {
                // use the resource defined by the dialog delivery servlet
                editResource = (Resource) request.getAttribute(EDIT_RESOURCE_KEY);
            }
            if (editResource == null) {
                // use the request suffix as the resource to edit
                String suffix = XSS.filter(request.getRequestPathInfo().getSuffix());
                if (StringUtils.isNotBlank(suffix) && !"/".equals(suffix)) {
                    editResource = request.getResourceResolver().getResource(suffix);
                    if (editResource != null) {
                        request.setAttribute(EDIT_RESOURCE_KEY, editResource);
                    }
                } else {
                    editResource = super.getModelResource(context);
                }
            }
            if (isStarResource) {
                // keep parent resource and support access to it
                parentResource = editResource;
                // wrap the current resource and make the resource empty
                editResource = new NewResourceParent(editResource);
            }
        }
        return editResource;
    }

    public String getEditPath() {
        if (editPath == null) {
            Resource editResource = getModelResource(context);
            editPath = editResource != resource ? editResource.getPath() : "";
        }
        return editPath;
    }

    /**
     * @return the result of 'getModelResource()' as the resource to drive this tag
     */
    @Override
    public Resource getResource() {
        return getModelResource(context);
    }

    public Resource getParentResource() {
        return parentResource;
    }

    // create: 'sling:resourceType'

    @Override
    public void setResource(Resource resource) {
        editResource = resource;
    }

    public void setResourcePath(String path) {
        resourcePath = path;
    }

    /**
     * the resource type of a new element created by a dialog (hidden 'sling:resourceType' property value)
     */
    @Override
    public String getResourceType() {
        return ResourceTypeUtil.relativeResourceType(resourceResolver,
                StringUtils.isNotBlank(resourceType) ? resourceType : getDefaultResourceType());
    }

    public void setResourceType(String type) {
        resourceType = type;
    }

    public String getDefaultResourceType() {
        String requestedType = (String) request.getAttribute(EDIT_RESOURCE_TYPE_KEY);
        return requestedType != null ? requestedType : "";
    }

    // create: 'jcr:primaryType'

    /**
     * the primary type of a new element created by a dialog (hidden 'jcr:primaryType' property value)
     */
    public String getPrimaryType() {
        return StringUtils.isNotBlank(primaryType) ? primaryType : getDefaultPrimaryType();
    }

    public void setPrimaryType(String type) {
        primaryType = type;
    }

    public String getDefaultPrimaryType() {
        if (defaultPrimaryType == null) {
            defaultPrimaryType = PagesConstants.ComponentType.getPrimaryType(
                    PagesConstants.ComponentType.typeOf(resourceResolver, getReferenceResource(), getResourceType()));
        }
        return defaultPrimaryType;
    }

    public Resource getReferenceResource() {
        if (referenceResource == null) {
            referenceResource = getResource();
            if (Page.isPageContent(referenceResource) || Site.isSiteConfiguration(referenceResource)) {
                referenceResource = referenceResource.getParent();
            }
        }
        return referenceResource;
    }

    @Override
    protected void collectAttributes(Map<String, Object> attributeSet) {
        super.collectAttributes(attributeSet);
        Resource resourceToEdit = getReferenceResource();
        if (resourceToEdit != null) {
            // embed reference data as data attributes of the dialog DOM element
            addEditAttributes(attributeSet, resourceToEdit, getResourceType());
        }
    }
}
