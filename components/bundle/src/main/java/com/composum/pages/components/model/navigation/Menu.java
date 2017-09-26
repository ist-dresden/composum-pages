package com.composum.pages.components.model.navigation;

import com.composum.pages.commons.model.Element;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import org.apache.sling.api.resource.Resource;

import java.util.ArrayList;
import java.util.List;

public class Menu extends Element {

    protected transient List<Menuitem> menuItems;

    private transient ResourceFilter filter;

    public Menu() {
    }

    public Menu(BeanContext context, Resource resource) {
        super(context, resource);
    }

    protected Resource determineResource(Resource resource) {
        // ensure that the page is used to determine the navigation items
        return getPageManager().getContainingPageResource(resource);
    }

    public boolean isEmpty() {
        return getMenuItems().size() == 0;
    }

    public List<Menuitem> getMenuItems() {
        if (menuItems == null) {
            menuItems = new ArrayList<>();
            ResourceFilter filter = getFilter();
            for (Resource child : resource.getChildren()) {
                if (filter.accept(child)) {
                    Menuitem item = new Menuitem(context, child);
                    menuItems.add(item);
                }
            }
        }
        return menuItems;
    }

    protected ResourceFilter getFilter() {
        if (filter == null) {
            filter = new NavigationPageFilter(context);
        }
        return filter;
    }
}
