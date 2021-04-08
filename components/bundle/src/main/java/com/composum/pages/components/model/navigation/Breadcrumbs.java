package com.composum.pages.components.model.navigation;

import com.composum.pages.commons.model.AbstractModel;
import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.util.LinkUtil;
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
import java.util.Collection;
import java.util.List;

import static com.composum.pages.commons.PagesConstants.PROP_HIDE_IN_NAV;
import static com.composum.pages.components.model.navigation.Menuitem.PROP_TITLE_KEYS;

public class Breadcrumbs extends AbstractModel {

    private static final Logger LOG = LoggerFactory.getLogger(Breadcrumbs.class);

    public class Filter extends Page.DefaultPageFilter {

        public Filter(BeanContext context) {
            super(context);
        }

        @Override
        public boolean accept(Resource resource) {
            Page page = isAcceptedPage(resource);
            return page != null && !page.getProperty(PROP_HIDE_IN_NAV, false);
        }
    }

    protected static final Gson GSON = new GsonBuilder().create();

    public class BreadcrumbsItem {

        @Nonnull
        protected final Page page;

        private transient String label;

        public BreadcrumbsItem(@Nonnull final Page page) {
            this.page = page;
        }

        public boolean isCurrent() {
            return this.page.equals(getCurrentPage());
        }

        @Nonnull
        public String getLabel() {
            if (label == null) {
                label = page.getProperty(getLocale(), "", PROP_TITLE_KEYS);
                if (StringUtils.isBlank(label)) {
                    label = page.getName();
                }
            }
            return label;
        }

        @Nonnull
        public String getTitle() {
            return page.getTitle();
        }

        @Nonnull
        public String getUrl() {
            return page.getUrl();
        }

        @Nonnull
        public String getPath() {
            return page.getPath();
        }
    }

    private transient ResourceFilter filter;
    private transient List<BreadcrumbsItem> breadcrumbItems;
    private transient String jsonLdScript;

    public Breadcrumbs() {
    }

    public Breadcrumbs(BeanContext context, Resource resource) {
        initialize(context, resource);
    }

    protected ResourceFilter getFilter() {
        if (filter == null) {
            filter = new Filter(context);
        }
        return filter;
    }

    public boolean isUseful() {
        return getBreadcrumbItems().size() > 1;
    }

    @Nonnull
    public List<BreadcrumbsItem> getBreadcrumbItems() {
        if (breadcrumbItems == null) {
            breadcrumbItems = new ArrayList<>();
            Page currentPage = getCurrentPage();
            if (currentPage != null) {
                ResourceFilter filter = getFilter();
                for (Page page : currentPage.getPagesPath()) {
                    if (filter.accept(page.getResource())) {
                        breadcrumbItems.add(new BreadcrumbsItem(page));
                    }
                }
                if (filter.accept(currentPage.getResource())) {
                    breadcrumbItems.add(new BreadcrumbsItem(currentPage));
                }
            }
        }
        return breadcrumbItems;
    }

    @Nonnull
    public String getJsonLdScript() {
        if (jsonLdScript == null) {
            jsonLdScript = "";
            Collection<BreadcrumbsItem> items = getBreadcrumbItems();
            if (items.size() > 0) {
                int index = 1;
                JsonArray itemList = new JsonArray();
                for (BreadcrumbsItem item : getBreadcrumbItems()) {
                    JsonObject json = new JsonObject();
                    json.addProperty("@type", "ListItem");
                    json.addProperty("position", index);
                    json.addProperty("name", item.getLabel());
                    json.addProperty("item", XSS.getValidHref(
                            LinkUtil.getAbsoluteUrl(getContext().getRequest(), item.getUrl())));
                    itemList.add(json);
                    index++;
                }
                JsonObject jsonLd = new JsonObject();
                jsonLd.addProperty("@context", "https://schema.org");
                jsonLd.addProperty("@type", "BreadcrumbList");
                jsonLd.add("itemListElement", itemList);
                jsonLdScript = "<script type=\"application/ld+json\">" + GSON.toJson(jsonLd) + "</script>";
            }
        }
        return jsonLdScript;
    }
}
