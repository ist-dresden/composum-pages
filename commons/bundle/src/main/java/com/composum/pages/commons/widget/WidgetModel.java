package com.composum.pages.commons.widget;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * a WidgetModel is supporting the rendering of a widget performed by the widgets component and
 * attributed by an edit widget tag instance embedded in an edit dialog
 */
public interface WidgetModel {

    String DATA_PREFIX = "data-";

    /**
     * the extension hook for the model implementation to consume dynamic tag attributes
     * @return the attribute key for the widget tag (or the tag template) to use the attribute value;
     * return 'null' is the tag should not use the attribute (used by the model only)
     */
    @Nullable
    String filterWidgetAttribute(@Nonnull String attributeKey, @Nullable Object attributeValue);
}
