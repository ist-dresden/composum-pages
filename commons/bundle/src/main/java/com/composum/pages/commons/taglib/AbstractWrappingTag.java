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
import java.util.LinkedHashMap;
import java.util.Map;

import static com.composum.pages.commons.servlet.EditServlet.EDIT_RESOURCE_KEY;
import static com.composum.pages.commons.util.TagCssClasses.cssOfType;

/**
 * the base class for all content wrapping tags: prepare - start tag - end tag - finish
 */
public abstract class AbstractWrappingTag extends ModelTag {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractWrappingTag.class);

    public static final String TAG_ID = "id";

    protected Object editResource;

    private transient TagCssClasses tagCssClasses;
    private transient String attributes;

    @Override
    protected void clear() {
        attributes = null;
        tagCssClasses = null;
        editResource = null;
        super.clear();
    }

    protected TagCssClasses getTagCssClasses() {
        if (tagCssClasses == null) {
            tagCssClasses = new TagCssClasses();
        }
        return tagCssClasses;
    }

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
     * builds the complete CSS classes string with the given classes and all collected classes
     */
    public String buildCssClasses() {
        TagCssClasses.CssSet collection = getTagCssClasses().getCssClasses();
        collectCssClasses(collection);
        return getTagCssClasses().toString();
    }

    /**
     * collects the set of CSS classes (extension hook)
     * adds the 'cssBase' itself as CSS class and the transformed resource super type if available
     */
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
    protected void collectAttributes(Map<String, String> attributeSet) {
        String cssClasses = buildCssClasses();
        if (StringUtils.isNotBlank(cssClasses)) {
            attributeSet.put("class", cssClasses);
        }
        super.collectAttributes(attributeSet);
    }

    /**
     * returns the complete set of attributes as one string value with a leading space
     * provided to embed all attributes in a template (JSP or something else)
     */
    public String getAttributes() {
        if (attributes == null) {
            StringBuilder builder = new StringBuilder();
            Map<String, String> attributeSet = new LinkedHashMap<>();
            collectAttributes(attributeSet);
            for (Map.Entry<String, String> attribute : attributeSet.entrySet()) {
                builder.append(" ").append(attribute.getKey()).append("=\"").append(attribute.getValue()).append("\"");
            }
            attributes = builder.toString();
        }
        return attributes;
    }

    //
    // the render steps for all superclasses
    //

    /**
     * if this returns 'false' nothing is rendered, no wrapping tag and no content within
     * (extension hook; returns 'true')
     */
    protected boolean renderTag() {
        return true;
    }

    protected abstract void prepareTagStart();

    protected abstract void renderTagStart() throws JspException, IOException;

    protected abstract void renderTagEnd() throws JspException, IOException;

    protected abstract void finishTagEnd();

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
            /* FIXME remove?!
            if (request.getAttribute(EDIT_RESOURCE_KEY) == null) {
                request.setAttribute(EDIT_RESOURCE_KEY, editResource = resource);
            }
            */
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
            if (editResource != null) {
                request.removeAttribute(EDIT_RESOURCE_KEY);
                editResource = null;
            }
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
