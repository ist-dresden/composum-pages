package com.composum.pages.stage.tools;

import com.composum.pages.commons.request.DisplayMode;
import com.composum.pages.stage.model.edit.FramePage;

import java.util.List;

public class NavigationTools extends FramePage {

    public static final String NAVIGATION_CATEGORY = "navigation";

    private transient ToolsCollection tools;

    public ToolsCollection getTools() {
        if (tools == null) {
            tools = new ToolsCollection(context, null, NAVIGATION_CATEGORY,
                    DisplayMode.requested(context).name().toLowerCase());
        }
        return tools;
    }

    public List<ToolsCollection.Component> getComponentList() {
        return getTools().getComponentList();
    }
}
