package com.composum.pages.commons.taglib;

import com.composum.pages.commons.util.TagCssClasses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

/**
 * the PageBodyTag creates the HTML body tag and the EDIT elements around the page content
 */
public class PageBodyTag extends AbstractPageTag {

    private static final Logger LOG = LoggerFactory.getLogger(PageBodyTag.class);

    public static final String PAGE_EDIT_BODY_CLASSES = "composum-pages-EDIT_body";

    public static final String PAGE_BODY = "pageBody";

    protected String tagId;

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String id) {
        tagId = id;
    }

    protected void collectCssClasses(TagCssClasses.CssSet collection) {
        super.collectCssClasses(collection);
        if (isEditMode()) {
            collection.add(PAGE_EDIT_BODY_CLASSES);
        }
    }

    @Override
    protected void prepareTagStart() {
        setAttribute(PAGE_BODY, this, PageContext.REQUEST_SCOPE);
    }

    protected void renderTagStart() throws JspException, IOException {
        includeSnippet("body-start");
    }

    protected void renderTagEnd() throws JspException, IOException {
        includeSnippet("body-end");
    }

    protected void finishTagEnd() {
    }
}
