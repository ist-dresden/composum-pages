package com.composum.pages.stage.model.widget;

import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.taglib.PropertyEditHandle;
import com.composum.pages.commons.widget.WidgetModel;

import java.util.Collection;

/**
 * the PageTemplate model handles a 'template path' string property and provides the set of available values
 */
public class PageTemplate extends PropertyEditHandle<String> implements WidgetModel {

    private transient Collection<Page> templates;

    public PageTemplate() {
        super(String.class);
    }

    /**
     * transforms a tag attribute name (dynamic attribute) into the key expected by the widgets view
     */
    @Override
    public String getWidgetAttributeKey(String attributeKey) {
        return attributeKey;
    }

    /**
     * @return the set of template pages available in the context of the handles model resource
     */
    public Collection<Page> getTemplates() {
        if (templates == null) {
            templates = getPageManager().getPageTemplates(context, getResource());
        }
        return templates;
    }
}
