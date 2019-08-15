/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.components.model.composed.table;

import com.composum.pages.commons.model.Element;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;

import static com.composum.pages.components.model.composed.table.Row.DEFAULT_LEVEL;
import static com.composum.pages.components.model.composed.table.Row.PN_HEAD;
import static com.composum.pages.components.model.composed.table.Row.PN_LEVEL;
import static com.composum.pages.components.model.text.Text.PROP_TEXT;

public class Cell extends Element {

    public static final String PN_TEXT = PROP_TEXT;
    public static final String PN_TEXT_ALIGN = "textAlignment";
    public static final String PN_VERTICAL_ALIGN = "verticalAlign";
    public static final String PN_WIDTH = "width";
    public static final String PN_ROWSPAN = "rowspan";
    public static final String PN_COLSPAN = "colspan";

    public static final String DEF_TEXT_ALIGN = "left";
    public static final String DEF_VERTICAL_ALIGN = "top";

    private transient String text;
    private transient String textAlign;
    private transient String vericalAlign;

    private transient Boolean head;
    private transient String level;
    private transient String width;
    private transient Integer rowspan;
    private transient Integer colspan;

    private transient String tdAttributes;

    public boolean isHead() {
        if (head == null) {
            head = getProperty(PN_HEAD, null, Boolean.class);
            if (head == null) {
                head = false;
                Resource row = getResource().getParent();
                if (row != null) {
                    head = row.getValueMap().get(PN_HEAD, Boolean.FALSE);
                }
            }
        }
        return head;
    }

    @Nonnull
    @Override
    public String getType() {
        return isHead() ? "th" : "td";
    }

    public String getTdAttributes() {
        if (tdAttributes == null) {
            StringBuilder attrs = new StringBuilder();
            String value;
            if (StringUtils.isNotBlank(value = getWidth())) {
                attrs.append(" width=\"").append(value).append("\"");
            }
            if (getHasRowspan()) {
                attrs.append(" rowspan=\"").append(getRowspan()).append("\"");
            }
            if (getHasColspan()) {
                attrs.append(" colspan=\"").append(getColspan()).append("\"");
            }
            tdAttributes = attrs.toString();
        }
        return tdAttributes;
    }

    public String getTextAlign() {
        if (textAlign == null) {
            textAlign = getProperty(PN_TEXT_ALIGN, null, DEF_TEXT_ALIGN);
        }
        return textAlign;
    }

    public String getVerticalAlign() {
        if (vericalAlign == null) {
            vericalAlign = getProperty(PN_VERTICAL_ALIGN, null, DEF_VERTICAL_ALIGN);
        }
        return vericalAlign;
    }

    public String getWarningLevel() {
        if (level == null) {
            level = getProperty(PN_LEVEL, DEFAULT_LEVEL);
        }
        return level;
    }

    public String getWidth() {
        if (width == null) {
            width = getProperty(PN_WIDTH, null, "");
        }
        return width;
    }

    public boolean getHasRowspan() {
        return getRowspan() > 1;
    }

    public int getRowspan() {
        if (rowspan == null) {
            rowspan = getProperty(PN_ROWSPAN, null, 0);
        }
        return rowspan;
    }

    public boolean getHasColspan() {
        return getColspan() > 1;
    }

    public int getColspan() {
        if (colspan == null) {
            colspan = getProperty(PN_COLSPAN, null, 0);
        }
        return colspan;
    }

    public boolean getHasText() {
        return StringUtils.isNotBlank(getText());
    }

    @Nonnull
    public String getText() {
        if (text == null) {
            text = getProperty(PN_TEXT, "");
        }
        return text;
    }
}
