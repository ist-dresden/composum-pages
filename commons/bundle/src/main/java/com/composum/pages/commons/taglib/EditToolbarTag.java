package com.composum.pages.commons.taglib;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

/**
 * the PageBodyTag creates the HTML body tag and the EDIT elements around the page content
 */
public class EditToolbarTag extends AbstractEditTag {

    public static final String TOOLBAR_VAR = "toolbar";
    public static final String TOOLBAR_CSS_VAR = TOOLBAR_VAR + "CssBase";
    public static final String TOOLBAR_CSSBASE_VAR = TOOLBAR_VAR + "CssBase";

    public static final String DEFAULT_CSS_BASE = "composum-pages-stage-edit-toolbar";

    protected String tagId;

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String id) {
        tagId = id;
    }

    @Override
    public int doStartTag() throws JspException {
        if (StringUtils.isBlank(cssBase)) {
            cssBase = DEFAULT_CSS_BASE;
        }
        return super.doStartTag();
    }

    @Override
    protected void clear() {
        tagId = null;
        super.clear();
    }

    @Override
    protected void prepareTagStart() {
        pageContext.setAttribute(TOOLBAR_VAR, this, PageContext.REQUEST_SCOPE);
        if (StringUtils.isNotBlank(cssBase)) {
            pageContext.setAttribute(TOOLBAR_CSS_VAR, cssBase, PageContext.REQUEST_SCOPE);
            pageContext.setAttribute(TOOLBAR_CSSBASE_VAR, cssBase, PageContext.REQUEST_SCOPE);
        }
    }

    @Override
    protected void renderTagStart() throws IOException {
        out.append("<div").append(getAttributes()).append(">\n");
    }

    @Override
    protected void renderTagEnd() throws IOException {
        out.append("</div>\n");
    }

    @Override
    protected void finishTagEnd() {
        if (StringUtils.isNotBlank(cssBase)) {
            pageContext.removeAttribute(TOOLBAR_CSSBASE_VAR, PageContext.REQUEST_SCOPE);
        }
        pageContext.removeAttribute(TOOLBAR_VAR, PageContext.REQUEST_SCOPE);
    }
}
