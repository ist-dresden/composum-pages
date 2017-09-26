package com.composum.pages.components.model.illustration;

import com.composum.pages.commons.model.Container;
import com.composum.pages.commons.model.Image;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.filter.StringFilter;

public class Illustration extends Container {

    public enum Behavior {
        accordion, independent
    }

    public static final ResourceFilter ANNOTATION_FILTER = new ResourceFilter.ResourceTypeFilter(
            new StringFilter.WhiteList("composum/pages/components/composed/illustration/annotation"));

    private transient Image image;
    private transient String style;

    public boolean isValid() {
        return getImage().isValid();
    }

    public Image getImage() {
        if (image == null) {
            image = new Image(context, resource.getChild("image"));
        }
        return image;
    }

    protected ResourceFilter getRenderFilter() {
        return ANNOTATION_FILTER;
    }

    public Behavior getBehavior() {
        return Behavior.valueOf(getProperty("shape/behavior", Behavior.accordion.name()));
    }

    public String getStyle () {
        if (style == null) {
            StringBuilder buffer = new StringBuilder();
            Image image = getImage();
            int width = image.getWidth();
            if (width > 0) {
                buffer.append("max-width:").append(width).append("px;");
            }
            style = buffer.toString();
        }
        return style;
    }
}
