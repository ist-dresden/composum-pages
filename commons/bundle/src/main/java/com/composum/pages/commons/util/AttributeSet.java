package com.composum.pages.commons.util;


import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AttributeSet implements Iterable<Map.Entry<String, Object>> {

    protected Map<String, Object> attributes = new HashMap<>();

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            Object value = entry.getValue();
            if (value != null) {
                if (builder.length() > 0) {
                    builder.append(" ");
                }
                builder.append(entry.getKey()).append("=\"");
                builder.append(value instanceof Collection
                        ? StringUtils.join((Collection) value, ",")
                        : value.toString()).append("\"");
            }
        }
        return builder.toString();
    }

    public <T> T consumeAttribute(String key, Class<T> type) {
        T value = getAttribute(key, type);
        attributes.remove(key);
        return value;
    }

    public <T> T consumeAttribute(String key, T defaultValue) {
        T value = getAttribute(key, defaultValue);
        attributes.remove(key);
        return value;
    }

    public <T> T getAttribute(String key, Class<T> type) {
        return (T) attributes.get(key);
    }

    public <T> T getAttribute(String key, T defaultValue) {
        T value = (T) getAttribute(key, defaultValue.getClass());
        return value != null ? value : defaultValue;
    }

    public void setAttribute(String key, Object value) {
        if (value != null) {
            attributes.put(key, value);
        } else {
            attributes.remove(key);
        }
    }

    public void setOption(String optionsKey, String key, Object value) {
        List<String> options = getAttribute(optionsKey, List.class);
        if (options == null) {
            options = new ArrayList<>();
        }
        boolean ruleValue = value == null
                ? Boolean.TRUE : (value instanceof Boolean
                ? (Boolean) value : Boolean.valueOf(value.toString()));
        if (ruleValue) {
            if (!options.contains(key)) {
                options.add(key);
            }
        } else {
            options.remove(key);
        }
        setAttribute(optionsKey, options.size() > 0 ? options : null);
    }

    @Override
    public Iterator<Map.Entry<String, Object>> iterator() {
        return attributes.entrySet().iterator();
    }
}
