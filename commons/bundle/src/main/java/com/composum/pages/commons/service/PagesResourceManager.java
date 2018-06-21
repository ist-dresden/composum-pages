package com.composum.pages.commons.service;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.filter.TemplateFilter;
import com.composum.pages.commons.model.AbstractModel;
import com.composum.pages.commons.model.ContentTypeFilter;
import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.model.properties.PathPatternSet;
import com.composum.pages.commons.util.ResolverUtil;
import com.composum.pages.commons.util.ValueHashMap;
import com.composum.platform.cache.service.CacheConfiguration;
import com.composum.platform.cache.service.CacheManager;
import com.composum.platform.cache.service.impl.CacheServiceImpl;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.filter.StringFilter;
import com.composum.sling.core.util.PropertyUtil;
import com.composum.sling.core.util.ResourceUtil;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.composum.pages.commons.PagesConstants.NODE_NAME_DESIGN;
import static com.composum.pages.commons.PagesConstants.PROP_TEMPLATE;
import static com.composum.pages.commons.PagesConstants.PROP_TEMPLATE_REF;
import static com.composum.pages.commons.PagesConstants.PROP_TYPE_PATTERNS;
import static com.composum.pages.commons.model.Page.isPage;

/**
 * the ResourceManager implementation of Pages handles templates and design rules of content resources and
 * provides some resource management operations such as create from template, copy, move, ... in the Pages
 * context (with reference transformation and property filtering)
 */
@Component(
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Resource Manager"
        }
)
@Designate(
        ocd = PagesResourceManager.Config.class
)
public class PagesResourceManager extends CacheServiceImpl<ResourceManager.Template> implements ResourceManager {

    protected static final Logger LOG = LoggerFactory.getLogger(PagesResourceManager.class);

    /**
     * the configuration for the template cache
     */
    @ObjectClassDefinition(
            name = "Pages Template Service Configuration"
    )
    public @interface Config {

        @AttributeDefinition(
                description = "the count maximum of templates stored in the cache"
        )
        int maxElementsInMemory() default 1000;

        @AttributeDefinition(
                description = "the validity period maximum for the cache entries in seconds"
        )
        int timeToLiveSeconds() default 600;

        @AttributeDefinition(
                description = "the validity period after last access of a cache entry in seconds"
        )
        int timeToIdleSeconds() default 300;

        @AttributeDefinition()
        String webconsole_configurationFactory_nameHint() default
                "Templates (heap: {maxElementsInMemory}, time: {timeToIdleSeconds}-{timeToLiveSeconds})";
    }

    protected final Template NO_TEMPLATE = new TemplateImpl();
    protected final Design NO_DESIGN = new DesignImpl(null, 0);
    public static final Serializable NO_VALUE = "";

    /** the template cache is registered as a cache od the platform cache manager */
    @Reference
    protected CacheManager cacheManager;

    protected Config config;

    /**
     * a template is used to create new resources (pages) wth predefined content (structure); the template
     * contains also content hierarchy rules for pages and component design rules for containers and elements;
     * a page created from a template is referencing this template and using the rules in content operations,
     * the rules are not copied to resources during creation from a template a reference property is set
     * instead of copying the rules; templates are cached to improve the performance of rule calculations
     */
    protected class TemplateImpl implements Template {

        // serializable properties only to support caching
        protected final String templatePath;
        protected final String resourceType;
        protected final Map<String, PathPatternSet> typePatternMap;
        protected final Map<String, Design> designCache;

        /**
         * A template can be a Site or Page template with a 'jcr:content' child resourece containing the template rules
         * but it can also be a simple resource with the template rules properties (e.g. for folder rules).
         */
        public TemplateImpl(@Nonnull Resource templateResource) {
            this.templatePath = templateResource.getPath();
            Resource contentChild = templateResource.getChild(JcrConstants.JCR_CONTENT);
            this.resourceType = contentChild.getResourceType();
            this.typePatternMap = new LinkedHashMap<>();
            this.designCache = new HashMap<>();
        }

        /** for the EMPTY instance only */
        protected TemplateImpl() {
            this.templatePath = null;
            this.resourceType = null;
            this.typePatternMap = null;
            this.designCache = null;
        }

        /**
         * @return the path of the template itself; the templates reference value
         */
        @Nonnull
        public String getPath() {
            return templatePath;
        }

        /**
         * @return the resource type of the template itself (of the jcr:content child)
         */
        @Nonnull
        public String getResourceType() {
            return resourceType;
        }

        /**
         * @return the templates content (jcr:content) as resource of the current resolver (not able to cache)
         */
        @Nonnull
        public Resource getContentResource(ResourceResolver resolver) {
            Resource resource = resolver.getResource(templatePath + "/" + JcrConstants.JCR_CONTENT);
            return resource != null ? resource : getTemplateResource(resolver);
        }

        /**
         * @return the templates resource of the current resolver (not able to cache)
         */
        @Nonnull
        public Resource getTemplateResource(ResourceResolver resolver) {
            return resolver.getResource(templatePath);
        }

        /**
         * @return the list od regex resource type patterns from a template property (the list is cached)
         */
        @Nonnull
        public PathPatternSet getTypePatterns(@Nonnull ResourceResolver resolver, @Nonnull String propertyName) {
            PathPatternSet types = typePatternMap.get(propertyName);
            if (types == null) {
                types = new PathPatternSet(getReference(getContentResource(resolver), null), propertyName);
                typePatternMap.put(propertyName, types);
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
         * @param resourceType the designated or overlayed resource type of the content element
         * @return the design model; 'null' if no design rules found
         */
        @Override
        @Nullable
        public Design getDesign(@Nonnull Resource pageContent,
                                @Nonnull String relativePath, @Nullable String resourceType) {
            String cacheKey = relativePath + "@" + pageContent.getResourceType();
            Design design = designCache.get(cacheKey);
            if (design == null) {
                ResourceResolver resolver = pageContent.getResourceResolver();
                design = findDesign(getContentResource(resolver), pageContent, relativePath, resourceType);
                designCache.put(cacheKey, design != null ? design : NO_DESIGN);
            }
            return design != NO_DESIGN ? design : null;
        }

        /**
         * The internal 'find' for a design is searching for the best matching 'cpp:design' content element of the template
         * and then searching for the best matching element node of the selected 'cpp:design' resource.
         *
         * @param templateContent the content resource of the template
         * @param pageContent     the content resource of the content elements page
         * @param relativePath    the elements path within the pages content
         * @param resourceType    the designated or overlayed resource type of the content element
         * @return the determined design; 'null' if n o appropriate design resource found
         */
        @Nullable
        protected Design findDesign(@Nonnull Resource templateContent, @Nonnull Resource pageContent,
                                    @Nonnull String relativePath, @Nullable String resourceType) {
            DesignImpl bestMatchingDesign = null;
            String designPath = relativePath;
            do {
                Resource templateElement = StringUtils.isNotBlank(designPath)
                        ? templateContent.getChild(designPath) : templateContent;
                if (templateElement != null) {
                    Resource contentElement = StringUtils.isNotBlank(designPath)
                            ? pageContent.getChild(designPath) : pageContent;
                    if (contentElement != null || StringUtils.isNotBlank(resourceType)) {
                        Resource designNode = templateElement.getChild(NODE_NAME_DESIGN);
                        if (designNode != null) {
                            // use 'resourceType' if present for the last path segment (potentially not existing)
                            String elementType = contentElement != null && (StringUtils.isBlank(resourceType)
                                    || !designPath.equals(relativePath)) ? contentElement.getResourceType() : resourceType;
                            if (isMatchingType(designNode, elementType)) {
                                String contentPath = StringUtils.isNotBlank(designPath)
                                        ? StringUtils.substring(relativePath, designPath.length() + 1) : relativePath;
                                int weight = StringUtils.countMatches(designPath, '/');
                                if (StringUtils.isNotBlank(designPath)) {
                                    weight++;
                                }
                                DesignImpl design = findDesign(designNode, contentElement, contentPath, resourceType, weight * 10);
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
         * @param resourceType the designated or overlayed resource type of the content element
         * @param weight       the current weight for a matching design resource
         * @return a matching design resource if found, otherwise 'null'
         */
        protected DesignImpl findDesign(Resource designNode, Resource contentNode,
                                        String relativePath, String resourceType, int weight) {
            String childName = StringUtils.substringBefore(relativePath, "/");
            Resource contentChild = contentNode.getChild(childName);
            if (contentChild != null || StringUtils.isNotBlank(resourceType)) {
                String contentPath = StringUtils.substringAfter(relativePath, "/");
                // use 'resourceType' if present for the last path segment (potentially not existing)
                String contentType = contentChild != null && (StringUtils.isNotBlank(contentPath)
                        || StringUtils.isBlank(resourceType)) ? contentChild.getResourceType() : resourceType;
                for (Resource designChild : designNode.getChildren()) {
                    if (isMatchingType(designChild, contentType)) {
                        if (StringUtils.isBlank(contentPath)) {
                            return new DesignImpl(designChild, weight);
                        } else {
                            return findDesign(designChild, contentChild, contentPath, resourceType, weight + 2);
                        }
                    }
                }
                if (StringUtils.isNotBlank(contentPath)) {
                    return findDesign(designNode, contentChild, contentPath, resourceType, weight);
                }
            }
            return null;
        }

        /**
         * @return 'true' if the resource type matches to the 'typePatterns' set of the design rule
         */
        protected boolean isMatchingType(Resource designNode, String resourceType) {
            PathPatternSet typePatterns = new PathPatternSet(getReference(designNode, null), PROP_TYPE_PATTERNS);
            return typePatterns.matches(resourceType);
        }
    }

    /**
     * the reference to a design rule of a content element; this is referencing the child resource of the
     * structure matching 'cpp:design' subnode of a template appropriate for a content element; such a design
     * is used to get design properties of an element, especially the content structure pattern sets
     * ('allowedContainers', 'allowedElements', ...)
     */
    public class DesignImpl implements Design {

        // serializable properties only to support caching
        protected final String path;
        protected final int weight;
        protected final Map<String, Serializable> propertyCache = new HashMap<>();

        protected DesignImpl(Resource resource, int weight) {
            this.path = resource != null ? resource.getPath() : null;
            this.weight = weight;
        }

        /**
         * @return the path of the desing itself
         */
        @Override
        @Nonnull
        public String getPath() {
            return path;
        }

        /**
         * @return the designs resource of the current resolver (not able to cache)
         */
        @Override
        @Nonnull
        public Resource getResource(@Nonnull ResourceResolver resolver) {
            return resolver.getResource(path);
        }

        /**
         * @return a property from the design; properties are cached in the Design instance
         */
        @SuppressWarnings("unchecked")
        @Override
        @Nullable
        public <T extends Serializable> T getProperty(@Nonnull ResourceResolver resolver,
                                                      @Nonnull String name, @Nonnull Class<T> type) {
            T value = (T) propertyCache.get(name);
            if (value == null) {
                value = getResource(resolver).getValueMap().get(name, type);
                propertyCache.put(name, value != null ? value : NO_VALUE);
            }
            return value != NO_VALUE ? value : null;
        }
    }

    /**
     * @return the template instance of a template resource
     */
    @Nonnull
    public Template toTemplate(@Nonnull Resource resource) {
        return new TemplateImpl(resource);
    }

    /**
     * @return the template referenced by the containing page of a resource
     */
    @Nullable
    public Template getTemplateOf(@Nullable Resource resource) {
        Template template = null;
        if (resource != null && !ResourceUtil.isNonExistingResource(resource)) {
            String path = resource.getPath();
            template = get(path);
            if (template == null) {
                template = findTemplateOf(resource);
                put(path, template != null ? template : NO_TEMPLATE);
            }
        }
        return template != NO_TEMPLATE ? template : null;
    }

    /**
     * retrieves the template referenced by the containing page of a resource
     */
    @Nullable
    protected Template findTemplateOf(@Nonnull Resource resource) {
        Template template = null;
        if (Site.isSite(resource)) {
            return getTemplateOf(resource.getChild(JcrConstants.JCR_CONTENT));
        } else if (Page.isPage(resource)) {
            return getTemplateOf(resource.getChild(JcrConstants.JCR_CONTENT));
        } else {
            String templatePath = resource.getValueMap().get(PagesConstants.PROP_TEMPLATE, "");
            if (StringUtils.isNotBlank(templatePath)) {
                Resource templateResource = resource.getResourceResolver().getResource(templatePath);
                if (templateResource != null && !ResourceUtil.isNonExistingResource(templateResource)) {
                    template = toTemplate(templateResource);
                }
            } else {
                if (!JcrConstants.JCR_CONTENT.equals(resource.getName())) {
                    Template parentTemplate = getTemplateOf(resource.getParent());
                    if (parentTemplate != null) {
                        ResourceResolver resolver = resource.getResourceResolver();
                        Resource templateChild = parentTemplate.getTemplateResource(resolver).getChild(resource.getName());
                        template = templateChild != null ? toTemplate(templateChild) : parentTemplate;
                    }
                }
            }
        }
        return template;
    }

    /**
     * the reference to a potentially non existing (static included; can exist but mustn't) content resource
     * this is a simple transferable (JSON) resource description without the overhead of a NonExistingResource and
     * with access to the resources properties (if resource exists), the design and the resource type properties
     * even if the resource ist not existing (to check rules during creation, moving and copying)
     */
    protected class ReferenceImpl implements ResourceReference {

        /** JSON attribute names */
        public static final String PATH = "path";
        public static final String TYPE = "type";

        /** the REFERENCE attributes */
        protected String path;
        protected String type;

        /** the resource determined by the path - can be a NonExistingResource */
        private transient Resource resource;
        /** the properties of the resource - an empty map if resource doesn't exist */
        private transient ValueMap resourceValues;
        /** the design of the resource reference if such a design is specified */
        private transient Design design;

        public final ResourceResolver resolver;

        // references can be built from various sources...

        protected ReferenceImpl(AbstractModel model) {
            this(model.getResource(), model.getType());
        }

        /** a resource and a probably overlayed type (type can be 'null') */
        protected ReferenceImpl(@Nonnull Resource resource, @Nullable String type) {
            this.resolver = resource.getResourceResolver();
            this.path = resource.getPath();
            this.type = StringUtils.isNotBlank(type) ? type : resource.getResourceType();
        }

        /** a reference simply created by the values */
        protected ReferenceImpl(@Nonnull ResourceResolver resolver, @Nonnull String path, @Nullable String type) {
            this.resolver = resolver;
            this.path = path;
            this.type = type;
        }

        /** a reference translated from a JSON object (transferred reference) */
        protected ReferenceImpl(ResourceResolver resolver, JsonReader reader) throws IOException {
            this.resolver = resolver;
            fromJson(reader);
        }

        public boolean isExisting() {
            return !ResourceUtil.isNonExistingResource(getResource());
        }

        @Nonnull
        public String getPath() {
            return path;
        }

        /**
         * @return the resource type if such a type is part of the reference, determines the type for
         * non existing resources or overlays the type of the referenced resource
         */
        @Nonnull
        public String getType() {
            return type;
        }

        /**
         * returns the property value using the cascade: resource - design - resource type;
         * no 18n support for this property value retrieval
         */
        @Nonnull
        public <T extends Serializable> T getProperty(@Nonnull String name, @Nonnull T defaultValue) {
            Class<T> type = PropertyUtil.getType(defaultValue);
            T value = getProperty(name, type);
            return value != null ? value : defaultValue;
        }

        /**
         * returns the property value using the cascade: resource - design - resource type;
         * no 18n support for this property value retrieval
         */
        @Nullable
        public <T extends Serializable> T getProperty(@Nonnull String name, @Nonnull Class<T> type) {
            T value = getResourceValues().get(name, type);
            if (value == null) {
                Design design = getDesign();
                if (design != null) {
                    value = design.getProperty(resolver, name, type);
                }
                if (value == null) {
                    value = ResolverUtil.getTypeProperty(resolver, getType(), name, type);
                }
            }
            return value;
        }

        /**
         * retrieves the design of this resource reference form the template of the containing or designated page
         */
        protected Design getDesign() {
            if (design == null) {
                Resource page = findContainingPageResource(this.getResource());
                if (page != null) {
                    Template template = findTemplateOf(page);
                    if (template != null) {
                        Resource pageContent = page.getChild(JcrConstants.JCR_CONTENT);
                        if (pageContent != null) {
                            String relativePath = getPath().substring(pageContent.getPath().length() + 1);
                            design = template.getDesign(pageContent, relativePath, getType());
                        }
                    }
                }
                if (design == null) {
                    design = NO_DESIGN;
                }
            }
            return design != NO_DESIGN ? design : null;
        }

        protected ValueMap getResourceValues() {
            if (resourceValues == null) {
                Resource resource = getResource();
                if (ResourceUtil.isNonExistingResource(resource)) {
                    resourceValues = new ValueHashMap();
                } else {
                    resourceValues = resource.adaptTo(ValueMap.class);
                }
            }
            return resourceValues;
        }

        @Nonnull
        public Resource getResource() {
            if (resource == null) {
                resource = resolver.resolve(getPath());
            }
            return resource;
        }

        // JSON transformation for the Pages editing UI

        public void fromJson(JsonReader reader) throws IOException {
            reader.beginObject();
            while (reader.peek() != JsonToken.END_OBJECT) {
                String name = reader.nextName();
                switch (name) {
                    case PATH:
                        path = reader.nextString();
                        break;
                    case TYPE:
                        type = reader.nextString();
                        break;
                    default:
                        reader.skipValue();
                }
            }
            reader.endObject();
        }

        public void toJson(JsonWriter writer) throws IOException {
            writer.beginObject();
            writer.name(PATH).value(path);
            writer.name(TYPE).value(type);
            writer.endObject();
        }

        public String toString() {
            return path + ":" + type;
        }
    }

    /**
     * a list of references (simple transferable as a JSON array) for the Pages editing UI
     */
    public class ReferenceListImpl extends ArrayList<ResourceReference> implements ReferenceList {

        public ReferenceListImpl() {
        }

        public ReferenceListImpl(ResourceManager.ResourceReference... references) {
            Collections.addAll(this, references);
        }

        public ReferenceListImpl(ResourceResolver resolver, String jsonValue) throws IOException {
            if (StringUtils.isNotBlank(jsonValue)) {
                try (StringReader string = new StringReader(jsonValue);
                     JsonReader reader = new JsonReader(string)) {
                    fromJson(resolver, reader);
                }
            }
        }

        public ReferenceListImpl(ResourceResolver resolver, JsonReader reader) throws IOException {
            fromJson(resolver, reader);
        }

        public void fromJson(ResourceResolver resolver, JsonReader reader) throws IOException {
            reader.beginArray();
            while (reader.peek() != JsonToken.END_ARRAY) {
                add(new ReferenceImpl(resolver, reader));
            }
            reader.endArray();
        }

        public void toJson(JsonWriter writer) throws IOException {
            writer.beginArray();
            for (ResourceManager.ResourceReference reference : this) {
                ((ReferenceImpl) reference).toJson(writer);
            }
            writer.endArray();
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("[");
            for (ResourceManager.ResourceReference reference : this) {
                if (builder.length() > 1) {
                    builder.append(",");
                }
                builder.append(reference);
            }
            builder.append("]");
            return builder.toString();
        }
    }

    //
    // resource reference factory methods; references are only useful in the context of the resource manager
    //

    /** the reference of the (existing) resource of a model instance */
    public ResourceReference getReference(AbstractModel model) {
        return new ReferenceImpl(model);
    }

    /** a resource and a probably overlayed type (type can be 'null') */
    public ResourceReference getReference(@Nonnull Resource resource, @Nullable String type) {
        return new ReferenceImpl(resource, type);
    }

    /**
     * a resource rewferenced by the path and a probably overlayed type; the type can be 'null' if the addressed
     * resource exists, the type should be present if the path is pointing to a non existing resource
     */
    public ResourceReference getReference(@Nonnull ResourceResolver resolver,
                                          @Nonnull String path, @Nullable String type) {
        return new ReferenceImpl(resolver, path, type);
    }

    /** a reference translated from a JSON object (transferred reference) */
    public ResourceReference getReference(ResourceResolver resolver, JsonReader reader) throws IOException {
        return new ReferenceImpl(resolver, reader);
    }

    public ReferenceList getReferenceList() {
        return new ReferenceListImpl();

    }

    public ReferenceList getReferenceList(ResourceManager.ResourceReference... references) {
        return new ReferenceListImpl(references);
    }

    public ReferenceList getReferenceList(ResourceResolver resolver, String jsonValue) throws IOException {
        return new ReferenceListImpl(resolver, jsonValue);
    }

    public ReferenceList getReferenceList(ResourceResolver resolver, JsonReader reader) throws IOException {
        return new ReferenceListImpl(resolver, reader);
    }

    /**
     * @return the resource of the containing page of a pages content element
     */
    @Nullable
    public Resource findContainingPageResource(Resource resource) {
        if (resource != null) {
            if (isPage(resource)) {
                return resource;
            } else {
                return findContainingPageResource(resource.getParent());
            }
        }
        return null;
    }

    //
    // content structure operations
    //

    /**
     * Checks the policies of the resource hierarchy for a given parent and child (for move and copy operations).
     *
     * @param resolver the resolver of the current request
     * @param parent   the designated parent resource
     * @param child    the resource to check
     * @return 'true' if the child could be a child of the given parent
     */
    @Override
    public boolean isAllowedChild(@Nonnull ResourceResolver resolver, @Nonnull Resource parent, @Nonnull Resource child) {
        String referencePath = parent.getPath() + "/" + child.getName();
        Resource childTypeResource = child;
        if (Page.isPage(child) || Site.isSite(child)) {
            childTypeResource = child.getChild(JcrConstants.JCR_CONTENT);
        }
        String referenceType = childTypeResource.getResourceType();
        ContentTypeFilter filter = new ContentTypeFilter(this, parent);
        return filter.isAllowedChild(getTemplateOf(child), getReference(resolver, referencePath, referenceType));
    }

    /**
     * Moves a resource and adopts all references to the moved resource or one of its children.
     *
     * @param resolver     the resolver (session context)
     * @param changeRoot   the root element for reference search and change
     * @param source       the resource to move
     * @param targetParent the target (the parent resource) of the move
     * @param newName      an optional new name for the resource
     * @param before       the designated sibling in an ordered target collection
     * @return the new resource at the target path
     */
    @Override
    @Nonnull
    public Resource moveContentResource(@Nonnull ResourceResolver resolver, @Nonnull Resource changeRoot,
                                        @Nonnull Resource source, @Nonnull Resource targetParent,
                                        @Nullable String newName, @Nullable Resource before)
            throws RepositoryException {

        Session session = resolver.adaptTo(Session.class);

        String oldPath = source.getPath();
        int lastSlash = oldPath.lastIndexOf('/');
        String oldParentPath = lastSlash == 0 ? "/" : oldPath.substring(0, lastSlash);
        String newParentPath = targetParent.getPath();
        String name = oldPath.substring(lastSlash + 1);
        boolean isAnotherParent = !oldParentPath.equals(newParentPath);

        // determine the name of the next sibling in the target resource (ordering)
        String siblingName = before != null ? before.getName() : null;
        if (StringUtils.isNotBlank(newName) && !isAnotherParent) {
            // preserve ordering on a simple rename
            Iterator<Resource> children = targetParent.listChildren();
            while (children.hasNext()) {
                if (children.next().getPath().equals(oldPath)) {
                    if (children.hasNext()) {
                        siblingName = children.next().getName();
                    }
                    break;
                }
            }
        }

        if (StringUtils.isBlank(newName)) {
            newName = name;
        }
        String newPath = newParentPath + "/" + newName;

        // check name collision before move into a new target resource; on the same parent let it fail
        if (isAnotherParent) {
            String n = newName;
            for (int i = 1; resolver.getResource(newPath) != null; i++) {
                newPath = newParentPath + "/" + (newName = n + i);
            }
        }

        if (LOG.isInfoEnabled()) {
            LOG.info("moveContentResource(" + oldPath + " > " + newPath + " < " + siblingName + ")...");
        }

        // move it if it is a real move and adjust all references
        if (isAnotherParent || !newName.equals(name)) {
            session.move(oldPath, newPath);
            ArrayList<Resource> foundReferers = new ArrayList<>();
            // adopt all references to the source and use the new target path
            changeReferences(ResourceFilter.ALL, StringFilter.ALL, changeRoot, foundReferers, false, oldPath, newPath);
        }

        // move to the designated position in the target collection
        try {
            session.refresh(true);
            Node parentNode = session.getNode(newParentPath);
            parentNode.orderBefore(newName, siblingName);
        } catch (UnsupportedRepositoryOperationException ignore) {
            // ordering not supported - ignore it
        }

        return resolver.getResource(newPath);
    }

    //
    // adjust referenced (paths) on move operations
    //

    /**
     * Changes the 'oldPath' references in each property of a tree to the 'newPath'.
     *
     * @param resourceFilter change all resources accepted by this filter, let all other resources unchanged
     * @param propertyFilter change only the properties with names matching to this property name filter
     * @param resource       the resource to change (recursive! - the root in the initial call)
     * @param foundReferrers the List of referers found (to fill during traversal, empty in the initial call)
     * @param scanOnly       if 'true' no changes are made but the referer list is filled
     * @param oldPath        the old path of a moved resource
     * @param newPath        the new path of the resource
     */
    @Override
    public void changeReferences(@Nonnull ResourceFilter resourceFilter, @Nonnull StringFilter propertyFilter,
                                 @Nonnull Resource resource, @Nonnull List<Resource> foundReferrers, boolean scanOnly,
                                 @Nonnull String oldPath, @Nonnull String newPath) {
        // check resource filter
        if (resourceFilter.accept(resource)) {
            boolean resourceChanged = false;

            ModifiableValueMap values = resource.adaptTo(ModifiableValueMap.class);
            for (Map.Entry<String, Object> entry : values.entrySet()) {

                String key = entry.getKey();
                // check property by name
                if (propertyFilter.accept(key)) {

                    Object value = entry.getValue();
                    if (value instanceof String) {
                        // change single string values (probably rich text)
                        String newValue = changeReferences((String) value, oldPath, newPath);
                        if (newValue != null) {
                            if (!scanOnly) {
                                values.put(key, newValue);
                            }
                            resourceChanged = true;
                        }

                    } else if (value instanceof String[]) {
                        // change each value of a multi string (probably rich text)
                        boolean changed = false;
                        List<String> newList = new ArrayList<>();
                        for (String val : (String[]) value) {
                            String newValue = changeReferences(val, oldPath, newPath);
                            if (newValue != null) {
                                newList.add(newValue);
                                changed = true;
                                if (scanOnly) {
                                    break;
                                }
                            } else {
                                newList.add(val);
                            }
                        }
                        if (changed) {
                            // perform a change if one value is changed
                            if (!scanOnly) {
                                values.put(key, newList.toArray());
                            }
                            resourceChanged = true;
                        }
                    }
                }

                if (resourceChanged && scanOnly) {
                    break;
                }
            }
            if (resourceChanged) {
                foundReferrers.add(resource);
            }

            // recursive traversal
            for (Resource child : resource.getChildren()) {
                changeReferences(resourceFilter, propertyFilter, child, foundReferrers, scanOnly, oldPath, newPath);
            }
        }
    }

    /**
     * Changes all references to 'oldPath' and use the 'newPath' in simple values or rich text strings.
     *
     * @param value   the string value (probably rich text)
     * @param oldPath the old path of the references to change
     * @param newPath the new path value
     * @return the changed value if changed otherwise 'null'
     */
    protected String changeReferences(String value, String oldPath, String newPath) {
        if (value.matches("^" + oldPath + "(/.*)?$")) {
            // simple path value...
            return newPath + value.substring(oldPath.length());
        } else {
            // check for HTML patterns and change all references if found
            Pattern htmlPattern = Pattern.compile("(href|src)=\"" + oldPath + "(/[^\"]*)?\"");
            Matcher matcher = htmlPattern.matcher(value);
            if (matcher.matches()) {
                return matcher.replaceAll("$1=\"" + newPath + "$2\"");
            }
        }
        return null;
    }

    /**
     * Changes the 'oldTypePattern' resource types in every appropriate component using the 'newTypeRule'.
     *
     * @param resourceFilter change all resources accepted by this filter, let all other resources unchanged
     * @param resource       the resource to change (recursive! - the root in the initial call)
     * @param oldTypePattern the resource type pattern to change
     * @param newTypeRule    the pattern matcher rule to build the new type
     */
    @Override
    public void changeResourceType(@Nonnull ResourceFilter resourceFilter, @Nonnull Resource resource,
                                   @Nonnull String oldTypePattern, @Nonnull String newTypeRule) {
        changeResourceType(resourceFilter, resource, Pattern.compile(oldTypePattern), newTypeRule);
    }

    /**
     * Changes the 'oldTypePattern' resource types in every appropriate component using the 'newTypeRule'.
     *
     * @param resourceFilter change all resources accepted by this filter, let all other resources unchanged
     * @param resource       the resource to change (recursive! - the root in the initial call)
     * @param oldTypePattern the resource type pattern to change
     * @param newTypeRule    the pattern matcher rule to build the new type
     */
    public void changeResourceType(@Nonnull ResourceFilter resourceFilter, @Nonnull Resource resource,
                                   @Nonnull Pattern oldTypePattern, @Nonnull String newTypeRule) {
        // check resource filter
        if (resourceFilter.accept(resource)) {

            ModifiableValueMap values = resource.adaptTo(ModifiableValueMap.class);
            String resourceType = values.get(ResourceUtil.PROP_RESOURCE_TYPE, "");
            if (StringUtils.isNotBlank(resourceType)) {
                Matcher matcher = oldTypePattern.matcher(resourceType);
                if (matcher.matches()) {
                    String newResourceType = matcher.replaceAll(newTypeRule);
                    if (!resourceType.equals(newResourceType)) {
                        if (LOG.isInfoEnabled()) {
                            LOG.info("changeResourceType(" + resource.getPath() + "): "
                                    + resourceType + " -> " + newResourceType);
                        }
                        values.put(ResourceUtil.PROP_RESOURCE_TYPE, newResourceType);
                    }
                }
            }

            // recursive traversal
            for (Resource child : resource.getChildren()) {
                changeResourceType(resourceFilter, child, oldTypePattern, newTypeRule);
            }
        }
    }

    //
    // content templates and copies
    //

    /**
     * the 'transform nothing' context used in case of a copy operation
     */
    public static class NopTemplateContext implements TemplateContext {

        protected final ResourceResolver resolver;

        public NopTemplateContext(ResourceResolver resolver) {
            this.resolver = resolver;
        }

        @Override
        public ResourceResolver getResolver() {
            return resolver;
        }

        @Override
        public String applyTemplatePlaceholders(@Nonnull Resource target, @Nonnull String value) {
            return value;
        }
    }

    /**
     * Creates a new resource as a copy of another resource.
     *
     * @param resolver the resolver to use for CRUD operations
     * @param template the template content resource
     * @param parent   the parent resource for the new resource
     * @param name     the name of the new resource
     * @param before   the designated sibling in an ordered target collection
     * @return the resource created
     * @throws PersistenceException if an error is occurring
     */
    @Override
    public Resource copyContentResource(@Nonnull ResourceResolver resolver, @Nonnull Resource template,
                                        @Nonnull Resource parent, @Nonnull String name, @Nullable Resource before)
            throws PersistenceException {
        TemplateContext templateContext = new NopTemplateContext(resolver);
        ValueMap templateValues = template.getValueMap();
        Resource target = resolver.create(parent, name, Collections.singletonMap(
                JcrConstants.JCR_PRIMARYTYPE, templateValues.get(JcrConstants.JCR_PRIMARYTYPE)));
        Resource templateContent = template.getChild(JcrConstants.JCR_CONTENT);

        if (templateContent != null) {
            // create the 'jcr:content' child if the template contains such a child
            ValueMap contentValues = templateContent.getValueMap();
            String primaryContentType = (String) contentValues.get(JcrConstants.JCR_PRIMARYTYPE);
            Resource targetContent = resolver.create(target, JcrConstants.JCR_CONTENT, Collections.singletonMap(
                    JcrConstants.JCR_PRIMARYTYPE, (Object) primaryContentType));
            ModifiableValueMap targetValues = targetContent.adaptTo(ModifiableValueMap.class);
            applyContentTemplate(templateContext, templateContent, targetContent, false, false);
        } else {
            applyTemplateProperties(templateContext, template, target, false);
        }
        // create a full structure copy of the template
        for (Resource child : template.getChildren()) {
            String childName = child.getName();
            // maybe the child is always created by the referenced template
            if (!JcrConstants.JCR_CONTENT.equals(childName) && target.getChild(childName) == null) {
                copyContentResource(resolver, child, target, childName, null);
            }
        }
        return target;
    }

    /**
     * @return 'true' if the resource is a content template
     */
    @Override
    public boolean isTemplate(@Nonnull Resource resource) {
        ResourceResolver resolver = resource.getResourceResolver();
        String tenant = null; // TODO tenant support
        String path = resource.getPath();
        for (String root : resolver.getSearchPath()) {
            if (path.startsWith(StringUtils.isNotBlank(tenant) ? root + tenant : root)) {
                return TemplateFilter.INSTANCE.accept(resource);
            }
        }
        return false;
    }

    /**
     * the node filter to prevent from copying template rules to content targets
     */
    public static class TemplateCopyFilter implements ResourceFilter {

        @Override
        public boolean accept(Resource resource) {
            String name = resource.getName();
            return !NODE_NAME_DESIGN.equals(name);
        }

        @Override
        public boolean isRestriction() {
            return true;
        }

        @Override
        public void toString(StringBuilder builder) {
            builder.append(getClass().getSimpleName());
        }
    }

    public static final ResourceFilter TEMPLATE_COPY_FILTER = new TemplateCopyFilter();

    /**
     * Creates a new resource as a copy of a template. If content nodes of such a template are referencing
     * other templates by a 'template' property the content of these referenced templates is copied
     * (used in site templates to reference the normal page templates of the site inside of a site template).
     *
     * @param context             the resolver to use for CRUD operations
     * @param parent              the parent resource for the new resource
     * @param name                the name of the new resource
     * @param template            the template content resource
     * @param setTemplateProperty if 'true' the 'template' property is filled with the template path
     * @return the resource created
     * @throws PersistenceException if an error is occurring
     */
    @Override
    public Resource createFromTemplate(@Nonnull TemplateContext context, @Nonnull Resource parent, @Nonnull String name,
                                       @Nonnull Resource template, boolean setTemplateProperty)
            throws PersistenceException {
        ResourceResolver resolver = context.getResolver();
        ValueMap templateValues = template.getValueMap();
        Resource target = resolver.create(parent, name, Collections.singletonMap(
                JcrConstants.JCR_PRIMARYTYPE, templateValues.get(JcrConstants.JCR_PRIMARYTYPE)));
        Resource templateContent = template.getChild(JcrConstants.JCR_CONTENT);
        Resource referencedTemplate = null;

        if (templateContent != null) {
            // create the 'jcr:content' child if the template contains such a child
            ValueMap contentValues = templateContent.getValueMap();
            String templateRef = contentValues.get(PROP_TEMPLATE_REF, "");

            if (StringUtils.isNotBlank(templateRef)) {
                // if the templates 'jcr:content' resource has a property 'template'
                // use the template referenced by this property instead if this template exists
                referencedTemplate = resolver.getResource(templateRef);
                if (referencedTemplate != null) {
                    Resource referencedContent = referencedTemplate.getChild(JcrConstants.JCR_CONTENT);
                    if (referencedContent != null) {
                        templateContent = referencedContent;
                        contentValues = templateContent.getValueMap();
                    }
                }
            }
            // create the 'jcr:content' child and mark this resource with the path of the used template
            String primaryContentType = (String) contentValues.get(JcrConstants.JCR_PRIMARYTYPE);
            Resource targetContent = resolver.create(target, JcrConstants.JCR_CONTENT, Collections.singletonMap(
                    JcrConstants.JCR_PRIMARYTYPE, (Object) primaryContentType));
            ModifiableValueMap targetValues = targetContent.adaptTo(ModifiableValueMap.class);
            applyContentTemplate(context, templateContent, targetContent, false, true);

            // prevent from unwanted properties in raw node types...
            if (setTemplateProperty && !primaryContentType.startsWith("nt:")) {
                if (targetValues.get(PROP_TEMPLATE) == null) {
                    // write template only if not always set by the template properties
                    targetValues.put(PROP_TEMPLATE, templateContent.getParent().getPath());
                }
            }
        } else {
            applyTemplateProperties(context, template, target, false);
        }
        if (referencedTemplate != null) {
            // create a full structure copy of the referenced template
            for (Resource child : template.getChildren()) {
                String childName = child.getName();
                if (!JcrConstants.JCR_CONTENT.equals(childName) && TEMPLATE_COPY_FILTER.accept(child)) {
                    createFromTemplate(context, target, child.getName(), child, setTemplateProperty);
                }
            }
        }
        // create a full structure copy of the template
        for (Resource child : template.getChildren()) {
            String childName = child.getName();
            if (!JcrConstants.JCR_CONTENT.equals(childName) && TEMPLATE_COPY_FILTER.accept(child)
                    // maybe the child is always created by the referenced template
                    && target.getChild(childName) == null) {
                createFromTemplate(context, target, childName, child, setTemplateProperty);
            }
        }
        return target;
    }

    /**
     * Applies the content resource data from a template content resource to a target content resource.
     *
     * @param context  the resolver to use for CRUD operations
     * @param template the template content resource
     * @param target   the target content resource
     * @param merge    if 'true' the target properties will be unmodified and all new aspects from the template will be added
     * @throws PersistenceException if an error is occurring
     */
    protected void applyContentTemplate(@Nonnull TemplateContext context, @Nonnull Resource template,
                                        @Nonnull Resource target, boolean merge, boolean filter)
            throws PersistenceException {
        ResourceResolver resolver = context.getResolver();
        if (!merge) {
            // in case of a 'reset' remove all children from the target resource
            for (Resource child : target.getChildren()) {
                resolver.delete(child);
            }
        }
        applyTemplateProperties(context, template, target, merge);
        for (Resource child : template.getChildren()) {
            // apply the template recursive...
            if (!filter || TEMPLATE_COPY_FILTER.accept(child)) {
                String name = child.getName();
                Resource targetChild = null;
                if (merge) {
                    targetChild = target.getChild(name);
                }
                if (targetChild == null) {
                    ValueMap childValues = child.getValueMap();
                    targetChild = resolver.create(target, name, Collections.singletonMap(
                            JcrConstants.JCR_PRIMARYTYPE, childValues.get(JcrConstants.JCR_PRIMARYTYPE)));
                }
                applyContentTemplate(context, child, targetChild, merge, filter);
            }
        }
    }

    /**
     * Applies the resource properties from a template resource to a target resource.
     *
     * @param context  the resolver to use for CRUD operations
     * @param template the template content resource
     * @param target   the target content resource
     * @param merge    if 'true' the target properties will be unmodified and all new aspects from the template will be added
     */
    protected void applyTemplateProperties(@Nonnull TemplateContext context, @Nonnull Resource template,
                                           @Nonnull Resource target, boolean merge) {
        ResourceResolver resolver = context.getResolver();
        ModifiableValueMap values = target.adaptTo(ModifiableValueMap.class);
        ValueMap templateValues = template.getValueMap();
        if (!merge) {
            // in case of a 'reset' remove all properties from the target resource
            for (String key : values.keySet().toArray(new String[0])) {
                if (!TEMPLATE_TARGET_KEEP.accept(key)) {
                    values.remove(key);
                }
            }
        }
        for (Map.Entry<String, Object> entry : templateValues.entrySet()) {
            String key = entry.getKey();
            if (TEMPLATE_PROPERTY_FILTER.accept(key)) {
                // copy template properties if not always present or a 'reset' is requested
                if (values.get(key) == null || (!merge && !TEMPLATE_TARGET_KEEP.accept(key))) {
                    Object value = entry.getValue();
                    if (value instanceof String) {
                        value = context.applyTemplatePlaceholders(target, (String) value);
                    }
                    if (value != null) {
                        values.put(key, value);
                    }
                }
            }
        }
    }

    //
    // template cache initialization
    //

    @SuppressWarnings("ClassExplicitlyAnnotation")
    protected class CacheConfig implements CacheConfiguration {

        @Override
        public boolean enabled() {
            return true;
        }

        @Override
        public String name() {
            return "Templates";
        }

        @Override
        public String contentType() {
            return Template.class.getName();
        }

        @Override
        public int maxElementsInMemory() {
            return config.maxElementsInMemory();
        }

        @Override
        public int timeToLiveSeconds() {
            return config.timeToLiveSeconds();
        }

        @Override
        public int timeToIdleSeconds() {
            return config.timeToIdleSeconds();
        }

        @Override
        public String webconsole_configurationFactory_nameHint() {
            return config.webconsole_configurationFactory_nameHint();
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return config.annotationType();
        }
    }

    @Activate
    @Modified
    public void activate(final Config config) {
        this.config = config;
        super.activate(cacheManager, new CacheConfig());
    }

    @Deactivate
    public void deactivate() {
        super.deactivate();
        config = null;
    }

}