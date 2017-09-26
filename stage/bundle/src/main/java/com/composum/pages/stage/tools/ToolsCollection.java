package com.composum.pages.stage.tools;

import com.composum.sling.core.filter.ResourceFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import javax.jcr.query.Query;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ToolsCollection {

    public static final String CATEGORIES = "categories";

    public static final String CONTENT_QUERY_BASE = "/jcr:root";
    public static final String CONTENT_QUERY_RULE = "//*[@sling:resourceType='composum/pages/tools/collection']";
    public static final String CONTENT_QUERY_LIBS = CONTENT_QUERY_BASE + "/libs" + CONTENT_QUERY_RULE;
    public static final String CONTENT_QUERY_APPS = CONTENT_QUERY_BASE + "/apps" + CONTENT_QUERY_RULE;

    protected final ResourceResolver resolver;
    protected final List<String> categories;

    public class Component implements Comparable<Component> {

        private final Resource resource;
        private final ValueMap values;

        public Component(Resource resource) {
            this.resource = resource;
            this.values = resource.adaptTo(ValueMap.class);
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
        public int compareTo(Component other) {
            return getOrder() - other.getOrder();
        }
    }

    private transient List<Component> componentList;

    public ToolsCollection(ResourceResolver resolver, String... categories) {
        this.resolver = resolver;
        this.categories = Arrays.asList(categories);
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

    protected void findComponents(List<Component> list, String query) {

        Iterator<Resource> toolsContentResources = resolver.findResources(query, Query.XPATH);

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

    public class ToolsFilter implements ResourceFilter {

        @Override
        public boolean accept(Resource resource) {
            ValueMap values = resource.adaptTo(ValueMap.class);
            List<String> categories = Arrays.asList(values.get(CATEGORIES, new String[0]));
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
}
