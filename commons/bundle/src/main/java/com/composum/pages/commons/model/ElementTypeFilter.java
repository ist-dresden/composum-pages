package com.composum.pages.commons.model;

import com.composum.pages.commons.model.properties.PathPatternSet;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.HashMap;
import java.util.Map;

import static com.composum.pages.commons.PagesConstants.PROP_ALLOWED_CONTAINERS;
import static com.composum.pages.commons.PagesConstants.PROP_ALLOWED_ELEMENTS;

/**
 * a filter to determine the allowed elements for a set of containers (all containers of a page)
 */
public class ElementTypeFilter {

    /** the set of containers to use for allowed element check */
    protected final ResourceReference.List containerList;

    /** caches for the 'allowed...' properties */
    protected Map<String, PathPatternSet> allowedElements = new HashMap<>();
    protected Map<String, PathPatternSet> allowedContainers = new HashMap<>();

    protected final ResourceResolver resolver;

    public ElementTypeFilter(final ResourceResolver resolver, final ResourceReference.List containerList) {
        this.resolver = resolver;
        this.containerList = containerList;
    }

    // check by resource reference (probably an existing resource)

    /**
     * returns 'true' if the element can be inserted into the specified container
     */
    public boolean isAllowedElement(final ResourceReference element, final ResourceReference container) {
        final PathPatternSet allowedEl = getAllowedTypes(allowedElements, PROP_ALLOWED_ELEMENTS, container);
        if (allowedEl.matches(element.getType())) {
            final PathPatternSet allowedCont = getAllowedTypes(allowedContainers, PROP_ALLOWED_CONTAINERS, element);
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
        final PathPatternSet allowedEl = getAllowedTypes(allowedElements, PROP_ALLOWED_ELEMENTS, container);
        if (allowedEl.matches(type)) {
            final PathPatternSet allowedCont = getAllowedTypes(allowedContainers, PROP_ALLOWED_CONTAINERS, type);
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

    // PathPatternSet cache

    /**
     * gets the allowed types property from the map using the reference path as key (can be a property of the instance)
     *
     * @param map          the cache to use for property retrieval
     * @param propertyName the property name
     * @param reference    the resource whose property should be retrieved
     */
    protected PathPatternSet getAllowedTypes(Map<String, PathPatternSet> map,
                                             String propertyName, final ResourceReference reference) {
        String path = reference.getPath();
        PathPatternSet allowedTypes = map.get(path);
        if (allowedTypes == null) {
            allowedTypes = new PathPatternSet(reference, propertyName);
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
    protected PathPatternSet getAllowedTypes(Map<String, PathPatternSet> map,
                                             String propertyName, String resourceType) {
        PathPatternSet allowedTypes = map.get(resourceType);
        if (allowedTypes == null) {
            allowedTypes = new PathPatternSet(resolver, resourceType, propertyName);
            map.put(resourceType, allowedTypes);
        }
        return allowedTypes;
    }
}
