package com.composum.pages.stage.tools;

import com.composum.pages.commons.request.DisplayMode;
import com.composum.pages.stage.model.edit.FrameModel;
import com.composum.sling.core.BeanContext;

import java.util.List;

public class ContextTools extends FrameModel {

    private transient ToolsCollection.Component status;
    private transient ToolsCollection tools;

    public String getComponentTypeName() {
        return getComponentType().name();
    }

    public ToolsCollection.Component getStatus() {
        if (status == null) {
            BeanContext context = getDelegate().getContext();
            ToolsCollection statusCollection = new ToolsCollection(context, getResource(),
                    "status",
                    DisplayMode.requested(context).name().toLowerCase(),
                    getComponentTypeName());
            if (statusCollection.getComponentList().size() > 0) {
                status = statusCollection.getComponentList().get(0);
            }
        }
        return status;
    }

    public ToolsCollection getTools() {
        if (tools == null) {
            BeanContext context = getDelegate().getContext();
            tools = new ToolsCollection(context, getResource(),
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
