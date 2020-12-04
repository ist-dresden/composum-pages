package com.composum.pages.components.model.navigation;

import com.composum.pages.commons.model.Page;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.util.XSS;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class Breadcrumbs extends NavbarItem {

    private static final Logger LOG = LoggerFactory.getLogger(Breadcrumbs.class);

    protected static final Gson GSON = new GsonBuilder().create();

    public class BreadcrumbsMenu extends NavbarMenu {

        private transient Menuitem current;
        private transient List<Menuitem> breadcrumbItems;
        private transient String jsonLdScript;

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

        @Nonnull
        public String getJsonLdScript() {
            if (jsonLdScript == null) {
                int index = 1;
                JsonArray itemList = new JsonArray();
                for (Menuitem item : getBreadcrumbItems()) {
                    JsonObject json = new JsonObject();
                    json.addProperty("@type", "ListItem");
                    json.addProperty("position", index);
                    json.addProperty("name", item.getTitle());
                    json.addProperty("item", XSS.getValidHref(item.getUrl()));
                    itemList.add(json);
                    index++;
                }
                JsonObject jsonLd = new JsonObject();
                jsonLd.addProperty("@context", "https://schema.org");
                jsonLd.addProperty("@type", "BreadcrumbList");
                jsonLd.add("itemListElement", itemList);
                jsonLdScript = "<script type=\"application/ld+json\">" + GSON.toJson(jsonLd) + "</script>";
            }
            return jsonLdScript;
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

    @Nonnull
    public String getJsonLdScript() {
        return ((BreadcrumbsMenu) getMenu()).getJsonLdScript();
    }

    @Override
    protected Menu buildMenu() {
        return new BreadcrumbsMenu();
    }
}
