package com.composum.pages.commons.model;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.properties.PathPatternSet;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.vault.util.JcrConstants;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

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
                } else {
                    if (!JcrConstants.JCR_CONTENT.equals(resource.getName())) {
                        Template parentTemplate = getTemplateOf(resource.getParent());
                        if (parentTemplate != null) {
                            Resource templateChild = parentTemplate.templateResource.getChild(resource.getName());
                            template = templateChild != null ? new Template(templateChild) : parentTemplate;
                        }
                    }
                }
            }
        }
        return template;
    }

    protected final Resource templateResource;
    protected final Resource contentResource;
    protected final Map<String, PathPatternSet> allowedTypes;

    /**
     * A template can be a Site or Page template with a 'jcr:content' child resourece containing the template rules
     * but it can also be a simple resource with the template rules properties (e.g. for folder rules).
     */
    public Template(@Nonnull Resource templateResource) {
        this.templateResource = templateResource;
        Resource contentChild = templateResource.getChild(JcrConstants.JCR_CONTENT);
        this.contentResource = contentChild != null ? contentChild : templateResource;
        this.allowedTypes = new LinkedHashMap<>();
    }

    public String getPath() {
        return templateResource.getPath();
    }

    public String getResourceType() {
        return contentResource.getResourceType();
    }

    @Nonnull
    public PathPatternSet getAllowedPaths() {
        return getAllowedTypes(PagesConstants.PROP_ALLOWED_PATHS);
    }

    @Nonnull
    public PathPatternSet getForbiddenPaths() {
        return getAllowedTypes(PagesConstants.PROP_FORBIDDEN_PATHS);
    }

    @Nonnull
    public PathPatternSet getAllowedParentTemplates() {
        return getAllowedTypes(PagesConstants.PROP_ALLOWED_PARENT_TEMPLATES);
    }

    @Nonnull
    public PathPatternSet getForbiddenParentTemplates() {
        return getAllowedTypes(PagesConstants.PROP_FORBIDDEN_PARENT_TEMPLATES);
    }

    @Nonnull
    public PathPatternSet getAllowedChildTemplates() {
        return getAllowedTypes(PagesConstants.PROP_ALLOWED_CHILD_TEMPLATES);
    }

    @Nonnull
    public PathPatternSet getForbiddenChildTemplates() {
        return getAllowedTypes(PagesConstants.PROP_FORBIDDEN_CHILD_TEMPLATES);
    }

    @Nonnull
    public PathPatternSet getAllowedParentTypes() {
        return getAllowedTypes(PagesConstants.PROP_ALLOWED_PARENT_TYPES);
    }

    @Nonnull
    public PathPatternSet getForbiddenParentTypes() {
        return getAllowedTypes(PagesConstants.PROP_FORBIDDEN_PARENT_TYPES);
    }

    @Nonnull
    public PathPatternSet getAllowedChildTypes() {
        return getAllowedTypes(PagesConstants.PROP_ALLOWED_CHILD_TYPES);
    }

    @Nonnull
    public PathPatternSet getForbiddenChildTypes() {
        return getAllowedTypes(PagesConstants.PROP_FORBIDDEN_CHILD_TYPES);
    }

    @Nonnull
    public PathPatternSet getAllowedTypes(@Nonnull String propertyName) {
        PathPatternSet types = allowedTypes.get(propertyName);
        if (types == null) {
            types = new PathPatternSet(new ResourceReference(contentResource, null), propertyName);
            allowedTypes.put(propertyName, types);
        }
        return types;
    }
}
