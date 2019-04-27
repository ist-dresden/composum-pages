package com.composum.pages.stage.tools;

import com.composum.pages.commons.request.DisplayMode;
import com.composum.pages.stage.model.edit.FrameModel;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.ResourceResolver;

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
            ResourceResolver resolver = context.getResolver();
            ToolsCollection statusCollection = new ToolsCollection(resolver,
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
            ResourceResolver resolver = context.getResolver();
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
