package com.composum.pages.commons.widget;

import com.composum.pages.commons.taglib.PropertyEditHandle;

public class Checkbox extends PropertyEditHandle<Boolean> {

    public Checkbox() {
        super(Boolean.class);
    }

    public boolean isChecked() {
        Boolean value = getValue();
        return value != null ? value : false;
    }

    public String getCheckedValue() {
        return isChecked() ? "checked" : "";
    }
}
