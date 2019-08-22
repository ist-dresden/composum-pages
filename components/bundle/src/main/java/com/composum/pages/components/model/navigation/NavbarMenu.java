package com.composum.pages.components.model.navigation;

import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

import java.util.List;

public class NavbarMenu extends Menu {

    protected transient List<Menuitem> menuItems;

    private transient Resource menuParent;

    public NavbarMenu() {
    }

    public NavbarMenu(BeanContext context, Resource resource) {
        super(context, resource);
    }

    protected Resource determineResource(Resource resource) {
        // ensure that the page is used to determine the navigation items
        return getPageManager().getContainingPageResource(resource);
    }

    public boolean isParentUsed() {
        return !resource.equals(menuParent);
    }

    public boolean isEmpty() {
        return getSize() == 0;
    }

    public int getSize() {
        return getMenuItems().size();
    }

    public List<Menuitem> getMenuItems() {
        if (menuItems == null) {
            menuParent = resource;
            do {
                menuItems = buildMenuItems(menuParent);
            } while (menuItems.size() < 1 && (menuParent = menuParent.getParent()) != null);
        }
        return menuItems;
    }

    protected Resource getMenuParent() {
        getMenuItems();
        return menuParent;
    }
}
