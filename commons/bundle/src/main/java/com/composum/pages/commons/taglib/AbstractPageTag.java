package com.composum.pages.commons.taglib;

import com.composum.pages.commons.request.DisplayMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspException;

import java.io.IOException;

import static com.composum.pages.commons.request.DisplayMode.Value.EDIT;

/**
 * the base class for page rendering tags
 */
public abstract class AbstractPageTag extends AbstractWrappingTag {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPageTag.class);

    public static final String COMMONS_COMPONENT_BASE = "composum/pages/commons/";
    public static final String STAGE_COMPONENT_BASE = "composum/pages/stage/";

    public static final String PAGE_PATH = "/page";

    @Override
    protected void clear() {
        super.clear();
    }

    protected String getSnippetDisplayMode() {
        DisplayMode.Value mode = DisplayMode.current(context);
        switch (mode){
            case DEVELOP:
                mode = EDIT;
        }
        return mode.name().toLowerCase();
    }

    protected String getSnippetResourceType() {
        return STAGE_COMPONENT_BASE + getSnippetDisplayMode() + PAGE_PATH;
    }

    protected boolean includeSnippet(String selector) throws JspException, IOException {
        return includeSnippet(getSnippetResourceType(), selector);
    }
}
