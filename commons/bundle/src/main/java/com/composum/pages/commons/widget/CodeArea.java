package com.composum.pages.commons.widget;

import com.composum.pages.commons.taglib.PropertyEditHandle;

public class CodeArea extends PropertyEditHandle<String> implements WidgetModel {

    public static final String LANGUAGE_ATTR = "language";
    public static final String DATA_LANGUAGE_ATTR = "data-" + LANGUAGE_ATTR;

    public CodeArea() {
        super(String.class);
    }

    public String getText() {
        return getValue();
    }

    public String getHeight() {
        return widget.consumeDynamicAttribute("height", "");
    }

    @Override
    public String getWidgetAttributeKey(String attributeKey) {
        if (LANGUAGE_ATTR.equals(attributeKey) || DATA_LANGUAGE_ATTR.equals(attributeKey)) {
            return DATA_LANGUAGE_ATTR;
        }
        return attributeKey;
    }
}
