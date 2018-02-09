package com.composum.pages.commons.widget;

public interface WidgetModel {

    /**
     * transforms a tag attribute name (dynamic attribute) into the key expected by the widgets view
     */
    String getWidgetAttributeKey(String attributeKey);
}
