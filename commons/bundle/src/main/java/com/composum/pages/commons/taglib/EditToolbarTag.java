package com.composum.pages.commons.taglib;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

/**
 * Creates the html markup for an edit toolbar.
 */
public class EditToolbarTag extends AbstractEditTag {

    public static final String TOOLBAR_VAR = "toolbar";
    public static final String TOOLBAR_CSS_VAR = TOOLBAR_VAR + "CssBase";
    public static final String TOOLBAR_CSSBASE_VAR = TOOLBAR_VAR + "CssBase";

    public static final String DEFAULT_CSS_BASE = "composum-pages-stage-edit-toolbar";

    @Override
    public int doStartTag() throws JspException {
        if (StringUtils.isBlank(cssBase)) {
            cssBase = DEFAULT_CSS_BASE;
        }
        return super.doStartTag();
    }

    @Override
    protected void prepareTagStart() {
        setAttribute(TOOLBAR_VAR, this, PageContext.REQUEST_SCOPE);
        if (StringUtils.isNotBlank(cssBase)) {
            setAttribute(TOOLBAR_CSS_VAR, cssBase, PageContext.REQUEST_SCOPE);
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
}
