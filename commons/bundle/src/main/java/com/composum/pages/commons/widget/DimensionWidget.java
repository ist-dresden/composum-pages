package com.composum.pages.commons.widget;

import com.composum.pages.commons.model.properties.Dimension;
import com.composum.pages.commons.taglib.PropertyEditHandle;

public class DimensionWidget extends PropertyEditHandle<Dimension.Value> {

    public DimensionWidget() {
        super(Dimension.Value.class);
    }

    public String getWidth() {
        Dimension.Value value = getValue();
        return value != null ? value.one : "";
    }

    public String getHeight() {
        Dimension.Value value = getValue();
        return value != null ? value.two : "";
    }

    public String getDefaultWidth() {
        Dimension.Value value = getDefaultValue();
        return value != null ? value.one : "";
    }

    public String getDefaultHeight() {
        Dimension.Value value = getDefaultValue();
        return value != null ? value.two : "";
    }

    @Override
    protected Dimension retrieveValue(String path) {
        return new Dimension(getResource(), path);
    }

    public String getString() {
        Dimension.Value value = getValue();
        return value != null ? value.toString() : "";
    }
}
