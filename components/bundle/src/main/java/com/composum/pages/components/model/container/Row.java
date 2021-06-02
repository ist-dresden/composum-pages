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

    public static final String RA_NARROW_MAIN = "NARROW_MAIN";

    public static final String PN_ANCHOR = "anchor";

    private transient String anchor;
    private transient String[] columns;

    public static final Map<String, String[]> DEFAULT_SPACE;
    public static final Map<String, String[]> NARROW_SPACE;

    static {
        DEFAULT_SPACE = new HashMap<>();
        DEFAULT_SPACE.put("-12-", new String[]{
                "col-xl-12 col-lg-12 col-md-12 col-sm-12 col-12"});
        DEFAULT_SPACE.put("-6--6-", new String[]{
                "col-xl-6 col-lg-6 col-md-6 col-sm-12 col-xs-12 col-12",
                "col-xl-6 col-lg-6 col-md-6 col-sm-12 col-xs-12 col-12"});
        DEFAULT_SPACE.put("-4--8-", new String[]{
                "col-xl-4 col-lg-4 col-md-4 col-sm-12 col-xs-12 col-12",
                "col-xl-8 col-lg-8 col-md-8 col-sm-12 col-xs-12 col-12"});
        DEFAULT_SPACE.put("-8--4-", new String[]{
                "col-xl-8 col-lg-8 col-md-8 col-sm-12 col-xs-12 col-12",
                "col-xl-4 col-lg-4 col-md-4 col-sm-12 col-xs-12 col-12"});
        DEFAULT_SPACE.put("-4--4--4-", new String[]{
                "col-xl-4 col-lg-4 col-md-4 col-sm-12 col-xs-12 col-12",
                "col-xl-4 col-lg-4 col-md-4 col-sm-12 col-xs-12 col-12",
                "col-xl-4 col-lg-4 col-md-4 col-sm-12 col-xs-12 col-12"});
        DEFAULT_SPACE.put("-3--6--3-", new String[]{
                "col-xl-3 col-lg-3 col-md-3 col-sm-12 col-xs-12 col-12",
                "col-xl-6 col-lg-6 col-md-6 col-sm-12 col-xs-12 col-12",
                "col-xl-3 col-lg-3 col-md-3 col-sm-12 col-xs-12 col-12"});
        DEFAULT_SPACE.put("-3--3--3--3-", new String[]{
                "col-xl-3 col-lg-3 col-md-3 col-sm-6 col-xs-12 col-12",
                "col-xl-3 col-lg-3 col-md-3 col-sm-6 col-xs-12 col-12",
                "col-xl-3 col-lg-3 col-md-3 col-sm-6 col-xs-12 col-12",
                "col-xl-3 col-lg-3 col-md-3 col-sm-6 col-xs-12 col-12"});
        DEFAULT_SPACE.put("-5--7-", new String[]{
                "col-xl-5 col-lg-5 col-md-5 col-sm-12 col-xs-12 col-12",
                "col-xl-7 col-lg-7 col-md-7 col-sm-12 col-xs-12 col-12"});
        DEFAULT_SPACE.put("-7--5-", new String[]{
                "col-xl-7 col-lg-7 col-md-7 col-sm-12 col-xs-12 col-12",
                "col-xl-5 col-lg-5 col-md-5 col-sm-12 col-xs-12 col-12"});
        DEFAULT_SPACE.put("-3--9-", new String[]{
                "col-xl-3 col-lg-3 col-md-3 col-sm-12 col-xs-12 col-12",
                "col-xl-9 col-lg-9 col-md-9 col-sm-12 col-xs-12 col-12"});
        DEFAULT_SPACE.put("-9--3-", new String[]{
                "col-xl-9 col-lg-9 col-md-9 col-sm-12 col-xs-12 col-12",
                "col-xl-3 col-lg-3 col-md-3 col-sm-12 col-xs-12 col-12"});
        NARROW_SPACE = new HashMap<>();
        NARROW_SPACE.put("-12-", new String[]{
                "col-xl-12 col-lg-12 col-md-12 col-sm-12 col-12"});
        NARROW_SPACE.put("-6--6-", new String[]{
                "col-xl-6 col-lg-6 col-md-12 col-sm-12 col-xs-12 col-12",
                "col-xl-6 col-lg-6 col-md-12 col-sm-12 col-xs-12 col-12"});
        NARROW_SPACE.put("-4--8-", new String[]{
                "col-xl-4 col-lg-4 col-md-12 col-sm-12 col-xs-12 col-12",
                "col-xl-8 col-lg-8 col-md-12 col-sm-12 col-xs-12 col-12"});
        NARROW_SPACE.put("-8--4-", new String[]{
                "col-xl-8 col-lg-8 col-md-12 col-sm-12 col-xs-12 col-12",
                "col-xl-4 col-lg-4 col-md-12 col-sm-12 col-xs-12 col-12"});
        NARROW_SPACE.put("-4--4--4-", new String[]{
                "col-xl-4 col-lg-4 col-md-12 col-sm-12 col-xs-12 col-12",
                "col-xl-4 col-lg-4 col-md-12 col-sm-12 col-xs-12 col-12",
                "col-xl-4 col-lg-4 col-md-12 col-sm-12 col-xs-12 col-12"});
        NARROW_SPACE.put("-3--6--3-", new String[]{
                "col-xl-3 col-lg-3 col-md-12 col-sm-12 col-xs-12 col-12",
                "col-xl-6 col-lg-6 col-md-12 col-sm-12 col-xs-12 col-12",
                "col-xl-3 col-lg-3 col-md-12 col-sm-12 col-xs-12 col-12"});
        NARROW_SPACE.put("-3--3--3--3-", new String[]{
                "col-xl-3 col-lg-3 col-md-12 col-sm-6 col-xs-12 col-12",
                "col-xl-3 col-lg-3 col-md-12 col-sm-6 col-xs-12 col-12",
                "col-xl-3 col-lg-3 col-md-12 col-sm-6 col-xs-12 col-12",
                "col-xl-3 col-lg-3 col-md-12 col-sm-6 col-xs-12 col-12"});
        NARROW_SPACE.put("-5--7-", new String[]{
                "col-xl-5 col-lg-5 col-md-12 col-sm-12 col-xs-12 col-12",
                "col-xl-7 col-lg-7 col-md-12 col-sm-12 col-xs-12 col-12"});
        NARROW_SPACE.put("-7--5-", new String[]{
                "col-xl-7 col-lg-7 col-md-12 col-sm-12 col-xs-12 col-12",
                "col-xl-5 col-lg-5 col-md-12 col-sm-12 col-xs-12 col-12"});
        NARROW_SPACE.put("-3--9-", new String[]{
                "col-xl-3 col-lg-3 col-md-12 col-sm-12 col-xs-12 col-12",
                "col-xl-9 col-lg-9 col-md-12 col-sm-12 col-xs-12 col-12"});
        NARROW_SPACE.put("-9--3-", new String[]{
                "col-xl-9 col-lg-9 col-md-12 col-sm-12 col-xs-12 col-12",
                "col-xl-3 col-lg-3 col-md-12 col-sm-12 col-xs-12 col-12"});
    }

    public String[] getColumns() {
        if (columns == null) {
            Boolean narrowSpace = (Boolean) getContext().getRequest().getAttribute(RA_NARROW_MAIN);
            columns = (narrowSpace != null && narrowSpace ? NARROW_SPACE : DEFAULT_SPACE)
                    .get(getProperty("columns", "-12-"));
            if (columns == null) {
                LOG.error("invalid column rule: " + getProperty("columns", ""));
                columns = new String[0];
            }
        }
        return columns;
    }

    public String getAnchor() {
        if (anchor == null) {
            anchor = getProperty(PN_ANCHOR, null, "");
        }
        return anchor;
    }
}
