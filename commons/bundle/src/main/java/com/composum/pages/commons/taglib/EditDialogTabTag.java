package com.composum.pages.commons.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import java.io.IOException;

import static com.composum.pages.commons.taglib.AbstractPageTag.STAGE_COMPONENT_BASE;
import static com.composum.pages.commons.taglib.EditDialogTag.DIALOG_PATH;
import static com.composum.pages.commons.taglib.EditDialogTag.DIALOG_VAR;

/**
 * the PageBodyTag creates the HTML body tag and the EDIT elements around the page content
 */
public class EditDialogTabTag extends AbstractFormTag {

    public static final String DIALOG_TAB_VAR = "dialogTab";

    protected String tabId;
    protected String label;

    @Override
    protected void clear() {
        label = null;
        tabId = null;
        super.clear();
    }

    protected EditDialogTag getDialog() {
        return (EditDialogTag) pageContext.findAttribute(DIALOG_VAR);
    }

    public String getDialogId() {
        return getDialog().getDialogId();
    }

    public FormAction getDefaultAction(){
        return getDialog().getDefaultAction();
    }

    public String getTabId() {
        return tabId;
    }

    public void setTabId(String id) {
        tabId = id;
    }

    public String getLabel() {
        return i18n(label);
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isDisabledSet() {
        return hasDisabledAttribute() ? getDisabledValue() : getDialog().isDisabledSet();
    }

    @Override
    protected void prepareTagStart() {
        setAttribute(DIALOG_TAB_VAR, this, PageContext.REQUEST_SCOPE);
    }

    protected String getSnippetResourceType() {
        return STAGE_COMPONENT_BASE + DIALOG_PATH;
    }

    @Override
    protected void renderTagStart() throws JspException, IOException {
        includeSnippet(getSnippetResourceType(), "edit-dialog-tab-start");
    }

    @Override
    protected void renderTagEnd() throws JspException, IOException {
        includeSnippet(getSnippetResourceType(), "edit-dialog-tab-end");
    }
}
