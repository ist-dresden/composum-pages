package com.composum.pages.commons.taglib;

import com.composum.pages.commons.model.Model;
import com.composum.pages.commons.request.DisplayMode;
import com.composum.pages.commons.util.TagCssClasses;
import com.composum.sling.cpnl.CpnlBodyTagSupport;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspException;
import java.io.IOException;

import static com.composum.pages.commons.taglib.ElementTag.DEFAULT_TAG;
import static com.composum.pages.commons.taglib.ElementTag.NONE_TAG;
import static com.composum.pages.commons.taglib.ModelTag.PAGES_EDIT_DATA;
import static com.composum.pages.commons.taglib.ModelTag.PAGES_EDIT_DATA_PATH;
import static com.composum.pages.commons.taglib.ModelTag.PAGES_EDIT_DATA_TYPE;

/**
 * the general action tag to embed action calls to the edit layer into a page
 */
public class InvokeTag extends CpnlBodyTagSupport {

    private static final Logger LOG = LoggerFactory.getLogger(InvokeTag.class);

    public static final String INVOKE_ACTION_CSS_CLASS = "composum-pages-action";

    public static final String PAGES_ACTION_DATA_ATTR = PAGES_EDIT_DATA + "-action";
    public static final String PAGES_ACTION_URL_DATA_TYPE = PAGES_EDIT_DATA + "-url";

    protected String tagName;

    protected String action;
    protected String actionUrl;

    protected Resource tagResource;
    protected String path;
    protected String resourceType;

    protected Boolean test;
    private transient Boolean testResult;

    private transient TagCssClasses tagCssClasses;

    protected void clear() {
        tagCssClasses = null;
        testResult = null;
        test = null;
        resourceType = null;
        path = null;
        tagResource = null;
        actionUrl = null;
        action = null;
        tagName = null;
    }

    /**
     * the HTML tag name
     */
    public void setTagName(String value) {
        tagName = value;
    }

    /**
     * the target path
     */
    public void setAction(String value) {
        action = value;
    }

    /**
     * the target path
     */
    public void setActionUrl(String value) {
        actionUrl = value;
    }

    /**
     * the target model
     */
    public void setModel(Model model) {
        tagResource = model.getResource();
    }

    /**
     * the target resource
     */
    public void setResource(Resource resource) {
        tagResource = resource;
    }

    /**
     * the target path
     */
    public void setPath(String value) {
        path = value;
    }

    /**
     * the target resource type
     */
    public void setResourceType(String value) {
        resourceType = value;
    }

    /**
     * the 'test' expression for conditional tags
     */
    public void setTest(Object value) {
        test = (Boolean) value;
    }

    /**
     * return the value of the test expression if present; default: 'EDIT' is requested
     */
    protected boolean getTestResult() {
        if (testResult == null) {
            testResult = test != null ? test : DisplayMode.isEditMode(DisplayMode.requested(context));
        }
        return testResult;
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

    protected TagCssClasses getTagCssClasses() {
        if (tagCssClasses == null) {
            tagCssClasses = new TagCssClasses();
        }
        return tagCssClasses;
    }

    /**
     * renders the tag start HTML element if not 'none' is set for the tag name (tagName='none')
     * if 'none' is set the content is rendered only not the wrapping tag (with no edit capability)
     */
    @Override
    public int doStartTag() throws JspException {
        super.doStartTag();
        if (getTestResult()) {
            if (!NONE_TAG.equalsIgnoreCase(tagName)) {
                try {
                    String path = this.path;
                    if (StringUtils.isBlank(path) || !path.startsWith("/")) {
                        if (tagResource != null) {
                            path = tagResource.getPath();
                        } else {
                            path = resource.getPath();
                        }
                        if (StringUtils.isNotBlank(this.path)) {
                            path += "/" + this.path;
                        }
                    }
                    TagCssClasses cssClasses = getTagCssClasses();
                    if (StringUtils.isBlank(cssClasses.getCssSet())) {
                        cssClasses.getCssClasses().add(INVOKE_ACTION_CSS_CLASS);
                    }
                    out.append("<").append(StringUtils.isNotBlank(tagName) ? tagName : DEFAULT_TAG);
                    out.append(" class=\"").append(cssClasses.toString()).append("\"");
                    out.append(" ").append(PAGES_ACTION_DATA_ATTR).append("=\"").append(action).append("\"");
                    if (StringUtils.isNotBlank(actionUrl)) {
                        out.append(" ").append(PAGES_ACTION_URL_DATA_TYPE).append("=\"").append(actionUrl).append("\"");
                    }
                    out.append(" ").append(PAGES_EDIT_DATA_PATH).append("=\"").append(path).append("\"");
                    if (StringUtils.isNotBlank(resourceType)) {
                        out.append(" ").append(PAGES_EDIT_DATA_TYPE).append("=\"").append(resourceType).append("\"");
                    }
                    out.append(">");
                } catch (IOException ioex) {
                    LOG.error(ioex.getMessage(), ioex);
                }
            }
            return EVAL_BODY_INCLUDE;
        }
        return SKIP_BODY;
    }

    @Override
    public int doEndTag() throws JspException {
        if (getTestResult()) {
            if (!NONE_TAG.equalsIgnoreCase(tagName)) {
                try {
                    out.append("</").append(StringUtils.isNotBlank(tagName) ? tagName : DEFAULT_TAG).append(">");
                } catch (IOException ioex) {
                    LOG.error(ioex.getMessage(), ioex);
                }
            }
        }
        return super.doEndTag();
    }
}
