package com.composum.pages.stage.model.widget;

import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.taglib.PropertyEditHandle;
import com.composum.pages.commons.widget.WidgetModel;

import java.util.Collection;

public class PageTemplate extends PropertyEditHandle<String> implements WidgetModel {

    private transient Collection<Page> templates;

    public PageTemplate() {
        super(String.class);
    }

    @Override
    public String getWidgetAttributeKey(String attributeKey) {
        return attributeKey;
    }

    public Collection<Page> getTemplates() {
        if (templates == null) {
            templates = getPageManager().getPageTemplates(context, "");
        }
        return templates;
    }
}
