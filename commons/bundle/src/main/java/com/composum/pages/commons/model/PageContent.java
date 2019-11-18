package com.composum.pages.commons.model;

import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by rw on 13.01.17.
 */
public class PageContent extends ContentModel<Page> {

    private static final Logger LOG = LoggerFactory.getLogger(PageContent.class);

    private transient Map<String, Model> elements;
    private transient ResourceFilter elementFilter;

    public PageContent() {
    }

    public PageContent(BeanContext context, Resource resource) {
        initialize(context, resource);
    }

    public boolean isThumbnailAvailable() {
        return getResource().getChild("thumbnail/image") != null;
    }

    // content elements

    /**
     * the list of elements for rendering - provided for the templates
     */
    public Map<String, Model> getElements() {
        if (elements == null) {
            Component component = getComponent();
            if (component != null) {
                elements = component.getComponentElements().instanceElements(this);
            }
            if (elements == null) {
                elements = retrieveElements();
            }
        }
        return elements;
    }

    protected Iterator<Resource> retrieveElementResources() {
        return resource.listChildren();
    }

    protected Map<String, Model> retrieveElements() {
        Map<String, Model> elements = new TreeMap<>();
        ResourceFilter filter = getRenderFilter();
        Iterator<Resource> elementIterator = retrieveElementResources();
        while (elementIterator.hasNext()) {
            Resource resource = elementIterator.next();
            if (filter.accept(resource)) {
                GenericModel element = new GenericModel(context, resource);
                elements.put(element.getName(), element);
            }
        }
        return elements;
    }

    /**
     * the filter to restrict the rendering of the embedded elements (defaults to an ElementFilter instance)
     */
    protected ResourceFilter getRenderFilter() {
        if (elementFilter == null) {
            elementFilter = new ResourceFilter() {

                @Override
                public boolean accept(@Nullable Resource resource) {
                    ResourceResolver resolver = context.getResolver();
                    return Container.isContainer(resolver, resource, null)
                            || Element.isElement(resolver, resource, null);
                }

                @Override
                public boolean isRestriction() {
                    return true;
                }

                @Override
                public void toString(@Nonnull StringBuilder builder) {
                    builder.append("pageElements");
                }
            };
        }
        return elementFilter;
    }
}
