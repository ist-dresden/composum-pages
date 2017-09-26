package com.composum.pages.commons.widget;

import com.composum.pages.commons.taglib.PropertyEditHandle;

public class RichText extends PropertyEditHandle<String> {

    public RichText() {
        super(String.class);
    }

    public String getText() {
        return getValue();
    }

    public String getHeight() {
        return widget.consumeDynamicAttribute("height", "200px");
    }
}
