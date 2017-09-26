package com.composum.pages.commons.widget;

import com.composum.pages.commons.taglib.PropertyEditHandle;

public class TextArea extends PropertyEditHandle<String> {

    public TextArea() {
        super(String.class);
    }

    public String getText() {
        return getValue();
    }

    public String getRows() {
        return widget.consumeDynamicAttribute("rows", "4");
    }
}
