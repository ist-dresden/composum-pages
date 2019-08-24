package com.composum.pages.components.model.composed.accordion;

import com.composum.pages.commons.model.Container;

public class AccordionItem extends Container {

    public static final String PROP_INITIAL_OPEN = "initialOpen";

    private transient Boolean initialOpen;

    public boolean isInitialOpen() {
        if (initialOpen == null) {
            initialOpen = getProperty(PROP_INITIAL_OPEN, Boolean.FALSE);
        }
        return initialOpen;
    }
}
