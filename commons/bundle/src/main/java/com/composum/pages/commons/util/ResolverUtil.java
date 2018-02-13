package com.composum.pages.commons.util;

import com.composum.sling.core.util.ResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

public class ResolverUtil {

    public static <T> T getTypeProperty(Resource resource, String type, String name, T defaultValue) {
        return getTypeProperty(getResourceType(resource, type), name, defaultValue);
    }

    public static <T> T getTypeProperty(ResourceResolver resolver, String type, String name, T defaultValue) {
        return getTypeProperty(getResourceType(resolver, type), name, defaultValue);
    }

    public static <T> T getTypeProperty(Resource typeResource, String name, T defaultValue) {
        T value = null;
        if (typeResource != null) {
            ResourceResolver resolver = typeResource.getResourceResolver();
            Class<?> valueType = defaultValue != null ? defaultValue.getClass() : String.class;
            while (typeResource != null && value == null) {
                ValueMap values = typeResource.adaptTo(ValueMap.class);
                value = (T) values.get(name, valueType);
                typeResource = getResourceType(resolver, typeResource.getResourceSuperType());
            }
        }
        return value != null ? value : defaultValue;
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
            for (String base : resolver.getSearchPath()) {
                Resource baseResource = resolver.getResource(base);
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
}
