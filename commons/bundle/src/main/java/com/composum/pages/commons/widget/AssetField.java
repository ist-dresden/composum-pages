package com.composum.pages.commons.widget;

import javax.annotation.Nonnull;

public class AssetField extends PathField {

    public static final String FILTER_ATTR = "filter";
    public static final String DATA_FILTER_ATTR = "data-" + FILTER_ATTR;

    @Override
    public String filterWidgetAttribute(@Nonnull String attributeKey, Object attributeValue) {
        switch (attributeKey) {
            case FILTER_ATTR:
            case DATA_FILTER_ATTR:
                return DATA_FILTER_ATTR;
        }
        return super.filterWidgetAttribute(attributeKey, attributeValue);
    }
}
