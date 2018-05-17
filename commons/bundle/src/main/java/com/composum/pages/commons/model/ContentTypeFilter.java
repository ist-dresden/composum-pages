package com.composum.pages.commons.model;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.properties.PathPatternSet;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * a filter implementation to determine a set of potential children types or templates of a target resource
 */
public class ContentTypeFilter {

    protected final ResourceReference designatedTarget;
    protected final Template targetTemplate;
    protected final String resourceType;

    protected final ResourceResolver resolver;

    private transient PathPatternSet forbiddenChildTemplates;
    private transient PathPatternSet allowedChildTemplates;
    private transient PathPatternSet forbiddenChildTypes;
    private transient PathPatternSet allowedChildTypes;

    public ContentTypeFilter(@Nonnull final Resource designatedTarget) {
        this.designatedTarget = new ResourceReference(designatedTarget, null);
        this.targetTemplate = Template.getTemplateOf(designatedTarget);
        Resource targetContent = designatedTarget.getChild(JcrConstants.JCR_CONTENT);
        this.resourceType = targetContent != null
                ? targetContent.getResourceType() : designatedTarget.getResourceType();
        this.resolver = designatedTarget.getResourceResolver();
    }

    public String getPath() {
        return designatedTarget.getPath();
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
        return isAllowedChildByTemplate(template, resourceReference) && isAllowedChildByType(template, resourceReference);
    }

    public boolean isAllowedChildByTemplate(@Nullable Template template, @Nonnull ResourceReference resourceReference) {
        PathPatternSet allowedParents = getAllowedParentTemplates(template, resourceReference);
        PathPatternSet forbiddenParents = getForbiddenParentTemplates(template, resourceReference);
        PathPatternSet allowedChildren = getAllowedChildTemplates();
        PathPatternSet forbiddenChildren = getForbiddenChildTemplates();
        @SuppressWarnings("UnnecessaryLocalVariable")
        boolean isAllowed = (targetTemplate != null
                && (!allowedParents.isValid() || allowedParents.matches(targetTemplate.getPath()))
                && (!forbiddenParents.isValid() || !forbiddenParents.matches(targetTemplate.getPath())))
                && (template != null
                && (!allowedChildren.isValid() || allowedChildren.matches(template.getPath()))
                && (!forbiddenChildren.isValid() || !forbiddenChildren.matches(template.getPath())));
        return isAllowed;
    }

    public boolean isAllowedChildByType(@Nullable Template template, @Nonnull ResourceReference resourceReference) {
        PathPatternSet allowedParents = getAllowedParentTypes(template, resourceReference);
        PathPatternSet forbiddenParents = getForbiddenParentTypes(template, resourceReference);
        PathPatternSet allowedChildren = getAllowedChildTypes();
        PathPatternSet forbiddenChildren = getForbiddenChildTypes();
        @SuppressWarnings("UnnecessaryLocalVariable")
        boolean isAllowed = (targetTemplate != null
                && (!allowedParents.isValid() || allowedParents.matches(resourceType))
                && (!forbiddenParents.isValid() || !forbiddenParents.matches(resourceType)))
                && (template != null
                && (!allowedChildren.isValid() || allowedChildren.matches(resourceReference.getType()))
                && (!forbiddenChildren.isValid() || !forbiddenChildren.matches(resourceReference.getType())));
        return isAllowed;
    }

    @Nonnull
    protected PathPatternSet getAllowedParentTemplates(@Nullable Template template, @Nonnull ResourceReference resourceReference) {
        return getAllowedTypes(template, resourceReference, PagesConstants.PROP_ALLOWED_PARENT_TEMPLATES);
    }

    @Nonnull
    protected PathPatternSet getForbiddenParentTemplates(@Nullable Template template, @Nonnull ResourceReference resourceReference) {
        return getAllowedTypes(template, resourceReference, PagesConstants.PROP_FORBIDDEN_PARENT_TEMPLATES);
    }

    @Nonnull
    protected PathPatternSet getAllowedChildTemplates() {
        if (allowedChildTemplates == null) {
            allowedChildTemplates = getAllowedTypes(targetTemplate, designatedTarget, PagesConstants.PROP_ALLOWED_CHILD_TEMPLATES);
        }
        return allowedChildTemplates;
    }

    @Nonnull
    protected PathPatternSet getForbiddenChildTemplates() {
        if (forbiddenChildTemplates == null) {
            forbiddenChildTemplates = getAllowedTypes(targetTemplate, designatedTarget, PagesConstants.PROP_FORBIDDEN_CHILD_TEMPLATES);
        }
        return forbiddenChildTemplates;
    }

    @Nonnull
    protected PathPatternSet getAllowedParentTypes(@Nullable Template template, @Nonnull ResourceReference resourceReference) {
        return getAllowedTypes(template, resourceReference, PagesConstants.PROP_ALLOWED_PARENT_TYPES);
    }

    @Nonnull
    protected PathPatternSet getForbiddenParentTypes(@Nullable Template template, @Nonnull ResourceReference resourceReference) {
        return getAllowedTypes(template, resourceReference, PagesConstants.PROP_FORBIDDEN_PARENT_TYPES);
    }

    @Nonnull
    protected PathPatternSet getAllowedChildTypes() {
        if (allowedChildTypes == null) {
            allowedChildTypes = getAllowedTypes(targetTemplate, designatedTarget, PagesConstants.PROP_ALLOWED_CHILD_TYPES);
        }
        return allowedChildTypes;
    }

    @Nonnull
    protected PathPatternSet getForbiddenChildTypes() {
        if (forbiddenChildTypes == null) {
            forbiddenChildTypes = getAllowedTypes(targetTemplate, designatedTarget, PagesConstants.PROP_FORBIDDEN_CHILD_TYPES);
        }
        return forbiddenChildTypes;
    }

    @Nonnull
    protected PathPatternSet getAllowedTypes(@Nullable Template template, @Nonnull ResourceReference resourceReference,
                                             @Nonnull String propertyName) {
        PathPatternSet allowedTypes = null;
        if (resourceReference.isExisting()) {
            allowedTypes = new PathPatternSet(resourceReference.getResource().getValueMap().get(propertyName, String[].class));
        }
        if (allowedTypes == null || !allowedTypes.isValid()) {
            if (template != null) {
                allowedTypes = template.getAllowedTypes(propertyName);
            }
        }
        if (allowedTypes == null || !allowedTypes.isValid()) {
            allowedTypes = new PathPatternSet(resourceReference, propertyName);
        }
        return allowedTypes;
    }
}
