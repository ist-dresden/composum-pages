package com.composum.pages.stage.model.edit;

import com.composum.pages.commons.model.Container;
import com.composum.pages.commons.model.Element;
import com.composum.pages.commons.model.Model;
import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.util.ResourceTypeUtil;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.Collections;
import java.util.List;

public class FrameContainer extends FrameModel {

    /**
     * retrieves the element resource focused by the frame element for editing;
     * retrieves the next useful container here to render a container view of a selected element
     */
    @Override
    protected Resource determineDelegateResource(BeanContext context, Resource resource) {
        Resource containerResource = super.determineDelegateResource(context, resource);
        ResourceResolver resolver = context.getResolver();
        while (containerResource != null && !Page.isPageContent(containerResource) &&
                !Container.isContainer(resolver, containerResource, containerResource.getResourceType())) {
            containerResource = containerResource.getParent();
        }
        if (containerResource != null) {
            if (!containerResource.getPath().equals(resource.getPath()) &&
                    !ResourceTypeUtil.isSyntheticResource(containerResource)) {
                // if the container is searched and not the same as the requested resource
                // use its resource type as the type for the containers view and not (!)
                // the type of the requested resource or an explicit requested (overridden) type
                resourceType = containerResource.getResourceType();
            }
            return containerResource;
        }
        return resource;
    }

    public List<Element> getElements() {
        Model model = getDelegate();
        return model instanceof Container ? ((Container) model).getElements() : Collections.<Element>emptyList();
    }
}
