package com.composum.pages.stage.model.widget;

import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.taglib.PropertyEditHandle;
import com.composum.pages.commons.widget.WidgetModel;

import java.util.Collection;

/**
 * the SiteTemplate model handles a 'template path' string property and provides the set of available values
 */
public class SiteTemplate extends PropertyEditHandle<String> implements WidgetModel {

    private transient Collection<Site> templates;

    public SiteTemplate() {
        super(String.class);
    }

    /**
     * transforms a tag attribute name (dynamic attribute) into the key expected by the widgets view
     */
    @Override
    public String filterWidgetAttribute(String attributeKey, Object attributeValue) {
        return attributeKey;
    }

    /**
     * @return the set of template sites available in the context of the tenant of the handles model resource
     */
    public Collection<Site> getTemplates() {
        if (templates == null) {
            templates = getSiteManager().getSiteTemplates(context, ""); // ToDo tenant support
        }
        return templates;
    }
}
