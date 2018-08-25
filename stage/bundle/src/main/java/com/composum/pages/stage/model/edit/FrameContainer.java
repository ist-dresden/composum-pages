package com.composum.pages.stage.model.edit;

import com.composum.pages.commons.model.Container;
import com.composum.pages.commons.model.Element;
import com.composum.pages.commons.model.Model;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.Collections;
import java.util.List;

public class FrameContainer extends FrameModel {

    /**
     * retrieves the element resource focused by the frame element for editing
     */
    @Override
    protected Resource determineDelegateResource(BeanContext context, Resource resource) {
        Resource containerResource = super.determineDelegateResource(context, resource);
        ResourceResolver resolver = context.getResolver();
        while (containerResource != null &&
                Element.isElement(resolver, containerResource, containerResource.getResourceType())) {
            containerResource = containerResource.getParent();
        }
        return containerResource != null ? containerResource : resource;
    }

    public List<Element> getElements() {
        Model model = getDelegate();
        return model instanceof Container ? ((Container) model).getElements() : Collections.<Element>emptyList();
    }
}
