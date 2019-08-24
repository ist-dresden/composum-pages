package com.composum.pages.commons.filter;

import com.composum.pages.commons.PagesConstants;
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
    public boolean accept(final Resource resource) {
        PagesConstants.ComponentType type;
        return resource != null && !resource.getName().startsWith("_") // assuming that '_' elements are static included
                && ((type = PagesConstants.ComponentType.typeOf(resource.getResourceResolver(), resource, null))
                == PagesConstants.ComponentType.element || type == PagesConstants.ComponentType.container)
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
