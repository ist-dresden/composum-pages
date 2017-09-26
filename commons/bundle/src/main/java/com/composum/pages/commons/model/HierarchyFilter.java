package com.composum.pages.commons.model;

import com.composum.pages.commons.model.properties.AllowedTypes;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.HashMap;
import java.util.Map;

/**
 * a filter to determine the allowed elements for a set of containers (all containers of a page)
 */
public class HierarchyFilter {

    /** the set of containers to use for allowed element check */
    protected final ResourceReference.List containerList;

    protected final String allowedParentsPropertyName;
    protected final String allowedChildrenPropertyName;

    /** caches for the 'allowed...' properties */
    protected Map<String, AllowedTypes> allowedElements = new HashMap<>();
    protected Map<String, AllowedTypes> allowedContainers = new HashMap<>();

    protected final ResourceResolver resolver;

    public HierarchyFilter(final ResourceResolver resolver, final ResourceReference.List containerList,
                           String allowedParentsPropertyName, String allowedChildrenPropertyName) {
        this.resolver = resolver;
        this.containerList = containerList;
        this.allowedParentsPropertyName = allowedParentsPropertyName;
        this.allowedChildrenPropertyName = allowedChildrenPropertyName;
    }

    // check by resource reference (probably an existing resource)

    /**
     * returns 'true' if the element can be inserted into the specified container
     */
    public boolean isAllowedElement(final ResourceReference element, final ResourceReference container) {
        final AllowedTypes allowedEl = getAllowedTypes(allowedElements, allowedChildrenPropertyName, container);
        if (allowedEl.matches(element.getType())) {
            final AllowedTypes allowedCont = getAllowedTypes(allowedContainers, allowedParentsPropertyName, element);
            if (allowedCont.matches(container.getType())) {
                return true;
            }
        }
        return false;
    }

    /**
     * returns 'true' if the element can be inserted into one of the containers
     */
    public boolean isAllowedElement(final ResourceReference element) {
        for (ResourceReference container : containerList) {
            if (isAllowedElement(element, container)) {
                return true;
            }
        }
        return false;
    }

    // check by resource type (for further element creation)

    /**
     * returns 'true' if an element of the type can be inserted into the specified container
     */
    public boolean isAllowedType(String type, final ResourceReference container) {
        final AllowedTypes allowedEl = getAllowedTypes(allowedElements, allowedChildrenPropertyName, container);
        if (allowedEl.matches(type)) {
            final AllowedTypes allowedCont = getAllowedTypes(allowedContainers, allowedParentsPropertyName, type);
            if (allowedCont.matches(container.getType())) {
                return true;
            }
        }
        return false;
    }

    /**
     * returns 'true' if an element of the type can be inserted into one of the containers
     */
    public boolean isAllowedType(String type) {
        for (ResourceReference container : containerList) {
            if (isAllowedType(type, container)) {
                return true;
            }
        }
        return false;
    }

    // AllowedTypes cache

    /**
     * gets the allowed types property from the map using the reference path as key (can be a property of the instance)
     *
     * @param map          the cache to use for property retrieval
     * @param propertyName the property name
     * @param reference    the resource whose property should be retrieved
     */
    protected AllowedTypes getAllowedTypes(Map<String, AllowedTypes> map,
                                           String propertyName, final ResourceReference reference) {
        String path = reference.getPath();
        AllowedTypes allowedTypes = map.get(path);
        if (allowedTypes == null) {
            allowedTypes = new AllowedTypes(reference, propertyName);
            map.put(path, allowedTypes);
        }
        return allowedTypes;
    }

    /**
     * gets the allowed types property from the map using the type as key (type properties used only)
     *
     * @param map          the cache to use for property retrieval
     * @param propertyName the property name
     * @param resourceType the resource type whose property should be retrieved
     */
    protected AllowedTypes getAllowedTypes(Map<String, AllowedTypes> map,
                                           String propertyName, String resourceType) {
        AllowedTypes allowedTypes = map.get(resourceType);
        if (allowedTypes == null) {
            allowedTypes = new AllowedTypes(resolver, resourceType, propertyName);
            map.put(resourceType, allowedTypes);
        }
        return allowedTypes;
    }
}
