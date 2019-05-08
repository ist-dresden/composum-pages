package com.composum.pages.commons.filter;

import com.composum.pages.commons.model.Container;
import com.composum.pages.commons.model.ElementTypeFilter;
import com.composum.pages.commons.service.ResourceManager;
import com.composum.sling.core.filter.ResourceFilter;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;

/**
 * accepts resources of allowed element types of a container instance
 */
public class ElementFilter extends ResourceFilter.AbstractResourceFilter {

    protected final ResourceManager resourceManager;
    protected final ElementTypeFilter typeFilter;

    public ElementFilter(@Nonnull final Container container) {
        resourceManager = container.getResourceManager();
        typeFilter = new ElementTypeFilter(container.getContext().getResolver(),
                resourceManager.getReferenceList(resourceManager.getReference(container)));
    }

    @Override
    public boolean accept(@Nonnull final Resource resource) {
        return !resource.getName().startsWith("_") // assuming that '_' elements are static included
                && typeFilter.isAllowedElement(resourceManager.getReference(resource, null));
    }

    @Override
    public boolean isRestriction() {
        return true;
    }

    @Override
    public void toString(StringBuilder builder) {
        builder.append("ElementFilter");
    }
}
