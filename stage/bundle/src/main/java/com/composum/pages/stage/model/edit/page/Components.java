package com.composum.pages.stage.model.edit.page;

import com.composum.pages.commons.model.Component;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.composum.pages.commons.servlet.EditServlet.PAGE_COMPONENT_TYPES;

public class Components extends PageElement {

    private static final Logger LOG = LoggerFactory.getLogger(Components.class);

    private transient List<Component> componentList;

    public List<Component> getComponentList() {
        if (componentList == null) {
            componentList = new ArrayList<>();
            List<String> allowedElements = (List<String>) context.getRequest().getAttribute(PAGE_COMPONENT_TYPES);
            if (allowedElements != null) {
                for (String path : allowedElements) {
                    Resource typeResource = resolver.getResource(path);
                    if (resource != null) {
                        componentList.add(new Component(context, typeResource));
                    }
                }
            }
        }
        return componentList;
    }
}
