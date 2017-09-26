package com.composum.pages.commons.taglib;

import com.composum.pages.commons.servlet.EditServlet;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.Map;

import static com.composum.pages.commons.taglib.ElementTag.PAGES_EDIT_DATA_NAME;
import static com.composum.pages.commons.taglib.ElementTag.PAGES_EDIT_DATA_PATH;
import static com.composum.pages.commons.taglib.ElementTag.PAGES_EDIT_DATA_TYPE;

/**
 * the PageBodyTag creates the HTML body tag and the EDIT elements around the page content
 */
public class EditToolbarTag extends AbstractWrappingTag {

    public static final String TOOLBAR_VAR = "toolbar";
    public static final String TOOLBAR_CSS_VAR = TOOLBAR_VAR + "CssBase";

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
    protected void collectAttributes(Map<String, String> attributeSet) {
        super.collectAttributes(attributeSet);
        Resource resourceToEdit = (Resource) request.getAttribute(EditServlet.EDIT_RESOURCE_KEY);
        if (resourceToEdit != null) {
            attributeSet.put(PAGES_EDIT_DATA_NAME, resourceToEdit.getName());
            attributeSet.put(PAGES_EDIT_DATA_PATH, resourceToEdit.getPath());
            attributeSet.put(PAGES_EDIT_DATA_TYPE, resourceToEdit.getResourceType());
        }
    }

    @Override
    protected void prepareTagStart() {
        pageContext.setAttribute(TOOLBAR_VAR, this, PageContext.REQUEST_SCOPE);
        if (StringUtils.isNotBlank(cssBase)) {
            pageContext.setAttribute(TOOLBAR_CSS_VAR, cssBase, PageContext.REQUEST_SCOPE);
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
            pageContext.removeAttribute(TOOLBAR_CSS_VAR, PageContext.REQUEST_SCOPE);
        }
        pageContext.removeAttribute(TOOLBAR_VAR, PageContext.REQUEST_SCOPE);
    }
}
