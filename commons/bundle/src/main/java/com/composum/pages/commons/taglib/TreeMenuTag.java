package com.composum.pages.commons.taglib;

import javax.servlet.jsp.PageContext;
import java.io.IOException;

/**
 * the action menu tag a dropdown menu in a tree toolbar
 */
public class TreeMenuTag extends AbstractWrappingTag {

    public static final String MENU_VAR = "menu";

    public static final String RESOURCE_TYPE = "composum/pages/stage/edit/actions/tree/menu";

    protected String key;
    protected String icon;
    protected String label;
    protected String title;

    @Override
    protected void clear() {
        title = null;
        label = null;
        icon = null;
        key = null;
        super.clear();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String id) {
        key = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String key) {
        icon = key;
    }

    public String getLabel() {
        return i18n(label);
    }

    public void setLabel(String text) {
        label = text;
    }

    public String getTitle() {
        return i18n(title);
    }

    public void setTitle(String text) {
        title = text;
    }

    @Override
    protected void prepareTagStart() {
        pageContext.setAttribute(MENU_VAR, this, PageContext.REQUEST_SCOPE);
    }

    @Override
    protected void renderTagStart() throws IOException {
        includeSnippet(RESOURCE_TYPE, "menu-start");
    }

    @Override
    protected void renderTagEnd() throws IOException {
        includeSnippet(RESOURCE_TYPE, "menu-end");
    }

    @Override
    protected void finishTagEnd() {
        pageContext.removeAttribute(MENU_VAR, PageContext.REQUEST_SCOPE);
    }
}

