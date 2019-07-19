package com.composum.pages.commons.widget;

import com.composum.pages.commons.taglib.PropertyEditHandle;

import javax.annotation.Nonnull;

public class TextWidget extends PropertyEditHandle<String> implements WidgetModel {

    public static final String PATTERN_ATTR = "pattern";
    public static final String DATA_PATTERN_ATTR = "data-" + PATTERN_ATTR;
    public static final String PATTERN_HINT_ATTR = PATTERN_ATTR + "-hint";
    public static final String DATA_PATTERN_HINT_ATTR = "data-" + PATTERN_HINT_ATTR;

    public TextWidget() {
        super(String.class);
    }

    public String getText() {
        return getValue();
    }

    @Override
    public String filterWidgetAttribute(@Nonnull String attributeKey, Object attributeValue) {
        if (PATTERN_ATTR.equals(attributeKey) || DATA_PATTERN_ATTR.equals(attributeKey)) {
            return DATA_PATTERN_ATTR;
        } else if (PATTERN_HINT_ATTR.equals(attributeKey) || DATA_PATTERN_HINT_ATTR.equals(attributeKey)) {
            return DATA_PATTERN_HINT_ATTR;
        } else {
            return attributeKey;
        }
    }
}
