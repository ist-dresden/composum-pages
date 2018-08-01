package com.composum.pages.commons.model;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.properties.PathPatternSet;
import com.composum.pages.commons.service.EditService;
import com.composum.pages.commons.service.ResourceManager;
import com.composum.pages.commons.util.ResolverUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.SyntheticResource;

import java.util.ArrayList;
import java.util.List;

import static com.composum.pages.commons.PagesConstants.NODE_TYPE_CONTAINER;
import static com.composum.pages.commons.PagesConstants.PROP_ALLOWED_ELEMENTS;

/**
 * The Container is an Element to arrange some Elements within dynamically...
 */
public class Container extends Element {

    public static final String PROP_WITH_SPACING = "withSpacing";
    public static final String PROP_MIN_ELEMENTS = "minElements";
    public static final String PROP_MAX_ELEMENTS = "maxElements";
    public static final String PROP_ELEMENT_TYPE = "elementType";

    // static resource type determination

    /**
     * check the 'cpp:Container' type for a resource with an optional overloaded type
     *
     * @param resolver the resolver to use for type check (if resource is null)
     * @param resource the resource (can be 'null' if type is available)
     * @param type     the optional resource type (necessary if resource is 'null')
     */
    public static boolean isContainer(ResourceResolver resolver, Resource resource, String type) {
        return (resource != null && (resource.isResourceType(NODE_TYPE_CONTAINER) ||
                NODE_TYPE_CONTAINER.equals(ResolverUtil.getTypeProperty(
                        resource, type, PagesConstants.PROP_COMPONENT_TYPE, "")))) ||
                (StringUtils.isNotBlank(type) &&
                        NODE_TYPE_CONTAINER.equals(ResolverUtil.getTypeProperty(
                                resolver, type, PagesConstants.PROP_COMPONENT_TYPE, "")));
    }

    public Container() {
    }

    public Container(BeanContext context, String path, String resourceType) {
        super(context, path, resourceType);
    }

    public Container(BeanContext context, Resource resource) {
        super(context, resource);
    }

    // transient attributes

    private transient Boolean withSpacing;

    private transient Integer minElements;
    private transient Integer maxElements;
    private transient String elementType;

    private transient PathPatternSet allowedElements;

    private transient List<String> elementTypes;

    private transient List<Element> elementList;

    // rendering

    /**
     * the filter to restrict the rendering of the embedded elements (if useful; defaults to ALL - no restriction)
     */
    protected ResourceFilter getRenderFilter() {
        return ResourceFilter.ALL;
    }

    /**
     * the list of elements for rendering - provided for the templates
     */
    public List<Element> getElements() {
        if (elementList == null) {
            int max = getMaxElements();
            elementList = new ArrayList<>();
            ResourceFilter filter = getRenderFilter();
            for (Resource child : resource.getChildren()) {
                if (filter.accept(child) && (max < 1 || elementList.size() < max)) {
                    Element element = new Element();
                    element.initialize(context, child);
                    elementList.add(element);
                }
            }
            int min = getMinElements();
            if (min > 0 && elementList.size() < min) {
                String elementType = getElementType();
                if (StringUtils.isNotBlank(elementType)) {
                    ResourceResolver resolver = getContext().getResolver();
                    for (int i = elementList.size(); i < min; i++) {
                        Resource synthetic = createSyntheticElement(elementType, i);
                        if (synthetic != null) {
                            Element element = new Element();
                            element.initialize(getContext(), synthetic);
                            elementList.add(element);
                        }
                    }
                }
            }
        }
        return elementList;
    }

    protected Resource createSyntheticElement(String resourceType, int elementIndex) {
        String name = StringUtils.substringAfterLast(elementType, "/") + "_" + elementIndex;
        String n = name;
        for (int j = 0; getResource().getChild(name) != null; j++) {
            name = n + j;
        }
        String path = getPath() + "/" + name;
        Element element = new Element();
        return new SyntheticResource(getContext().getResolver(), path, elementType);
    }

    /**
     * returns 'true' if spacing DOM elements should be rendered between the elements of the container
     */
    public boolean isWithSpacing() {
        if (withSpacing == null) {
            withSpacing = getProperty(PROP_WITH_SPACING, Boolean.FALSE);
        }
        return withSpacing;
    }

    /**
     * returns the mininum count of elements in the container; '0' for no minimum
     */
    public int getMinElements() {
        if (minElements == null) {
            minElements = getProperty(PROP_MIN_ELEMENTS, getDefaultMinElements());
        }
        return minElements;
    }

    /**
     * extension hook for fixed element slots
     */
    protected int getDefaultMinElements() {
        return 0;
    }

    /**
     * returns the maxinum count of elements in the container; '0' for no maximum
     */
    public int getMaxElements() {
        if (maxElements == null) {
            maxElements = getProperty(PROP_MAX_ELEMENTS, getDefaultMaxElements());
        }
        return maxElements;
    }

    /**
     * extension hook for fixed element slots
     */
    protected int getDefaultMaxElements() {
        return 0;
    }

    /**
     * returns the default element type to fill list up to the minimum
     */
    public String getElementType() {
        if (elementType == null) {
            elementType = getProperty(PROP_ELEMENT_TYPE, getDefaultElementType());
        }
        return elementType;
    }

    /**
     * extension hook for derived element slots
     */
    protected String getDefaultElementType() {
        return "";
    }

    // manipulation

    /**
     * returns a list of all available resource types which are allowed as container elements
     * used to offer the available component types for insertions / creation
     */
    public List<String> getElementTypes() {
        if (elementTypes == null) {
            EditService editService = context.getService(EditService.class);
            elementTypes = editService.getAllowedElementTypes(resolver,
                    getResourceManager().getReferenceList(getResourceManager().getReference(this)), true);
        }
        return elementTypes;
    }

    // 'allowedElements' property...

    public boolean isAllowedElement(Element element) {
        return isAllowedElement(element.getType());
    }

    public boolean isAllowedElement(Resource resource) {
        return isAllowedElement(resource.getResourceType());
    }

    public boolean isAllowedElement(ResourceManager.ResourceReference element) {
        return isAllowedElement(element.getType());
    }

    public boolean isAllowedElement(String resourceType) {
        return getAllowedElements().matches(resourceType);
    }

    /**
     * returns the 'allowedElements' rule for this container (from the configuration)
     */
    public PathPatternSet getAllowedElements() {
        if (allowedElements == null) {
            allowedElements = new PathPatternSet(getResourceManager().getReference(this), PROP_ALLOWED_ELEMENTS);
        }
        return allowedElements;
    }
}
