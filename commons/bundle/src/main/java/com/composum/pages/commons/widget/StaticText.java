package com.composum.pages.commons.widget;

import com.composum.pages.commons.taglib.PropertyEditHandle;
import org.apache.commons.lang3.StringUtils;

public class StaticText extends PropertyEditHandle<String> implements WidgetModel {

    public static final String ATTR_LEVEL = "level";

    private transient String level;

    public StaticText() {
        super(String.class);
    }

    public String getText() {
        return getValue();
    }

    public String getLevel() {
        return StringUtils.isNotBlank(level) ? level : "";
    }

    @Override
    public String filterWidgetAttribute(String attributeKey, Object attributeValue) {
        if (ATTR_LEVEL.equals(attributeKey)) {
            level = attributeValue != null ? attributeValue.toString() : null;
            return null;
        }
        return attributeKey;
    }
}
