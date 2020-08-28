package com.composum.pages.commons.widget;

import com.composum.pages.commons.taglib.PropertyEditHandle;

import javax.annotation.Nonnull;

public class ImageField extends PropertyEditHandle<String> implements WidgetModel {

    public static final String FILTER_ATTR = "filter";
    public static final String DATA_FILTER_ATTR = "data-" + FILTER_ATTR;

    public ImageField() {
        super(String.class);
    }

    @Nonnull
    public String getPath() {
        return getValue();
    }

    @Override
    public String filterWidgetAttribute(@Nonnull String attributeKey, Object attributeValue) {
        if (FILTER_ATTR.equals(attributeKey) || DATA_FILTER_ATTR.equals(attributeKey)) {
            return DATA_FILTER_ATTR;
        }
        return attributeKey;
    }
}
