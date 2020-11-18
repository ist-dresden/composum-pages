package com.composum.pages.stage.tools;

import com.composum.pages.commons.request.DisplayMode;
import com.composum.sling.core.BeanContext;

import java.util.List;

public class StandaloneTools extends ContextTools {

    public static final String STANDALONE_CATEGORY = "standalone";
    public static final String STATIC_CATEGORY = "static";

    private transient ToolsCollection staticTools;

    public List<ToolsCollection.Component> getStaticList() {
        return getStaticTools().getComponentList();
    }

    public ToolsCollection getStaticTools() {
        if (staticTools == null) {
            BeanContext context = getDelegate().getContext();
            staticTools = new ToolsCollection(context, getResource(),
                    STANDALONE_CATEGORY,
                    DisplayMode.requested(context).name().toLowerCase(),
                    STATIC_CATEGORY);
        }
        return staticTools;
    }

    @Override
    protected ToolsCollection createTools(BeanContext context) {
        ToolsCollection tools = new ToolsCollection(context, getResource(),
                STANDALONE_CATEGORY,
                DisplayMode.requested(context).name().toLowerCase(),
                getComponentTypeName());
        return tools;
    }
}
