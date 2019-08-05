package com.composum.pages.commons.widget;

import com.composum.pages.commons.taglib.PropertyEditHandle;

import javax.annotation.Nonnull;

public class VideoField extends PropertyEditHandle<String> {

    public VideoField() {
        super(String.class);
    }

    @Nonnull
    public String getPath() {
        return getValue();
    }
}
