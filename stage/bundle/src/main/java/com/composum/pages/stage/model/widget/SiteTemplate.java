package com.composum.pages.stage.model.widget;

import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.taglib.PropertyEditHandle;
import com.composum.pages.commons.widget.WidgetModel;

import java.util.Collection;

public class SiteTemplate extends PropertyEditHandle<String> implements WidgetModel {

    private transient Collection<Site> templates;

    public SiteTemplate() {
        super(String.class);
    }

    @Override
    public String getWidgetAttributeKey(String attributeKey) {
        return attributeKey;
    }

    public Collection<Site> getTemplates() {
        if (templates == null) {
            templates = getSiteManager().getSiteTemplates(context, "");
        }
        return templates;
    }
}
