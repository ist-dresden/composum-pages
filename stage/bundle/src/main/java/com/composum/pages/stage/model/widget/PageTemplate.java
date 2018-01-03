package com.composum.pages.stage.model.widget;

import com.composum.pages.commons.taglib.PropertyEditHandle;
import com.composum.pages.commons.widget.WidgetModel;

public class PageTemplate extends PropertyEditHandle<String> implements WidgetModel {

    public PageTemplate() {
        super(String.class);
    }

    @Override
    public String getWidgetAttributeKey(String attributeKey) {
        return attributeKey;
    }
}
