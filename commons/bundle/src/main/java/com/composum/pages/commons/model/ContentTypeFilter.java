package com.composum.pages.commons.model;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.properties.PathPatternSet;
import com.composum.pages.commons.service.ResourceManager;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * a filter implementation to determine a set of potential children types or templates of a target resource
 */
public class ContentTypeFilter {

    private static final Logger LOG = LoggerFactory.getLogger(ContentTypeFilter.class);

    protected final ResourceManager resourceManager;
    protected final ResourceManager.ResourceReference designatedTarget;
    protected final ResourceManager.Template targetTemplate;
    protected final String resourceType;

    protected final ResourceResolver resolver;

    private transient PathPatternSet forbiddenChildTemplates;
    private transient PathPatternSet allowedChildTemplates;
    private transient PathPatternSet forbiddenChildTypes;
    private transient PathPatternSet allowedChildTypes;

    public ContentTypeFilter(@Nonnull ResourceManager resourceManager, @Nonnull final Resource designatedTarget) {
        this.resourceManager = resourceManager;
        this.designatedTarget = resourceManager.getReference(designatedTarget, null);
        this.targetTemplate = resourceManager.getTemplateOf(designatedTarget);
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
        ResourceManager.Template contentTemplate = resourceManager.getTemplateOf(existingContent);
        return isAllowedChild(contentTemplate, resourceManager.getReference(existingContent, null));
    }

    /**
     * for creations - check for the right type of new content by an optional template and a reference (probably virtual)
     *
     * @param template          a potential template to check for a valid insert rule
     * @param resourceReference the probably virtual resource designated to insert as the targets child
     * @return true if the referenced resource could be a child of the filters designated target
     */
    public boolean isAllowedChild(@Nullable ResourceManager.Template template,
                                  @Nonnull ResourceManager.ResourceReference resourceReference) {
        return isAllowedChildByTemplate(template, resourceReference) && isAllowedChildByType(template, resourceReference);
    }

    public boolean isAllowedChildByTemplate(@Nullable ResourceManager.Template template,
                                            @Nonnull ResourceManager.ResourceReference resourceReference) {
        ResourceResolver resolver = resourceReference.getResolver();
        PathPatternSet allowedParents = getAllowedParentTemplates(template, resourceReference);
        PathPatternSet forbiddenParents = getForbiddenParentTemplates(template, resourceReference);
        PathPatternSet allowedChildren = getAllowedChildTemplates();
        PathPatternSet forbiddenChildren = getForbiddenChildTemplates();
        boolean isAllowed = (targetTemplate == null
                || ((!allowedParents.isValid() || allowedParents.matches(resolver, targetTemplate.getPath()))
                && (!forbiddenParents.isValid() || !forbiddenParents.matches(resolver, targetTemplate.getPath())))
                && (template != null
                && (!allowedChildren.isValid() || allowedChildren.matches(resolver, template.getPath()))
                && (!forbiddenChildren.isValid() || !forbiddenChildren.matches(resolver, template.getPath()))));
        if (LOG.isDebugEnabled()) {
            LOG.debug("isAllowedChildByTemplate({},{}): {}\n    >{}({})\n    ({},{})\n    ({},{})",
                    template, resourceReference, isAllowed, designatedTarget, targetTemplate,
                    allowedParents, forbiddenParents, allowedChildren, forbiddenChildren);
        }
        return isAllowed;
    }

    public boolean isAllowedChildByType(@Nullable ResourceManager.Template template,
                                        @Nonnull ResourceManager.ResourceReference resourceReference) {
        ResourceResolver resolver = resourceReference.getResolver();
        PathPatternSet allowedParents = getAllowedParentTypes(template, resourceReference);
        PathPatternSet forbiddenParents = getForbiddenParentTypes(template, resourceReference);
        PathPatternSet allowedChildren = getAllowedChildTypes();
        PathPatternSet forbiddenChildren = getForbiddenChildTypes();
        boolean isAllowed = (targetTemplate == null
                || ((!allowedParents.isValid() || allowedParents.matches(resolver, resourceType))
                && (!forbiddenParents.isValid() || !forbiddenParents.matches(resolver, resourceType)))
                && (template != null
                && (!allowedChildren.isValid() || allowedChildren.matches(resolver, resourceReference.getType()))
                && (!forbiddenChildren.isValid() || !forbiddenChildren.matches(resolver, resourceReference.getType()))));
        if (LOG.isDebugEnabled()) {
            LOG.debug("isAllowedChildByType({},{}): {}\n    >{}({})\n    ({},{})\n    ({},{})",
                    template, resourceReference, isAllowed, designatedTarget, targetTemplate,
                    allowedParents, forbiddenParents, allowedChildren, forbiddenChildren);
        }
        return isAllowed;
    }

    @Nonnull
    protected PathPatternSet getAllowedParentTemplates(@Nullable ResourceManager.Template template,
                                                       @Nonnull ResourceManager.ResourceReference resourceReference) {
        return getAllowedTypes(template, resourceReference, PagesConstants.PROP_ALLOWED_PARENT_TEMPLATES);
    }

    @Nonnull
    protected PathPatternSet getForbiddenParentTemplates(@Nullable ResourceManager.Template template,
                                                         @Nonnull ResourceManager.ResourceReference resourceReference) {
        return getAllowedTypes(template, resourceReference, PagesConstants.PROP_FORBIDDEN_PARENT_TEMPLATES);
    }

    @Nonnull
    protected PathPatternSet getAllowedChildTemplates() {
        if (allowedChildTemplates == null) {
            allowedChildTemplates = getAllowedTypes(targetTemplate, designatedTarget,
                    PagesConstants.PROP_ALLOWED_CHILD_TEMPLATES);
        }
        return allowedChildTemplates;
    }

    @Nonnull
    protected PathPatternSet getForbiddenChildTemplates() {
        if (forbiddenChildTemplates == null) {
            forbiddenChildTemplates = getAllowedTypes(targetTemplate, designatedTarget,
                    PagesConstants.PROP_FORBIDDEN_CHILD_TEMPLATES);
        }
        return forbiddenChildTemplates;
    }

    @Nonnull
    protected PathPatternSet getAllowedParentTypes(@Nullable ResourceManager.Template template,
                                                   @Nonnull ResourceManager.ResourceReference resourceReference) {
        return getAllowedTypes(template, resourceReference, PagesConstants.PROP_ALLOWED_PARENT_TYPES);
    }

    @Nonnull
    protected PathPatternSet getForbiddenParentTypes(@Nullable ResourceManager.Template template,
                                                     @Nonnull ResourceManager.ResourceReference resourceReference) {
        return getAllowedTypes(template, resourceReference, PagesConstants.PROP_FORBIDDEN_PARENT_TYPES);
    }

    @Nonnull
    protected PathPatternSet getAllowedChildTypes() {
        if (allowedChildTypes == null) {
            allowedChildTypes = getAllowedTypes(targetTemplate, designatedTarget,
                    PagesConstants.PROP_ALLOWED_CHILD_TYPES);
        }
        return allowedChildTypes;
    }

    @Nonnull
    protected PathPatternSet getForbiddenChildTypes() {
        if (forbiddenChildTypes == null) {
            forbiddenChildTypes = getAllowedTypes(targetTemplate, designatedTarget,
                    PagesConstants.PROP_FORBIDDEN_CHILD_TYPES);
        }
        return forbiddenChildTypes;
    }

    @Nonnull
    protected PathPatternSet getAllowedTypes(@Nullable ResourceManager.Template template,
                                             @Nonnull ResourceManager.ResourceReference resourceReference,
                                             @Nonnull String propertyName) {
        PathPatternSet allowedTypes = null;
        if (resourceReference.isExisting()) {
            allowedTypes = new PathPatternSet(resourceReference.getResource().getValueMap().get(propertyName, String[].class));
        }
        if (allowedTypes == null || !allowedTypes.isValid()) {
            if (template != null) {
                allowedTypes = template.getTypePatterns(resolver, propertyName);
            }
        }
        if (allowedTypes == null || !allowedTypes.isValid()) {
            allowedTypes = new PathPatternSet(resourceReference, propertyName);
        }
        return allowedTypes;
    }
}
