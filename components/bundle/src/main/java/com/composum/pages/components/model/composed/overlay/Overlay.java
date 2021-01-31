package com.composum.pages.components.model.composed.overlay;

import com.composum.pages.commons.model.Container;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.SyntheticResource;

public class Overlay extends Container {

    public static final String NN_FOREGROUND = "foreground";
    public static final String RT_FOREGROUND = "composum/pages/components/composed/overlay/foreground";

    private transient Foreground foreground;

    public Foreground getForeground() {
        if (foreground == null) {
            Resource child = resource.getChild(NN_FOREGROUND);
            foreground = new Foreground(context, child != null ? child
                    : new SyntheticResource(context.getResolver(), getPath() + "/" + NN_FOREGROUND, RT_FOREGROUND));
        }
        return foreground;
    }

    public boolean isHasForeground() {
        return getForeground().isNotEmpty();
    }

    public boolean isHideContent() {
        return getForeground().isHideContent();
    }
}
