package com.composum.pages.stage.tools;

import com.composum.pages.commons.request.DisplayMode;
import com.composum.pages.stage.model.edit.FrameElement;

import java.util.List;

public class ContextTools extends FrameElement {

    private transient ToolsCollection tools;

    public String getComponentTypeName() {
        return getComponentType().name();
    }

    public ToolsCollection getTools() {
        if (tools == null) {
            tools = new ToolsCollection(resolver,
                    "context",
                    DisplayMode.requested(context).name().toLowerCase(),
                    getComponentTypeName());
        }
        return tools;
    }

    public List<ToolsCollection.Component> getComponentList() {
        return getTools().getComponentList();
    }
}
