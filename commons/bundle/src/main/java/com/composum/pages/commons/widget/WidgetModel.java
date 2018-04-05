package com.composum.pages.commons.widget;

/**
 * a WidgetModel is supporting the rendering of a widget performed by the widgets component and
 * attributed by an edit widget tag instance embedded in an edit dialog
 */
public interface WidgetModel {

    /**
     * transforms a tag attribute name (dynamic attribute) into the key expected by the widgets view
     */
    String getWidgetAttributeKey(String attributeKey);
}
