package com.composum.pages.components.model.text;

import com.composum.pages.components.model.ImageRelatedElement;
import org.apache.commons.lang3.StringUtils;

public class Title extends ImageRelatedElement {

    private transient String subtitle;
    private transient String cssType;
    private transient String style;

    public boolean isValid() {
        return StringUtils.isNotBlank(getTitle()) || StringUtils.isNotBlank(getSubtitle());
    }

    public String getSubtitle() {
        if (subtitle == null) {
            subtitle = getProperty("subtitle", "");
        }
        return subtitle;
    }

    public String getTypeClass() {
        if (cssType == null) {
            String imageUrl = getImageUrl();
            if (StringUtils.isNotBlank(imageUrl)) {
                cssType = getCssBase() + "_bg-image";
            }
        }
        return cssType;
    }

    public String getStyle() {
        if (style == null) {
            StringBuilder builder = new StringBuilder();
            String imageUrl = getImageUrl();
            if (StringUtils.isNotBlank(imageUrl)) {
                builder.append("background-image: url(").append(imageUrl).append(");");
            }
            style = builder.toString();
        }
        return style;
    }
}
