package com.composum.pages.components.model.navigation;

import com.composum.pages.commons.model.Page;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class Breadcrumbs extends NavbarItem {

    private static final Logger LOG = LoggerFactory.getLogger(Breadcrumbs.class);

    public class BreadcrumbsMenu extends NavbarMenu {

        private transient Menuitem current;
        private transient List<Menuitem> breadcrumbItems;

        public BreadcrumbsMenu() {
            super(Breadcrumbs.this.context, Breadcrumbs.this.resource);
        }

        @Nonnull
        public List<Menuitem> getBreadcrumbItems() {
            if (breadcrumbItems == null) {
                breadcrumbItems = new ArrayList<>();
                ResourceFilter filter = getFilter();
                for (Page parent : getCurrent().getPagesPath()) {
                    if (filter.accept(parent.getResource())) {
                        Menuitem item = new Menuitem(context, parent.getResource());
                        if (StringUtils.isNotBlank(item.getTitle())) {
                            breadcrumbItems.add(item);
                        }
                    }
                }
            }
            return breadcrumbItems;
        }

        @Nonnull
        public Menuitem getCurrent() {
            if (current == null) {
                Resource menuParent = ((BreadcrumbsMenu) getMenu()).getMenuParent();
                if (menuParent != null) {
                    Page page = getPageManager().getContainingPage(context, menuParent);
                    if (page != null) {
                        current = new Menuitem(context, page.getResource());
                    }
                }
                if (current == null) {
                    current = Breadcrumbs.this;
                }
            }
            return current;
        }
    }

    public Breadcrumbs() {
    }

    public Breadcrumbs(BeanContext context, Resource resource) {
        super(context, resource);
    }

    @Nonnull
    public Menuitem getCurrent() {
        return ((BreadcrumbsMenu) getMenu()).getCurrent();
    }

    @Override
    public boolean isSubmenu() {
        return getBreadcrumbItems().size() > 0;
    }

    public int getLevel() {
        return getBreadcrumbItems().size(); // from '0': curent == size()
    }

    @Nonnull
    public List<Menuitem> getBreadcrumbItems() {
        return ((BreadcrumbsMenu) getMenu()).getBreadcrumbItems();
    }

    @Override
    protected Menu buildMenu() {
        return new BreadcrumbsMenu();
    }
}
