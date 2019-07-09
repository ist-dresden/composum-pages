package com.composum.pages.commons.widget;

import com.composum.pages.commons.taglib.PropertyEditHandle;

import javax.annotation.Nonnull;

public class ImageField extends PropertyEditHandle<String> {

    public ImageField() {
        super(String.class);
    }

    @Nonnull
    public String getPath() {
        return getValue();
    }
}
