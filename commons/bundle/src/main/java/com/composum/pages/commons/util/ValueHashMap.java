package com.composum.pages.commons.util;

import org.apache.sling.api.resource.ValueMap;

import javax.annotation.Nonnull;
import java.util.HashMap;

public class ValueHashMap extends HashMap<String, Object> implements ValueMap {

    @Override
    public <T> T get(@Nonnull String name, @Nonnull Class<T> type) {
        return (T) get(name);
    }

    @Override
    public <T> T get(@Nonnull String name, T defaultValue) {
        T result = (T) get(name);
        return result != null ? result : defaultValue;
    }
}
