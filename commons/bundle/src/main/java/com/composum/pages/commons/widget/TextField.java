package com.composum.pages.commons.widget;

public class TextField extends TextWidget {

    public static final String TYPEAHEAD_ATTR = "typeahead";
    public static final String DATA_TYPEAHEAD_ATTR = "data-" + TYPEAHEAD_ATTR;

    @Override
    public String getWidgetAttributeKey(String attributeKey) {
        if (TYPEAHEAD_ATTR.equals(attributeKey) || DATA_TYPEAHEAD_ATTR.equals(attributeKey)) {
            return DATA_TYPEAHEAD_ATTR;
        } else {
            return super.getWidgetAttributeKey(attributeKey);
        }
    }
}
