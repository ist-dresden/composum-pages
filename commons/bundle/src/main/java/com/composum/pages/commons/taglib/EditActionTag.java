package com.composum.pages.commons.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

import static com.composum.pages.commons.taglib.AbstractPageTag.STAGE_COMPONENT_BASE;

/**
 * the action tag an action of a element toolbar or a tree toolbar
 */
public class EditActionTag extends AbstractWrappingTag {

    public static final String ACTION_VAR = "action";

    public static final String ACTION_TAG_PATH = STAGE_COMPONENT_BASE + "edit/actions/toolbar";

    protected String icon;
    protected String label;
    protected String condition;
    protected String action;
    protected String title;

    @Override
    protected void clear() {
        title = null;
        action = null;
        condition = null;
        label = null;
        icon = null;
        super.clear();
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

    public String getCondition() {
        return condition;
    }

    public void setCondition(String script) {
        condition = script;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String script) {
        action = script;
    }

    public String getTitle() {
        return i18n(title);
    }

    public void setTitle(String text) {
        title = text;
    }

    protected String getSnippetResourceType() {
        return ACTION_TAG_PATH;
    }

    /**
     * ensure that each additional attribute starts with 'data-'
     */
    @Override
    protected void setDynamicAttribute(String key, Object value) throws JspException {
        super.setDynamicAttribute(key.startsWith("data-") ? key : "data-" + key, value);
    }

    @Override
    protected void prepareTagStart() {
        pageContext.setAttribute(ACTION_VAR, this, PageContext.REQUEST_SCOPE);
    }

    @Override
    protected void renderTagStart() throws JspException, IOException {
        includeSnippet(getSnippetResourceType(), "");
    }

    @Override
    protected void renderTagEnd() throws JspException {
    }

    @Override
    protected void finishTagEnd() {
        pageContext.removeAttribute(ACTION_VAR, PageContext.REQUEST_SCOPE);
    }
}
