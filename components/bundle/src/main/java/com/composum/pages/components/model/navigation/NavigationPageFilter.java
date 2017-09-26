package com.composum.pages.components.model.navigation;

import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.service.PageManager;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import org.apache.sling.api.resource.Resource;

import static com.composum.pages.commons.PagesConstants.PROP_HIDE_IN_NAV;

public class NavigationPageFilter implements ResourceFilter {

    protected final BeanContext context;

    public NavigationPageFilter(BeanContext context) {
        this.context = context;
    }

    @Override
    public boolean accept(Resource resource) {
        if (Page.isPage(resource)) {
            Page page = context.getService(PageManager.class).createBean(context, resource);
            if (!page.isValid()) {
                return false;
            }
            if (!page.getProperty(PROP_HIDE_IN_NAV, false)) {
                return true;
            }
        }
        return false;
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
