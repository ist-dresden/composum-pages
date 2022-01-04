/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.taglib;

import com.composum.pages.commons.util.TagCssClasses;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import java.io.IOException;
import java.util.Map;

import static com.composum.pages.commons.util.TagCssClasses.cssOfType;

/**
 * the base class for all content wrapping tags: prepare - start tag - end tag - finish
 */
public abstract class AbstractWrappingTag extends ModelTag {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractWrappingTag.class);

    /**
     * a string with the complete set of CSS classes (prevents from the generation of the default classes)
     */
    public void setCssSet(String classes) {
        getTagCssClasses().setCssSet(classes);
    }

    /**
     * a string with additional css classes (optional)
     */
    public void setCssAdd(String classes) {
        getTagCssClasses().setCssAdd(classes);
    }

    @Deprecated
    public void setCssClasses(String classes) {
        setCssAdd(classes);
    }

    /**
     * collects the set of CSS classes (extension hook)
     * adds the 'cssBase' itself as CSS class and the transformed resource super type if available
     */
    @Override
    protected void collectCssClasses(TagCssClasses.CssSet collection) {
        if (StringUtils.isBlank(getTagCssClasses().getCssSet())) {
            collection.add(getCssBase());
            collection.add(cssOfType(resource.getResourceSuperType()));
        }
    }

    /**
     * collects the set of tag attributes classes (extension hook)
     * adds the 'class' attribute with all collected CSS classes to the set build by the superclass
     */
    protected void collectAttributes(Map<String, Object> attributeSet) {
        String cssClasses = buildCssClasses();
        if (StringUtils.isNotBlank(cssClasses)) {
            attributeSet.put("class", cssClasses);
        }
        super.collectAttributes(attributeSet);
    }

    //
    // the render steps for all superclasses
    //

    /**
     * if this returns 'false' nothing is rendered, no wrapping tag and no content within
     * this is used if the option 'test' attribute is set; if the test fails this returns 'false'...
     */
    protected boolean renderTag() {
        return getTestResult();
    }

    protected abstract void prepareTagStart();

    protected abstract void renderTagStart() throws JspException, IOException;

    protected abstract void renderTagEnd() throws JspException, IOException;

    protected void finishTagEnd() {
    }

    /**
     * collects all CSS classes and attributes, prepares the rendering (prepareTagStart())
     * and performs the rendering of the tag start (renderTagStart()) if 'renderTag()' returns 'true'
     * stores the resource to edit as request attribute for further use if not always present
     * if 'cssBase' is specified the '{var}CssBase' attribute is set in the page context
     */
    @Override
    public int doStartTag() throws JspException {
        super.doStartTag(); // necessary to initialize the tag for the following 'render test'
        if (renderTag()) {
            TagCssClasses cssClasses = getTagCssClasses();
            String value;
            if (StringUtils.isNotBlank(value = cssClasses.getCssSet())) {
                cssClasses.setCssSet(eval(value, value));
            }
            if (StringUtils.isNotBlank(value = cssClasses.getCssAdd())) {
                cssClasses.setCssAdd(eval(value, value));
            }
            prepareTagStart();
            try {
                out.flush();
                renderTagStart();
                if (LoggerFactory.getLogger(getClass()).isDebugEnabled()) {
                    out.print(getTagDebug());
                }
                out.flush();
            } catch (IOException ioex) {
                LOG.error(ioex.getMessage(), ioex);
            }
            return EVAL_BODY_INCLUDE;
        } else {
            return SKIP_BODY;
        }
    }

    /**
     * performs the rendering of the tag end (renderTagEnd()) followed by the cleanup (finishTagEnd())
     * removes the resource to edit attribute if set by this tag during tag start
     * if '{var}CssBase' was set on start tag this attribute will be removed from the page context
     */
    @Override
    public int doEndTag() throws JspException {
        if (renderTag()) {
            try {
                out.flush();
                renderTagEnd();
                out.flush();
            } catch (IOException ioex) {
                LOG.error(ioex.getMessage(), ioex);
            }
            finishTagEnd();
        }
        super.doEndTag();
        return EVAL_PAGE;
    }

    //
    // helpers
    //

    protected boolean includeSnippet(String resourceType, String selector) throws IOException {
        out.flush();
        if (StringUtils.isNotBlank(resourceType)) {

            RequestDispatcherOptions options = new RequestDispatcherOptions();
            options.setForceResourceType(resourceType);
            if (StringUtils.isNotBlank(selector)) {
                options.setReplaceSelectors(selector);
            }
            RequestDispatcher dispatcher = request.getRequestDispatcher(getResource(), options);

            if (dispatcher != null) {
                try {
                    dispatcher.include(request, pageContext.getResponse());
                    return true;

                } catch (ServletException | IOException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
        }
        return false;
    }
}
