package com.composum.pages.components.model.composed;

import com.composum.pages.commons.model.Container;

import javax.annotation.Nonnull;


public class List extends Container {

    private transient String listType;

    @Nonnull
    public String getType() {
        if (listType == null) {
            listType = getProperty("listType", "ul");
        }
        return listType;
    }

}
