package com.composum.pages.stage.model.edit.page;

import com.composum.pages.commons.model.Component;
import com.composum.pages.commons.model.Page;
import org.apache.sling.api.resource.Resource;

import static com.composum.pages.commons.PagesConstants.NT_COMPONENT;

public class HelpPage extends Page {

    private transient Component component;

    /**
     * @return the component of the help page
     */
    @Override
    public Component getComponent() {
        if (component == null) {
            Resource resource = getResource();
            while (resource != null && !resource.isResourceType(NT_COMPONENT)) {
                resource = resource.getParent();
            }
            if (resource != null) {
                component = new Component(getContext(), resource);
            }
        }
        return component;
    }
}
