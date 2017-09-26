package com.composum.pages.commons.widget;

import com.composum.pages.commons.taglib.PropertyEditHandle;

public class PathField extends PropertyEditHandle<String> {

    public PathField() {
        super(String.class);
    }

    public String getPath() {
        return getValue();
    }
}
