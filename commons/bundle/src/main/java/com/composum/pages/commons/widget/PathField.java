package com.composum.pages.commons.widget;

import com.composum.pages.commons.taglib.PropertyEditHandle;

import javax.annotation.Nonnull;

public class PathField extends PropertyEditHandle<String> {

    public PathField() {
        super(String.class);
    }

    @Nonnull
    public String getPath() {
        return getValue();
    }
}
