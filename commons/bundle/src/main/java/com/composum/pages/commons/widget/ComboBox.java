package com.composum.pages.commons.widget;

import javax.annotation.Nonnull;

import static com.composum.pages.commons.widget.TextField.DATA_TYPEAHEAD_ATTR;
import static com.composum.pages.commons.widget.TextField.TYPEAHEAD_ATTR;

/**
 * the widget model for the 'combobox' widget
 */
public class ComboBox extends SingleStringOption implements WidgetModel {

    public String getText() {
        return getCurrent();
    }

    @Override
    public String filterWidgetAttribute(@Nonnull String attributeKey, Object attributeValue) {
        if (TYPEAHEAD_ATTR.equals(attributeKey) || DATA_TYPEAHEAD_ATTR.equals(attributeKey)) {
            return DATA_TYPEAHEAD_ATTR;
        }
        return attributeKey;
    }
}
