package com.composum.pages.commons.widget;

public class TextField extends TextWidget {

    public static final String TYPEAHEAD_ATTR = "typeahead";
    public static final String DATA_TYPEAHEAD_ATTR = "data-" + TYPEAHEAD_ATTR;

    @Override
    public String filterWidgetAttribute(String attributeKey, String attributeValue) {
        if (TYPEAHEAD_ATTR.equals(attributeKey) || DATA_TYPEAHEAD_ATTR.equals(attributeKey)) {
            return DATA_TYPEAHEAD_ATTR;
        } else {
            return super.filterWidgetAttribute(attributeKey, attributeValue);
        }
    }
}
