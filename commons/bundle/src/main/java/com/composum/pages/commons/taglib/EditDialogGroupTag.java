package com.composum.pages.commons.taglib;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import java.io.IOException;

import static com.composum.pages.commons.taglib.AbstractPageTag.STAGE_COMPONENT_BASE;
import static com.composum.pages.commons.taglib.EditDialogTabTag.DIALOG_TAB_VAR;
import static com.composum.pages.commons.taglib.EditDialogTag.DIALOG_PATH;
import static com.composum.pages.commons.taglib.EditDialogTag.DIALOG_VAR;

/**
 * the PageBodyTag creates the HTML body tag and the EDIT elements around the page content
 */
public class EditDialogGroupTag extends AbstractEditElementTag {

    public static final String DIALOG_GROUP_VAR = "dialogGroup";

    protected String groupId;
    protected String label;
    protected Boolean expanded;

    @Override
    protected void clear() {
        expanded = null;
        label = null;
        groupId = null;
        super.clear();
    }

    protected EditDialogTag getDialog() {
        return (EditDialogTag) pageContext.findAttribute(DIALOG_VAR);
    }

    public String getDialogId() {
        return getDialog().getDialogId();
    }

    public String getGroupId() {
        return StringUtils.isNotBlank(groupId) ? groupId : "group-" + getLabel().toLowerCase().replace(' ', '-');
    }

    public void setGroupId(String id) {
        groupId = id;
    }

    public String getLabel() {
        return i18n(label);
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isExpanded() {
        return expanded != null && expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isDisabled() {
        Boolean result = null;
        if (hasDisabledAttribute()) {
            result = getDisabledValue();
        } else {
            EditDialogTabTag tabTag = (EditDialogTabTag) pageContext.findAttribute(DIALOG_TAB_VAR);
            if (tabTag != null && tabTag.hasDisabledAttribute()) {
                result = tabTag.getDisabledValue();
            }
        }
        return result != null ? result : getDialog().getDisabledValue();
    }

    @Override
    protected void prepareTagStart() {
        pageContext.setAttribute(DIALOG_GROUP_VAR, this, PageContext.REQUEST_SCOPE);
    }

    protected String getSnippetResourceType() {
        return STAGE_COMPONENT_BASE + DIALOG_PATH;
    }

    @Override
    protected void renderTagStart() throws JspException, IOException {
        includeSnippet(getSnippetResourceType(), "edit-dialog-group-start");
    }

    @Override
    protected void renderTagEnd() throws JspException, IOException {
        includeSnippet(getSnippetResourceType(), "edit-dialog-group-end");
    }

    @Override
    protected void finishTagEnd() {
        pageContext.removeAttribute(DIALOG_GROUP_VAR, PageContext.REQUEST_SCOPE);
    }
}
