package com.composum.pages.commons.util;

import com.composum.sling.core.util.ResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResolverUtil {

    public static final Pattern URL_PATH_PATTERN =
            Pattern.compile("^(([\\w]+:)?//[^/:]+(:[\\d]+)?)?(/[^?]*)(\\?[^#]*(#.*)?)?$");

    /**
     * Resolves a resource addressed by a URL (probably with parameters, ...).
     *
     * @return the resource if the path of the url can be resolved as an existing resource
     */
    @Nullable
    public static Resource getUrlResource(ResourceResolver resolver, String pageUrl) {
        Resource resource = resolver.resolve(pageUrl);
        if (ResourceUtil.isNonExistingResource(resource)) {
            Matcher matcher = URL_PATH_PATTERN.matcher(pageUrl);
            if (matcher.matches()) {
                String path = matcher.group(4);
                Resource res = resolver.resolve(path);
                if (ResourceUtil.isNonExistingResource(res)) {
                    int namePos = path.lastIndexOf('/') + 1;
                    int extPos = path.lastIndexOf('.', namePos);
                    if (extPos > namePos) {
                        res = resolver.resolve(path.substring(0, extPos));
                    }
                }
                if (!ResourceUtil.isNonExistingResource(res)) {
                    resource = res;
                }
            }
        }
        return ResourceUtil.isNonExistingResource(resource) ? null : resource;
    }

    public static <T> T getTypeProperty(Resource resource, String type, String name, T defaultValue) {
        return getTypeProperty(getResourceType(resource, type), name, defaultValue);
    }

    public static <T> T getTypeProperty(Resource resource, String type, String name, Class<T> valueType) {
        return getTypeProperty(getResourceType(resource, type), name, valueType);
    }

    public static <T> T getTypeProperty(ResourceResolver resolver, String type, String name, T defaultValue) {
        return getTypeProperty(getResourceType(resolver, type), name, defaultValue);
    }

    public static <T> T getTypeProperty(ResourceResolver resolver, String type, String name, Class<T> valueType) {
        return getTypeProperty(getResourceType(resolver, type), name, valueType);
    }

    public static <T> T getTypeProperty(Resource typeResource, String name, T defaultValue) {
        Class<?> valueType = defaultValue != null ? defaultValue.getClass() : String.class;
        T value = (T) getTypeProperty(typeResource, name, valueType);
        return value != null ? value : defaultValue;
    }

    public static <T> T getTypeProperty(Resource typeResource, String name, Class<T> type) {
        T value = null;
        if (typeResource != null) {
            ResourceResolver resolver = typeResource.getResourceResolver();
            while (typeResource != null && value == null) {
                ValueMap values = typeResource.getValueMap();
                value = values.get(name, type);
                typeResource = getResourceType(resolver, typeResource.getResourceSuperType());
            }
        }
        return value;
    }

    /**
     * Retrieves the resource of a 'subtype' of the resources resource type
     * along the resource type inheritance hierarchy.
     *
     * @return the 'subtypes' resource or 'null' if not found
     */
    public static Resource getResourceType(Resource resource, String type, String subtype) {
        Resource typeResource = null;
        if (resource != null) {
            ResourceResolver resolver = resource.getResourceResolver();
            Resource resourceType = getResourceType(resource, type);
            while (resourceType != null && typeResource == null) {
                typeResource = resolver.getResource(resourceType, subtype);
                if (typeResource == null) {
                    resourceType = getResourceType(resolver, resourceType.getResourceSuperType());
                }
            }
        }
        return typeResource;
    }

    public static Resource getResourceType(ResourceResolver resolver, String type, String subtype) {
        Resource typeResource = null;
        Resource resourceType = resolver.getResource(type);
        while (resourceType != null && typeResource == null) {
            typeResource = resolver.getResource(resourceType, subtype);
            if (typeResource == null) {
                resourceType = getResourceType(resolver, resourceType.getResourceSuperType());
            }
        }
        return typeResource;
    }

    public static Resource getResourceType(Resource resource, String type) {
        Resource typeResource = null;
        if (resource != null) {
            ResourceResolver resolver = resource.getResourceResolver();
            if (StringUtils.isNotBlank(type)) {
                typeResource = getResourceType(resolver, type);
            }
            if (typeResource == null) {
                String typeOfResource = resource.getResourceType();
                if (StringUtils.isNotBlank(typeOfResource)) {
                    typeResource = getResourceType(resolver, typeOfResource);
                }
            }
        }
        return typeResource;
    }

    /**
     * Retrieves the resource of a resource type by the declared search path for resource types.
     *
     * @return the types resource or 'null' if not found
     */
    public static Resource getResourceType(ResourceResolver resolver, String resourceType) {
        Resource typeResource = null;
        if (StringUtils.isNotBlank(resourceType)) {
            for (String root : resolver.getSearchPath()) {
                Resource baseResource = resolver.getResource(root);
                typeResource = resolver.getResource(baseResource, resourceType);
                if (typeResource != null) {
                    if (ResourceUtil.isSyntheticResource(typeResource)) {
                        typeResource = null;
                    }
                } else {
                    typeResource = resolver.getResource(resourceType);
                }
                if (typeResource != null) {
                    break;
                }
            }
        }
        return typeResource;
    }

    /**
     * @return the resource type of a components path (without the resolvers search root)
     */
    public static String toResourceType(ResourceResolver resolver, String componentPath) {
        for (String root : resolver.getSearchPath()) {
            if (componentPath.startsWith(root)) {
                return componentPath.substring(root.length());
            }
        }
        return componentPath;
    }

    // page templates

    /**
     * Retrieves the resource of a template using the declared search path of the resolver.
     *
     * @return the templates resource or 'null' if not found
     */
    @Nullable
    public static Resource getTemplate(@Nonnull final ResourceResolver resolver,
                                       @Nullable final String pageTemplate) {
        Resource template = null;
        if (StringUtils.isNotBlank(pageTemplate)) {
            if (!pageTemplate.startsWith("/")) {
                for (String root : resolver.getSearchPath()) {
                    Resource baseResource = resolver.getResource(root);
                    template = resolver.getResource(root + pageTemplate);
                    if (template != null) {
                        break;
                    }
                }
            }
            if (template == null) {
                template = resolver.getResource(pageTemplate);
            }
        }
        return template;
    }

    /**
     * @return the template path (the reference value) of a template resource (without a resolvers search root)
     */
    @Nullable
    public static String toPageTemplate(@Nonnull final ResourceResolver resolver,
                                        @Nullable final String templatePath) {
        if (StringUtils.isNotBlank(templatePath)) {
            for (String root : resolver.getSearchPath()) {
                if (templatePath.startsWith(root)) {
                    return templatePath.substring(root.length());
                }
            }
        }
        return templatePath;
    }
}
