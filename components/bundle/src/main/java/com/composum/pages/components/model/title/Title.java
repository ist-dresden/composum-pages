package com.composum.pages.components.model.title;

import com.composum.pages.components.model.ImageRelatedElement;
import org.apache.commons.lang3.StringUtils;

public class Title extends ImageRelatedElement {

    private transient String subtitle;
    private transient String style;

    public boolean isValid() {
        return StringUtils.isNotBlank(getTitle());
    }

    public String getSubtitle() {
        if (subtitle == null) {
            subtitle = getProperty("subtitle", "");
        }
        return subtitle;
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
