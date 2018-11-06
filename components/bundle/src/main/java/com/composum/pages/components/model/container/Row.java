/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.components.model.container;

import com.composum.pages.commons.model.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Row extends Element {

    private static final Logger LOG = LoggerFactory.getLogger(Row.class);

    private transient String[] columns;

    public static final Map<String, String[]> COLUMN_RULES;

    static {
        COLUMN_RULES = new HashMap<>();
        COLUMN_RULES.put("-12-", new String[]{
                "col-lg-12 col-md-12 col-sm-12 col-xs-12"});
        COLUMN_RULES.put("-6--6-", new String[]{
                "col-lg-6 col-md-6 col-sm-6 col-xs-12",
                "col-lg-6 col-md-6 col-sm-6 col-xs-12"});
        COLUMN_RULES.put("-4--8-", new String[]{
                "col-lg-4 col-md-4 col-sm-4 col-xs-12",
                "col-lg-8 col-md-8 col-sm-8 col-xs-12"});
        COLUMN_RULES.put("-8--4-", new String[]{
                "col-lg-8 col-md-8 col-sm-8 col-xs-12",
                "col-lg-4 col-md-4 col-sm-4 col-xs-12"});
        COLUMN_RULES.put("-4--4--4-", new String[]{
                "col-lg-4 col-md-4 col-sm-6 col-xs-12",
                "col-lg-4 col-md-4 col-sm-6 col-xs-12",
                "col-lg-4 col-md-4 col-sm-6 col-xs-12"});
        COLUMN_RULES.put("-3--6--3-", new String[]{
                "col-lg-3 col-md-3 col-sm-6 col-xs-12",
                "col-lg-6 col-md-6 col-sm-6 col-xs-12",
                "col-lg-3 col-md-3 col-sm-6 col-xs-12"});
    }

    public String[] getColumns() {
        if (columns == null) {
            columns = COLUMN_RULES.get(getProperty("columns", "-12-"));
            if (columns == null) {
                LOG.error("invalid column rule: " + getProperty("columns", ""));
                columns = new String[0];
            }
        }
        return columns;
    }
}
