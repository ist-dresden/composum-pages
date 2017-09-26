package com.composum.pages.components.model.listtype;

import com.composum.pages.commons.model.Container;


public class ListType extends Container {

    private transient String listType;

    public String getType() {
        if (listType == null) {
            listType = getProperty("listType", "ul");
        }
        return listType;
    }

}
