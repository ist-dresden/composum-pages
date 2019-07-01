package com.composum.pages.commons.model.properties;

import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

public class Language extends PropertyNode {

    public static final String PROP_KEY = "key";
    public static final String PROP_LABEL = "label";
    public static final String PROP_DIRECTION = "direction";

    protected String name;
    protected String key;
    protected String label;
    protected String direction;

    public Language(BeanContext context, Resource resource) {
        super(context, resource);
    }

    protected void initialize() {
        name = resource.getName();
        key = values.get(PROP_KEY, "??");
        label = values.get(PROP_LABEL, key);
        direction = values.get(PROP_DIRECTION, "");
    }

    public boolean isCurrent() {
        Language current = model.getLanguage();
        return key.equals(current.key);
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
