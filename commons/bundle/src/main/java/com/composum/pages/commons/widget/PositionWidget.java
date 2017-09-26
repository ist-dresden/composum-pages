package com.composum.pages.commons.widget;

import com.composum.pages.commons.model.properties.Position;
import com.composum.pages.commons.taglib.PropertyEditHandle;

public class PositionWidget extends PropertyEditHandle<Position.Value> {

    public PositionWidget() {
        super(Position.Value.class);
    }

    public String getX() {
        Position.Value value = getValue();
        return value != null ? value.one : "";
    }

    public String getY() {
        Position.Value value = getValue();
        return value != null ? value.two : "";
    }

    public String getDefaultX() {
        Position.Value value = getDefaultValue();
        return value != null ? value.one : "";
    }

    public String getDefaultY() {
        Position.Value value = getDefaultValue();
        return value != null ? value.two : "";
    }

    @Override
    protected Position retrieveValue(String path) {
        return new Position(getResource(), path);
    }

    public String getString() {
        Position.Value value = getValue();
        return value != null ? value.toString() : "";
    }
}
