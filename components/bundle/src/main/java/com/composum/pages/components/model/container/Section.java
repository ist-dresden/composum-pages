package com.composum.pages.components.model.container;

import com.composum.pages.commons.model.Container;

public class Section extends Container {

    private transient String anchor;

    public String getAnchor() {
        if (anchor == null) {
            anchor = getProperty ("anchor", "");
        }
        return anchor;
    }
}
