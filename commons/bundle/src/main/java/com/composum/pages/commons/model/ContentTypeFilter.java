package com.composum.pages.commons.model;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.properties.AllowedTypes;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * a filter implementation to determine a set of potential children of a target resource
 */
public class ContentTypeFilter {

    protected final ResourceReference designatedTarget;
    protected final Template targetTemplate;
    protected final String resourceType;

    protected final ResourceResolver resolver;

    private transient AllowedTypes allowedChildTemplates;
    private transient AllowedTypes allowedChildTypes;

    public ContentTypeFilter(@Nonnull final Resource designatedTarget) {
        this.designatedTarget = new ResourceReference(designatedTarget, null);
        this.targetTemplate = Template.getTemplateOf(designatedTarget);
        this.resourceType = designatedTarget.getResourceType();
        this.resolver = designatedTarget.getResourceResolver();
    }

    /**
     * for moves - check the right target
     *
     * @param existingContent the content to insert as child of the filters target
     * @return true if the content can be a child of the filters designated target
     */
    public boolean isAllowedChild(@Nonnull Resource existingContent) {
        Template contentTemplate = Template.getTemplateOf(existingContent);
        return isAllowedChild(contentTemplate, new ResourceReference(existingContent, null));
    }

    /**
     * for creations - check for the right type of new content by an optional template and a reference (probably virtual)
     *
     * @param template          a potential template to check for a valid insert rule
     * @param resourceReference the probably virtual resource designated to insert as the targets child
     * @return true if the referenced resource could be a child of the filters designated target
     */
    public boolean isAllowedChild(@Nullable Template template, @Nonnull ResourceReference resourceReference) {
        AllowedTypes parentTypes = getAllowedParentTemplates(template, resourceReference);
        AllowedTypes childTypes = getAllowedParentTemplates(template, resourceReference);
        boolean checkTemplatesOnly = parentTypes.isValid() || childTypes.isValid();
        return  // check template matching first
                ((targetTemplate != null && (!parentTypes.isValid() || parentTypes.matches(targetTemplate.getPath())))
                        && (template != null && (!childTypes.isValid() || childTypes.matches(template.getPath()))))
                        || (!checkTemplatesOnly && // check resource type matching if no useful template rule found
                        ((!(parentTypes = getAllowedParentTypes(template, resourceReference)).isValid() || parentTypes.matches(resourceType))
                                && (!(parentTypes = getAllowedChildTypes()).isValid() || parentTypes.matches(resourceReference.getType()))));
    }

    @Nonnull
    protected AllowedTypes getAllowedParentTemplates(@Nullable Template template, @Nonnull ResourceReference resourceReference) {
        return getAllowedTypes(template, resourceReference, PagesConstants.PROP_ALLOWED_PARENT_TEMPLATES);
    }

    @Nonnull
    protected AllowedTypes getAllowedChildTemplates() {
        if (allowedChildTemplates == null) {
            allowedChildTemplates = getAllowedTypes(targetTemplate, designatedTarget, PagesConstants.PROP_ALLOWED_CHILD_TEMPLATES);
        }
        return allowedChildTemplates;
    }

    @Nonnull
    protected AllowedTypes getAllowedParentTypes(@Nullable Template template, @Nonnull ResourceReference resourceReference) {
        return getAllowedTypes(template, resourceReference, PagesConstants.PROP_ALLOWED_PARENT_TYPES);
    }

    @Nonnull
    protected AllowedTypes getAllowedChildTypes() {
        if (allowedChildTypes == null) {
            allowedChildTypes = getAllowedTypes(targetTemplate, designatedTarget, PagesConstants.PROP_ALLOWED_CHILD_TYPES);
        }
        return allowedChildTypes;
    }

    @Nonnull
    protected AllowedTypes getAllowedTypes(@Nullable Template template, @Nonnull ResourceReference resourceReference,
                                           @Nonnull String propertyName) {
        AllowedTypes allowedTypes = null;
        if (resourceReference.isExisting()) {
            allowedTypes = new AllowedTypes(resourceReference.getResource().getValueMap().get(propertyName, String[].class));
        }
        if (allowedTypes == null || !allowedTypes.isValid()) {
            if (template != null) {
                allowedTypes = template.getAllowedParentTypes();
            }
        }
        if (allowedTypes == null || !allowedTypes.isValid()) {
            allowedTypes = new AllowedTypes(resourceReference, propertyName);
        }
        return allowedTypes;
    }
}
