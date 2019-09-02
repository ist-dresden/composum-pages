package com.composum.pages.components.model.navigation;

import com.composum.pages.commons.model.Page;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

import java.util.List;

public class Siblings extends Menu {

    public static final Page NONE = new Page();

    protected Page currentPage;

    private transient Page previousPage;
    private transient Page nextPage;

    public Siblings() {
    }

    public Siblings(BeanContext context, Resource resource) {
        super(context, resource);
    }

    protected Resource determineResource(Resource resource) {
        currentPage = getPageManager().getContainingPage(context, resource);
        Resource parentRes = currentPage != null ? currentPage.getResource().getParent() : null;
        return parentRes != null ? parentRes : resource;
    }

    public boolean isHasPrevious() {
        return getPreviousPage() != null;
    }

    public Page getPreviousPage() {
        if (previousPage == null) {
            previousPage = NONE;
            Page last = NONE;
            List<Menuitem> menuItems = getMenuItems();
            for (Menuitem menuitem : getMenuItems()) {
                if (menuitem.getPath().equals(currentPage.getPath())) {
                    previousPage = last;
                    break;
                }
                last = menuitem;
            }
        }
        return previousPage != NONE ? previousPage : null;
    }

    public boolean isHasNext() {
        return getNextPage() != null;
    }

    public Page getNextPage() {
        if (nextPage == null) {
            nextPage = NONE;
            Page current = null;
            for (Menuitem menuitem : getMenuItems()) {
                if (menuitem.getPath().equals(currentPage.getPath())) {
                    current = menuitem;
                    continue;
                }
                if (current != null) {
                    nextPage = menuitem;
                    break;
                }
            }
        }
        return nextPage != NONE ? nextPage : null;
    }
}
