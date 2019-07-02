package com.composum.pages.commons.model.properties;

import com.composum.sling.core.BeanContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import java.util.Locale;

public class Language extends PropertyNode {

    public static final String PROP_KEY = "key";
    public static final String PROP_LABEL = "label";
    public static final String PROP_DIRECTION = "direction";

    protected String name;
    protected String key;
    protected String label;
    protected String direction;

    private transient Locale locale;

    public Language(BeanContext context, Resource resource) {
        super(context, resource);
    }

    protected void initialize() {
        name = resource.getName();
        key = values.get(PROP_KEY, "??");
        label = values.get(PROP_LABEL, key);
        direction = values.get(PROP_DIRECTION, "");
    }

    public String toString() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public String getDirection() {
        return direction;
    }

    public Locale getLocale() {
        if (locale == null) {
            String[] key = StringUtils.split(getKey(), "_", 3);
            locale = new Locale(key[0], key.length > 1 ? key[1] : "", key.length > 2 ? key[2] : "");
        }
        return locale;
    }

    // Object

    @Override
    public boolean equals(Object object) {
        return object instanceof Language && key.equals(((Language) object).key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
