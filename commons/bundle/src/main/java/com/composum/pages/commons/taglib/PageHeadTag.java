package com.composum.pages.commons.taglib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

/**
 * the PageBodyTag creates the HTML body tag and the EDIT elements around the page content
 */
public class PageHeadTag extends AbstractPageTag {

    private static final Logger LOG = LoggerFactory.getLogger(PageHeadTag.class);

    public static final String PAGE_HEAD = "pageHead";

    @Override
    protected void prepareTagStart() {
        setAttribute(PAGE_HEAD, this, PageContext.REQUEST_SCOPE);
    }

    @Override
    protected void renderTagStart() throws JspException, IOException {
        includeSnippet("head-start");
    }

    @Override
    protected void renderTagEnd() throws JspException, IOException {
        includeSnippet("head-end");
    }

    @Override
    protected void finishTagEnd() {
    }
}
