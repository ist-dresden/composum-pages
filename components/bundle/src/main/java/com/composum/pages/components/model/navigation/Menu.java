package com.composum.pages.components.model.navigation;

import com.composum.pages.commons.model.Element;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nullable;
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

    @Nullable
    protected Resource determineResource(@Nullable Resource resource) {
        // ensure that the page is used to determine the navigation items
        return resource != null ? getPageManager().getContainingPageResource(resource) : null;
    }

    /**
     * for JSP ${menu.isEmpty} to avoid use of 'empty' keyword
     */
    public boolean getIsEmpty() {
        return isEmpty();
    }

    public boolean isEmpty() {
        return getSize() == 0;
    }

    public boolean isParentUsed() {
        return false;
    }

    public int getSize() {
        return getMenuItems().size();
    }

    public List<Menuitem> getMenuItems() {
        if (menuItems == null) {
            menuItems = buildMenuItems(resource);
        }
        return menuItems;
    }

    public List<Menuitem> buildMenuItems(Resource resource) {
        List<Menuitem> menuItems = new ArrayList<>();
        ResourceFilter filter = getFilter();
        for (Resource child : resource.getChildren()) {
            if (filter.accept(child)) {
                Menuitem item = new Menuitem(context, child);
                if (StringUtils.isNotBlank(item.getTitle())) {
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
