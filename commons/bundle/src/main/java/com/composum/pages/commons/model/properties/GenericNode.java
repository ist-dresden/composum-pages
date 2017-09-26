package com.composum.pages.commons.model.properties;

import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * a simple model for a subnode of a content element which declares a simple structured type
 * such a simple model has no embedded i18n functionality but the node can be used as i18n property
 */
public class GenericNode extends PropertyNode implements Map<String, Object> {

    public GenericNode() {
    }

    public GenericNode(final BeanContext context, final Resource resource) {
        super(context, resource);
    }

    // the generic implementation implements a map which wraps to the value map of the resource

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return values.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return values.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return values.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return values.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return values.remove(key);
    }

    @Override
    public void putAll(@Nonnull Map<? extends String, ?> map) {
        values.putAll(map);
    }

    @Override
    public void clear() {
        values.clear();
    }

    @Nonnull
    @Override
    public Set<String> keySet() {
        return values.keySet();
    }

    @Nonnull
    @Override
    public Collection<Object> values() {
        return values.values();
    }

    @Nonnull
    @Override
    public Set<Entry<String, Object>> entrySet() {
        return values.entrySet();
    }
}
