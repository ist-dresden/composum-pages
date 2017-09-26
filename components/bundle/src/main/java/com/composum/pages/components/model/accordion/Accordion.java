package com.composum.pages.components.model.accordion;

import com.composum.pages.commons.model.Container;

public class Accordion extends Container {

    public static final String PROP_BEHAVIOR = "behavior";

    public enum Behavior {
        accordion, independent
    }

    private transient Behavior behavior;

    public boolean isAccordionMode() {
        return getBehavior() == Behavior.accordion;
    }

    public Behavior getBehavior() {
        if (behavior == null) {
            behavior = Behavior.valueOf(getProperty(PROP_BEHAVIOR, Behavior.accordion.name()));
        }
        return behavior;
    }
}
