/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.filter;

import com.composum.pages.commons.AssetsConfiguration;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * a filter implementation which is using resource filter keys of the set of filters
 * configured in the module configuration to support the determination of the right
 * DnD target drop zones for a dragged resource (asset or component)
 */
public class DropZoneFilter implements ResourceFilter {

    enum Type {page, component, asset}

    protected Type type;
    protected Collection<String> keys;

    protected final BeanContext context;
    private transient Map<String, ResourceFilter> filterMap;

    public DropZoneFilter(BeanContext context, String rule) {
        this.context = context;
        type = Type.valueOf(StringUtils.substringBefore(rule, ":").toLowerCase());
        keys = Arrays.asList(StringUtils.split(StringUtils.substringAfter(rule, ":"), ","));
    }

    // ResourceFilter

    @Override
    public boolean accept(Resource resource) {
        for (ResourceFilter filter : getFilterMap().values()) {
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
        builder.append("DropZone").append("(").append(getRule()).append(")");
    }

    // edit model

    public Type getType() {
        return type;
    }

    public Collection<String> getKeys() {
        return keys;
    }

    public boolean matches(String key) {
        return getKeys().contains(key);
    }

    public String getRule() {
        return new StringBuilder().append(getType()).append(":")
                .append(StringUtils.join(getKeys(), ",")).toString();
    }

    // filter configuration

    protected Map<String, ResourceFilter> getFilterMap() {
        if (filterMap == null) {
            filterMap = buildFilterMap();
        }
        return filterMap;
    }

    protected Map<String, ResourceFilter> buildFilterMap() {
        HashMap<String, ResourceFilter> result = new HashMap<>();
        switch (getType()) {
            case page:
                // TODO...
                break;
            case component:
                // TODO...
                break;
            case asset:
            default:
                AssetsConfiguration config = context.getService(AssetsConfiguration.class);
                for (String key : getKeys()) {
                    ResourceFilter filter = config.getFileFilter(key);
                    if (filter != null) {
                        result.put(key, filter);
                    }
                }
                break;
        }
        return result;
    }
}
