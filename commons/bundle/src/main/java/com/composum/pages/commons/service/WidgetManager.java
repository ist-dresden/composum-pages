package com.composum.pages.commons.service;

import com.composum.sling.core.BeanContext;

public interface WidgetManager {

    String getWidgetTypeResourcePath(BeanContext context, String widgetType);
}
