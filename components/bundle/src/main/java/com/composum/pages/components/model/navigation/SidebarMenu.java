package com.composum.pages.components.model.navigation;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.service.PageManager;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SidebarMenu extends Menu {

    @Nullable
    public static Page findNavigationRoot(@Nonnull final BeanContext context, @Nonnull final Resource resource) {
        PageManager pageManager = context.getService(PageManager.class);
        Page containingPage = pageManager.getContainingPage(context, resource);
        Page page = containingPage;
        while (page != null && !page.isHome() &&
                !page.getContent().getProperty(PagesConstants.PROP_IS_NAV_ROOT, false)) {
            page = page.getParentPage();
        }
        return page != null ? page : containingPage;
    }

    public SidebarMenu() {
    }

    public SidebarMenu(BeanContext context, Resource resource) {
        super(context, resource);
    }

    @Override
    @Nullable
    protected Resource determineResource(@Nullable Resource resource) {
        if (resource != null) {
            Page navRoot = findNavigationRoot(context, resource);
            return navRoot != null ? navRoot.getResource() : resource;
        }
        return null;
    }
}
