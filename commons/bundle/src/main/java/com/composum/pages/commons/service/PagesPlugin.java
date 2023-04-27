package com.composum.pages.commons.service;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * Interface with which plugins can register themselves.
 */
public interface PagesPlugin {

    /**
     * For ordering the plugins - high values are loaded later as in OSGI rank.
     */
    int getRank();

    /**
     * Returns a list of resourcetypes which provide extensions for widget labels.
     * For each widget, the resource is to be rendered with these resource types when the widget label is added,
     * so that we can add icons / buttons to the label.
     */
    @Nonnull
    default List<String> getWidgetLabelExtensions() {
        return Collections.emptyList();
    }

}
