/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.components.model.composed.table;

import com.composum.pages.commons.model.Container;

public class Table extends Container {

    public String getCopyright() {
        return getProperty("copyright", "");
    }

    public String getTableClasses() {
        StringBuilder classes = new StringBuilder("table");
        if (getProperty("striped", Boolean.FALSE)) {
            classes.append(" table-striped");
        }
        if (getProperty("bordered", Boolean.FALSE)) {
            classes.append(" table-bordered");
        }
        if (getProperty("condensed", Boolean.FALSE)) {
            classes.append(" table-condensed");
        }
        if (getProperty("hover", Boolean.FALSE)) {
            classes.append(" table-hover");
        }
        return classes.toString();
    }
}
