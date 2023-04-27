package com.composum.pages.commons.service;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * A service that collects PagesPlugins so that they could hook into Pages.
 */
public interface PagesPluginService {

    /**
     * Returns a list of resourcetypes which provide extensions for widget labels, collected from the plugins.
     * For each widget, the resource is to be rendered with these resource types when the widget label is added,
     * so that we can add icons / buttons to the label.
     */
    @Nonnull
    List<String> getWidgetLabelExtensions();

}
