package com.composum.pages.stage.model.widget;

import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.taglib.PropertyEditHandle;
import com.composum.pages.commons.widget.WidgetModel;

import javax.annotation.Nonnull;
import java.util.Collection;

public class SiteSelect extends PropertyEditHandle<String> implements WidgetModel {

    private transient Collection<Site> sites;

    public SiteSelect() {
        super(String.class);
    }

    @Override
    public String filterWidgetAttribute(@Nonnull String attributeKey, Object attributeValue) {
        return attributeKey;
    }

    public Collection<Site> getSites() {
        if (sites == null) {
            sites = getSiteManager().getSites(context);
        }
        return sites;
    }
}
