package com.composum.pages.commons.widget;

import com.composum.pages.commons.taglib.PropertyEditHandle;

import javax.annotation.Nonnull;

public class PathField extends PropertyEditHandle<String> implements WidgetModel {

    public static final String ROOT_ATTR = "root";
    public static final String DATA_ROOT_ATTR = "data-" + ROOT_ATTR;

    public static final String MAXLENGTH_ATTR = "maxlength";

    protected String maxlength;

    public PathField() {
        super(String.class);
    }

    public String getPath() {
        return getValue();
    }

    public String getMaxlength() {
        return maxlength != null ? maxlength : "";
    }

    @Override
    public String filterWidgetAttribute(@Nonnull String attributeKey, Object attributeValue) {
        switch (attributeKey) {
            case ROOT_ATTR:
            case DATA_ROOT_ATTR:
                return DATA_ROOT_ATTR;
            case MAXLENGTH_ATTR:
                if (attributeValue != null) {
                    maxlength = attributeValue.toString();
                }
                return null;
        }
        return attributeKey;
    }
}
