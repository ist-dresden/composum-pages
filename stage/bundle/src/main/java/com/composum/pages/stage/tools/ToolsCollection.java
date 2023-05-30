package com.composum.pages.stage.tools;

import com.composum.pages.commons.service.PageManager;
import com.composum.pages.commons.service.SiteManager;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import javax.annotation.Nonnull;
import javax.jcr.query.Query;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Finds all tools represented as {@link ToolsCollection.Component} from collections (with resource type <code>composum/pages/tools/collection</code>) that have a {@value #CATEGORIES} attribute that has all categories from our categories, as given in the constructor {@link #ToolsCollection(BeanContext, Resource, List)}.
 * The tool can also impose {@value #CONDITION} site or page that have to be met by the targetResource.
 */
public class ToolsCollection {

    public static final String CATEGORIES = "categories";
    public static final String CONDITION = "condition";

    public static final String CONTENT_QUERY_BASE = "/jcr:root";
    public static final String CONTENT_QUERY_RULE = "//*[@sling:resourceType='composum/pages/tools/collection']";
    public static final String CONTENT_QUERY_LIBS = CONTENT_QUERY_BASE + "/libs" + CONTENT_QUERY_RULE;
    public static final String CONTENT_QUERY_APPS = CONTENT_QUERY_BASE + "/apps" + CONTENT_QUERY_RULE;

    protected final BeanContext context;
    protected final Resource targetResource;
    protected final List<String> categories;

    private transient SiteManager siteManager;
    private transient PageManager pageManager;

    public class Component implements Comparable<Component> {

        private final Resource resource;
        private final ValueMap values;

        public Component(Resource resource) {
            this.resource = resource;
            this.values = resource.getValueMap();
        }

        public String getIconClass() {
            return values.get("iconClass", "");
        }

        public String getLabel() {
            String label = values.get("title", "");
            if (StringUtils.isBlank(label)) {
                label = values.get("jcr:title", "");
            }
            return StringUtils.isNotBlank(label) ? label : getName();
        }

        public String getHint() {
            String hint = values.get("hint", "");
            return StringUtils.isNotBlank(hint) ? hint : getLabel();
        }

        public String getKey() {
            String key = values.get("key", "");
            return StringUtils.isNotBlank(key) ? key : getName();
        }

        public String getName() {
            return resource.getName();
        }

        public String getPath() {
            return resource.getPath();
        }

        public int getOrder() {
            return values.get("order", 1);
        }

        @Override
        public int compareTo(@Nonnull Component other) {
            CompareToBuilder builder = new CompareToBuilder();
            builder.append(getOrder(), other.getOrder());
            builder.append(getPath(), other.getPath());
            return builder.toComparison();
        }
    }

    private transient List<Component> componentList;

    public ToolsCollection(BeanContext context, Resource targetResource, String... categories) {
        this(context, targetResource, Arrays.asList(categories));
    }

    public ToolsCollection(BeanContext context, Resource targetResource, List<String> categories) {
        this.context = context;
        this.targetResource = targetResource;
        this.categories = categories;
    }

    public List<Component> getComponentList() {
        if (componentList == null) {
            componentList = new ArrayList<>();
            findComponents(componentList, CONTENT_QUERY_APPS);
            findComponents(componentList, CONTENT_QUERY_LIBS);
            Collections.sort(componentList);
        }
        return componentList;
    }

    public void reverseComponentList() {
        if (componentList == null) {
            componentList = new ArrayList<>();
            findComponents(componentList, CONTENT_QUERY_APPS);
            findComponents(componentList, CONTENT_QUERY_LIBS);
            Collections.sort(componentList);
        }
        getComponentList();
        Collections.reverse(componentList);
    }

    protected void findComponents(List<Component> list, String query) {

        @SuppressWarnings("deprecation")
        Iterator<Resource> toolsContentResources = context.getResolver().findResources(query, Query.XPATH);

        ResourceFilter toolsFilter = new ToolsFilter();
        while (toolsContentResources.hasNext()) {

            Resource toolsContent = toolsContentResources.next();
            for (Resource component : toolsContent.getChildren()) {

                if (toolsFilter.accept(component)) {
                    list.add(new Component(component));
                }
            }
        }
    }

    public class ToolsFilter extends ResourceFilter.AbstractResourceFilter {

        @Override
        public boolean accept(Resource resource) {
            ValueMap values = resource.getValueMap();
            String[] categories = values.get(CATEGORIES, new String[0]);
            for (String category : ToolsCollection.this.categories) {
                boolean matches = false;
                for (String pattern : categories) {
                    if (category.matches(pattern)) {
                        matches = true;
                        break;
                    }
                }
                if (!matches) {
                    return false;
                }
            }
            if (targetResource != null) {
                String[] condition = values.get(CONDITION, new String[0]);
                if (condition.length > 0) {
                    for (String key : condition) {
                        switch (key) {
                            case "site":
                                if (getSiteManager().getContainingSite(context, targetResource) == null) {
                                    return false;
                                }
                                break;
                            case "page":
                                if (getPageManager().getContainingPage(context, targetResource) == null) {
                                    return false;
                                }
                                break;
                        }
                    }
                }
            }
            return true;
        }

        @Override
        public boolean isRestriction() {
            return true;
        }

        @Override
        public void toString(StringBuilder builder) {
            builder.append("tools(").append(StringUtils.join(categories, ',')).append(")");
        }
    }

    protected SiteManager getSiteManager() {
        if (siteManager == null) {
            siteManager = context.getService(SiteManager.class);
        }
        return siteManager;
    }

    protected PageManager getPageManager() {
        if (pageManager == null) {
            pageManager = context.getService(PageManager.class);
        }
        return pageManager;
    }
}
