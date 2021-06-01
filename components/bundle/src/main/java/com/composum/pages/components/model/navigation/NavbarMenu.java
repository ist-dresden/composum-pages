package com.composum.pages.components.model.navigation;

import com.composum.pages.commons.model.Homepage;
import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.Site;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class NavbarMenu extends Menu {

    protected transient List<Menuitem> menuItems;

    private transient Resource menuParent;

    public NavbarMenu() {
    }

    public NavbarMenu(BeanContext context, Resource resource) {
        super(context, resource);
    }

    @Nullable
    protected Resource determineResource(@Nullable Resource resource) {
        // ensure that the page is used to determine the navigation items
        return resource != null ? getPageManager().getContainingPageResource(resource) : null;
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

    public String getSizeCss() {
        return getSize() < 4 ? "default" : "condensed";
    }

    public List<Menuitem> getMenuItems() {
        if (menuItems == null) {
            menuParent = resource;
            do {
                if (Site.isSite(menuParent)) {
                    Site site = getSiteManager().getContainingSite(context, menuParent);
                    if (site != null) {
                        Homepage homepage = site.getHomepage(getLocale());
                        if (!homepage.isTheSiteItself()) {
                            menuParent = homepage.getResource();
                            menuItems = buildMenuItems(menuParent);
                            break;
                        }
                    }
                }
                if (Page.isPage(menuParent)) {
                    menuItems = buildMenuItems(menuParent);
                }
            } while ((menuItems == null || menuItems.size() < 1) && (menuParent = menuParent.getParent()) != null);
            if (menuItems == null) {
                menuItems = new ArrayList<>();
            }
        }
        return menuItems;
    }

    protected Resource getMenuParent() {
        getMenuItems();
        return menuParent;
    }
}
