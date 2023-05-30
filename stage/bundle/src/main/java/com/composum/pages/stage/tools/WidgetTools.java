package com.composum.pages.stage.tools;

import java.util.List;

import org.apache.sling.api.resource.Resource;

import com.composum.pages.commons.model.AbstractModel;
import com.composum.pages.commons.request.DisplayMode;
import com.composum.sling.core.BeanContext;

/**
 * Integration point for extensions that add e.g. buttons to a widget.
 */
public class WidgetTools extends AbstractModel {

    public static final String WIDGET_CATEGORY = "widget";

    private transient ToolsCollection tools;

    @Override
    public void initialize(BeanContext context, final Resource resource) {
        super.initialize(context, resource);
    }

    protected ToolsCollection getLabelTools() {
        if (tools == null) {
            tools = new ToolsCollection(context, null, WIDGET_CATEGORY, "label",
                    DisplayMode.requested(context).name().toLowerCase());
        }
        return tools;
    }

    public List<ToolsCollection.Component> getLabelExtensionComponentList() {
        return getLabelTools().getComponentList();
    }

}
