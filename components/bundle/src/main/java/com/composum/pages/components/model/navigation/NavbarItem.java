package com.composum.pages.components.model.navigation;

import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NavbarItem extends Menuitem {

    private static final Logger LOG = LoggerFactory.getLogger(NavbarItem.class);

    public NavbarItem() {
    }

    public NavbarItem(BeanContext context, Resource resource) {
        super(context, resource);
    }

    @Override
    public boolean isSubmenu() {
        return super.isSubmenu() && !isNavRoot() && !getMenu().isParentUsed();
    }

    @Override
    protected Menu buildMenu() {
        return new NavbarMenu(context, resource);
    }
}
