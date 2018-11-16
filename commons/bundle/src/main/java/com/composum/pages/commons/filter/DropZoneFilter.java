/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.filter;

import com.composum.pages.commons.AssetsConfiguration;
import com.composum.pages.commons.PagesConfiguration;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * a filter implementation which is using resource filter keys of the set of filters
 * configured in the module configuration to support the determination of the right
 * DnD target drop zones for a dragged resource (asset or component)
 */
public class DropZoneFilter implements ResourceFilter {

    private static final Logger LOG = LoggerFactory.getLogger(DropZoneFilter.class);

    public enum Type {page, component, asset}

    protected Map<Type, Collection<String>> rules;

    protected final BeanContext context;
    private transient Collection<ResourceFilter> filters;

    /**
     * construct a filter by parsing a string containg rules like 'page:site;file:document,text'
     */
    public DropZoneFilter(BeanContext context, String filterRules) {
        this.context = context;
        rules = new LinkedHashMap<>();
        for (String rule : filterRules.split(";")) {
            try {
                Type type = Type.valueOf(StringUtils.substringBefore(rule, ":").toLowerCase());
                Collection<String> keys = Arrays.asList(StringUtils.split(
                        StringUtils.substringAfter(rule, ":"), ","));
                rules.put(type, keys);
            } catch (IllegalArgumentException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }

    // ResourceFilter

    @Override
    public boolean accept(Resource resource) {
        for (ResourceFilter filter : getFilters()) {
            if (filter.accept(resource)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isRestriction() {
        return false;
    }

    @Override
    public void toString(StringBuilder builder) {
        builder.append("DropZone").append("(");
        for (Iterator<Type> it = getTypes().iterator(); it.hasNext(); ) {
            builder.append(getRule(it.next()));
            if (it.hasNext()) {
                builder.append(';');
            }
        }
        builder.append(")");
    }

    // edit model

    public Set<Type> getTypes() {
        return rules.keySet();
    }

    public Collection<String> getKeys(Type type) {
        return rules.get(type);
    }

    public String getRule(Type type) {
        return new StringBuilder().append(getTypes()).append(":")
                .append(StringUtils.join(getKeys(type), ",")).toString();
    }

    // filter configuration

    protected Collection<ResourceFilter> getFilters() {
        if (filters == null) {
            filters = buildFilterSet();
        }
        return filters;
    }

    protected Collection<ResourceFilter> buildFilterSet() {
        Collection<ResourceFilter> result = new ArrayList<>();
        for (Type type : getTypes()) {
            switch (type) {
                case page:
                    PagesConfiguration pagesConfig = context.getService(PagesConfiguration.class);
                    for (String key : getKeys(type)) {
                        ResourceFilter filter = pagesConfig.getPageFilter(context, key);
                        if (filter != null) {
                            result.add(filter);
                        }
                    }
                    break;
                case component:
                    // TODO...
                    break;
                case asset:
                default:
                    AssetsConfiguration assetsConfig = context.getService(AssetsConfiguration.class);
                    for (String key : getKeys(type)) {
                        ResourceFilter filter = assetsConfig.getFileFilter(context, key);
                        if (filter != null) {
                            result.add(filter);
                        }
                    }
                    break;
            }
        }
        return result;
    }
}
