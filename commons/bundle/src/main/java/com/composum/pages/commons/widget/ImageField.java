package com.composum.pages.commons.widget;

import com.composum.pages.commons.taglib.PropertyEditHandle;

public class ImageField extends PropertyEditHandle<String> {

    public ImageField() {
        super(String.class);
    }

    public String getPath() {
        return getValue();
    }
}
