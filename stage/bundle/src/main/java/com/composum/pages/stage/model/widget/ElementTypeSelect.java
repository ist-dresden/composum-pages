package com.composum.pages.stage.model.widget;

import com.composum.pages.commons.widget.RadioGroup;
import com.composum.pages.commons.widget.WidgetModel;
import com.composum.pages.stage.model.edit.page.Components;

import javax.annotation.Nonnull;

import static com.composum.pages.commons.servlet.EditServlet.PAGE_COMPONENT_TYPES;

/**
 * the ElementTypeSelect model...
 */
public class ElementTypeSelect extends RadioGroup implements WidgetModel {

    public static final String ATTR_CONTAINER = "container";

    private transient String containerRef;
    private transient Components components;

    public Components getComponents() {
        if (components == null) {
            components = new Components();
            components.initialize(context, getCurrentPage().getResource());
            context.getRequest().setAttribute(PAGE_COMPONENT_TYPES, getOptionValues());
        }
        return components;
    }

    public String getContainerRef() {
        return containerRef;
    }

    @Override
    public String filterWidgetAttribute(@Nonnull String attributeKey, Object attributeValue) {
        if (ATTR_CONTAINER.equals(attributeKey)) {
            containerRef = (String) attributeValue;
            return null;
        }
        return attributeKey;
    }
}
