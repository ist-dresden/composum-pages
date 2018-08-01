package com.composum.pages.components.model.navigation;

import com.composum.pages.commons.model.Page;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

import static com.composum.pages.commons.PagesConstants.PROP_HIDE_IN_NAV;

public class NavigationPageFilter extends Page.DefaultPageFilter {

    public NavigationPageFilter(BeanContext context) {
        super(context);
    }

    @Override
    public boolean accept(Resource resource) {
        Page page = isAcceptedPage(resource);
        return page != null && !page.getProperty(PROP_HIDE_IN_NAV, false);
    }

    @Override
    public boolean isRestriction() {
        return true;
    }

    @Override
    public void toString(StringBuilder builder) {
        builder.append(getClass().getSimpleName());
    }

}
