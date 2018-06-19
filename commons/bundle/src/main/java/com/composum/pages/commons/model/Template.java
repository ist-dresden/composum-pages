package com.composum.pages.commons.model;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.properties.PathPatternSet;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.vault.util.JcrConstants;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Template {

    public static final Template EMPTY = new Template();

    protected final Resource templateResource;
    protected final Resource contentResource;
    protected final Map<String, PathPatternSet> allowedTypes;
    protected final Map<String, Design> designCache;

    /**
     * A template can be a Site or Page template with a 'jcr:content' child resourece containing the template rules
     * but it can also be a simple resource with the template rules properties (e.g. for folder rules).
     */
    public Template(@Nonnull Resource templateResource) {
        this.templateResource = templateResource;
        Resource contentChild = templateResource.getChild(JcrConstants.JCR_CONTENT);
        this.contentResource = contentChild != null ? contentChild : templateResource;
        this.allowedTypes = new LinkedHashMap<>();
        this.designCache = new HashMap<>();
    }

    /** for the EMPTY instance only */
    protected Template() {
        this.templateResource = null;
        this.contentResource = null;
        this.allowedTypes = null;
        this.designCache = null;
    }

    public String getPath() {
        return templateResource.getPath();
    }

    public String getResourceType() {
        return contentResource.getResourceType();
    }

    public Resource getContentResource() {
        return contentResource;
    }

    public Resource getTemplateResource() {
        return templateResource;
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

    // design configuration

    /**
     * Retrieves es the design rules for an element of a page; the design rules are configured as elements
     * of a 'cpp:design' child node of the templates content resource.
     *
     * @param pageContent  the content resource of the page
     * @param relativePath the path of the element relative to the pages content resource
     * @return the design model; 'null' if no design rules found
     */
    public Design getDesign(Resource pageContent, String relativePath) {
        String cacheKey = relativePath + "@" + pageContent.getResourceType();
        Design design = designCache.get(cacheKey);
        if (design == null) {
            design = findDesign(getContentResource(), pageContent, relativePath);
            designCache.put(cacheKey, design != null ? design : Design.EMPTY);
        }
        return design != Design.EMPTY ? design : null;
    }

    /**
     * The internal 'find' for a design is searching for the best matching 'cpp:design' content element of the template
     * and then searching for the best matching element node of the selected 'cpp:design' resource.
     *
     * @param templateContent the content resource of the template
     * @param pageContent     the content resource of the content elements page
     * @param relativePath    the elements path within the pages content
     * @return the determined design; 'null' if n o appropriate design resource found
     */
    protected Design findDesign(Resource templateContent, Resource pageContent, String relativePath) {
        Design bestMatchingDesign = null;
        String designPath = relativePath;
        do {
            Resource templateElement = StringUtils.isNotBlank(designPath)
                    ? templateContent.getChild(designPath) : templateContent;
            if (templateElement != null) {
                Resource contentElement = StringUtils.isNotBlank(designPath)
                        ? pageContent.getChild(designPath) : pageContent;
                if (contentElement != null) {
                    Resource designNode = templateElement.getChild("cpp:design");
                    if (designNode != null) {
                        String resourceType = contentElement.getResourceType();
                        if (isMatchingType(designNode, resourceType)) {
                            String contentPath = StringUtils.isNotBlank(designPath)
                                    ? StringUtils.substring(relativePath, designPath.length() + 1) : relativePath;
                            int weight = StringUtils.countMatches(designPath, '/');
                            if (StringUtils.isNotBlank(designPath)) {
                                weight++;
                            }
                            Design design = findDesign(designNode, contentElement, contentPath, weight * 10);
                            if (design != null) {
                                if (bestMatchingDesign == null || design.weight > bestMatchingDesign.weight) {
                                    bestMatchingDesign = design;
                                }
                            }
                        }
                    }
                }
            }
            if (StringUtils.isBlank(designPath)) {
                designPath = null;
            } else {
                int lastSlash = designPath.lastIndexOf('/');
                designPath = lastSlash > 0 ? designPath.substring(0, lastSlash) : "";
            }
        } while (designPath != null);
        return bestMatchingDesign;
    }

    /**
     * Determines the element of a 'cpp:design' resource which is matching to a content element;
     * recursive traversal through the design hierarchy.
     *
     * @param designNode   the current design resource element
     * @param contentNode  the current content resource base of the traversal
     * @param relativePath the relative path the the content element
     * @param weight       the current weight for a matching design resource
     * @return a matching design resource if found, otherwise 'null'
     */
    protected Design findDesign(Resource designNode, Resource contentNode, String relativePath, int weight) {
        String childName = StringUtils.substringBefore(relativePath, "/");
        Resource contentChild = contentNode.getChild(childName);
        if (contentChild != null) {
            String contentPath = StringUtils.substringAfter(relativePath, "/");
            String resourceType = contentChild.getResourceType();
            for (Resource designChild : designNode.getChildren()) {
                if (isMatchingType(designChild, resourceType)) {
                    if (StringUtils.isBlank(contentPath)) {
                        return new Design(designChild, weight);
                    } else {
                        return findDesign(designChild, contentChild, contentPath, weight + 2);
                    }
                }
            }
            if (StringUtils.isNotBlank(contentPath)) {
                return findDesign(designNode, contentChild, contentPath, weight);
            }
        }
        return null;
    }

    /**
     * @return 'true' if the resource type matches to the 'typePatterns' set of the design rule
     */
    protected boolean isMatchingType(Resource designNode, String resourceType) {
        PathPatternSet typePatterns = new PathPatternSet(new ResourceReference(designNode, null), "typePatterns");
        return typePatterns.matches(resourceType);
    }
}
