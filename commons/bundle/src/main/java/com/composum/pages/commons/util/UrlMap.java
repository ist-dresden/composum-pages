package com.composum.pages.commons.util;

import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class UrlMap implements Map<String, String> {

    public interface Builder {

        @Nonnull
        String buildUrl(@Nonnull String key);
    }

    protected final Builder builder;

    public UrlMap(@Nonnull final Builder builder) {
        this.builder = builder;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        return true;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public String get(Object key) {
        return builder.buildUrl((String) key);
    }

    @Nullable
    @Override
    public String put(String key, String value) {
        return null;
    }

    @Override
    public String remove(Object key) {
        return null;
    }

    @Override
    public void putAll(@Nonnull Map<? extends String, ? extends String> m) {
    }

    @Override
    public void clear() {
    }

    @Nonnull
    @Override
    public Set<String> keySet() {
        return Collections.emptySet();
    }

    @Nonnull
    @Override
    public Collection<String> values() {
        return Collections.emptyList();
    }

    @Nonnull
    @Override
    public Set<Entry<String, String>> entrySet() {
        return Collections.emptySet();
    }
}
