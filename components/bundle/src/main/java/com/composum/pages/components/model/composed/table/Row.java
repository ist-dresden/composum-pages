/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.components.model.composed.table;

import com.composum.pages.commons.model.Container;

public class Row extends Container {

    public static final String PN_HEAD= "head";
    public static final String PN_LEVEL = "level";

    public static final String DEFAULT_LEVEL = "";

    private transient Boolean head;
    private transient String level;

    public boolean isHead() {
        if (head == null) {
            head = getProperty(PN_HEAD, null, Boolean.FALSE);
        }
        return head;
    }

    public String getLevel() {
        if (level == null) {
            level = getProperty(PN_LEVEL, DEFAULT_LEVEL);
        }
        return level;
    }
}
