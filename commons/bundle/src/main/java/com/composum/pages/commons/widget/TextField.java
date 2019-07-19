package com.composum.pages.commons.widget;

import javax.annotation.Nonnull;

public class TextField extends TextWidget {

    public static final String TYPEAHEAD_ATTR = "typeahead";
    public static final String DATA_TYPEAHEAD_ATTR = "data-" + TYPEAHEAD_ATTR;

    @Override
    public String filterWidgetAttribute(@Nonnull String attributeKey, Object attributeValue) {
        if (TYPEAHEAD_ATTR.equals(attributeKey) || DATA_TYPEAHEAD_ATTR.equals(attributeKey)) {
            return DATA_TYPEAHEAD_ATTR;
        } else {
            return super.filterWidgetAttribute(attributeKey, attributeValue);
        }
    }
}
