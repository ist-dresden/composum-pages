package com.composum.pages.commons.model;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.properties.AllowedTypes;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.vault.util.JcrConstants;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Template {

    @Nullable
    public static Template getTemplateOf(Resource resource) {
        Template template = null;
        if (resource != null && !ResourceUtil.isNonExistingResource(resource)) {
            if (Site.isSite(resource)) {
                return getTemplateOf(resource.getChild(JcrConstants.JCR_CONTENT));
            } else if (Page.isPage(resource)) {
                return getTemplateOf(resource.getChild(JcrConstants.JCR_CONTENT));
            } else {
                String templatePath = resource.getValueMap().get(PagesConstants.PROP_TEMPLATE, "");
                if (StringUtils.isNotBlank(templatePath)) {
                    Resource templateResource = resource.getResourceResolver().getResource(templatePath);
                    if (templateResource != null && !ResourceUtil.isNonExistingResource(templateResource)) {
                        template = new Template(templateResource);
                    }
                }
            }
        }
        return template;
    }

    protected final Resource templateResource;

    private transient AllowedTypes allowedParentTemplates;
    private transient AllowedTypes allowedChildTemplates;
    private transient AllowedTypes allowedParentTypes;
    private transient AllowedTypes allowedChildTypes;

    public Template(@Nonnull Resource templateResource) {
        this.templateResource = templateResource;
    }

    public String getPath() {
        return templateResource.getPath();
    }

    @Nonnull
    public AllowedTypes getAllowedParentTemplates() {
        if (allowedParentTemplates == null) {
            allowedParentTemplates = new AllowedTypes(
                    new ResourceReference(templateResource, null), PagesConstants.PROP_ALLOWED_PARENT_TEMPLATES);
        }
        return allowedParentTemplates;
    }

    @Nonnull
    public AllowedTypes getAllowedChildTemplates() {
        if (allowedChildTemplates == null) {
            allowedChildTemplates = new AllowedTypes(
                    new ResourceReference(templateResource, null), PagesConstants.PROP_ALLOWED_CHILD_TEMPLATES);
        }
        return allowedChildTemplates;
    }

    @Nonnull
    public AllowedTypes getAllowedParentTypes() {
        if (allowedParentTypes == null) {
            allowedParentTypes = new AllowedTypes(
                    new ResourceReference(templateResource, null), PagesConstants.PROP_ALLOWED_PARENT_TYPES);
        }
        return allowedParentTypes;
    }

    @Nonnull
    public AllowedTypes getAllowedChildTypes() {
        if (allowedChildTypes == null) {
            allowedChildTypes = new AllowedTypes(
                    new ResourceReference(templateResource, null), PagesConstants.PROP_ALLOWED_CHILD_TYPES);
        }
        return allowedChildTypes;
    }
}
