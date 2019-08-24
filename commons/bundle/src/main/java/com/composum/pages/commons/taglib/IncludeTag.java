package com.composum.pages.commons.taglib;

import com.composum.pages.commons.request.DisplayMode;
import com.composum.pages.commons.util.ResourceTypeUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.ExpressionUtil;
import com.composum.pages.commons.util.LinkUtil;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.scripting.jsp.taglib.IncludeTagHandler;
import org.apache.sling.scripting.jsp.util.TagUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspException;
import java.io.IOException;

@SuppressWarnings("serial")
public class IncludeTag extends IncludeTagHandler {

    /** Takes the {@link #setMode(String)} from the request. */
    public static final String MODE_REQUEST = "REQUEST";

    private static final Logger LOG = LoggerFactory.getLogger(IncludeTag.class);

    protected Resource resource;
    protected String path;
    protected String resourceType;
    protected String subtype;
    protected String mode;
    protected boolean dynamic;
    protected Object test;

    private transient Boolean testResult;
    private transient BeanContext context;
    private transient ExpressionUtil expressionUtil;

    public void setResource(final Resource resource) {
        if (ResourceUtil.isSyntheticResource(resource)) {
            if (StringUtils.isBlank(path)) {
                setPath(resource.getPath());
            }
            if (StringUtils.isBlank(resourceType)) {
                setResourceType(resource.getResourceType());
            }
        } else {
            super.setResource(this.resource = resource);
        }
    }

    public void setPath(String path) {
        super.setPath(this.path = path);
    }

    public void setResourceType(String type) {
        super.setResourceType(this.resourceType = type);
    }

    public void setSubtype(String type) {
        this.subtype = type;
    }

    /**
     * the 'test' expression for conditional tags
     */
    public void setTest(Object value) {
        test = value;
    }

    /**
     * the optional display mode for the include (Pages editing mode), or "request" to take the mode from the request
     * itself (to reset overrides).
     */
    public void setMode(String mode) {
        if (MODE_REQUEST.equalsIgnoreCase(mode)) this.mode = MODE_REQUEST;
        else try {
            this.mode = DisplayMode.Value.valueOf(mode.toUpperCase()).name();
        } catch (Exception ex) {
            LOG.error(ex.toString());
        }
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    @Override
    public void release() {
        clear();
        super.release();
    }

    private void clear() {
        dynamic = false;
        mode = null;
        subtype = null;
        resourceType = null;
        path = null;
        resource = null;
        context = null;
        test = null;
        testResult = null;
        expressionUtil = null;
    }

    // Initialization

    protected void configure() {
        /*
        TODO PagesConfiguration to overlay resource types on include
        */
        if (StringUtils.isNotBlank(subtype)) {
            final SlingHttpServletRequest request = TagUtil.getRequest(pageContext);
            final ResourceResolver resolver = request.getResourceResolver();
            String subtypePath = ResourceTypeUtil.getSubtypePath(resolver,
                    resource != null ? resource : resolver.getResource(path),
                    resourceType, subtype, null);
            if (StringUtils.isNotBlank(subtypePath)) {
                super.setResourceType(subtypePath);
            }
        }
    }

    public BeanContext getContext() {
        if (context == null) {
            context = new BeanContext.Page(pageContext);
        }
        return context;
    }

    // Tag Interface

    @Override
    public int doStartTag() throws JspException {
        if (getTestResult()) {
            int result = super.doStartTag();
            configure();
            if (StringUtils.isNotBlank(mode)) {
                DisplayMode.Value modeValue;
                if (MODE_REQUEST.equalsIgnoreCase(mode))
                    modeValue = DisplayMode.requested(getContext());
                else modeValue = DisplayMode.Value.valueOf(mode);
                DisplayMode.get(getContext()).push(modeValue);
            }
            return result;
        } else {
            return SKIP_BODY;
        }
    }

    @Override
    public int doEndTag() throws JspException {
        if (getTestResult()) {
            int returnValue;
            if (dynamic) {
                returnValue = includeVirtual();
            } else {
                returnValue = super.doEndTag();
            }
            if (StringUtils.isNotBlank(mode)) {
                DisplayMode.get(getContext()).pop();
            }
            clear();
            return returnValue;
        } else {
            clear();
            return EVAL_PAGE;
        }
    }

    /** Returns or creates the expressionUtil. Not null. */
    protected com.composum.sling.core.util.ExpressionUtil getExpressionUtil() {
        if (expressionUtil == null) {
            expressionUtil = new ExpressionUtil(pageContext);
        }
        return expressionUtil;
    }

    /**
     * evaluates the test expression if present and returns the evaluation result; default: 'true'
     */
    protected boolean getTestResult() {
        if (testResult == null) {
            testResult = getExpressionUtil().eval(test, test instanceof Boolean ? (Boolean) test : Boolean.TRUE);
        }
        return testResult;
    }

    // dynamic include implementation ...

    /**
     * Renders an include tag for Apache (needs Apache SSI module) instead of the actual include.
     */
    protected int includeVirtual() {
        StringBuilder url = new StringBuilder();
        SlingHttpServletRequest request = (SlingHttpServletRequest) pageContext.getRequest();
        url.append(LinkUtil.getUrl(request, resource != null ? resource.getPath() : path));
        url.append("?dynamic=true");
        try {
            pageContext.getOut().print(
                    "<!--#include virtual=\"" + url + "\" -->\n");
        } catch (IOException ioex) {
            LOG.error(ioex.getMessage(), ioex);
        }
        return EVAL_PAGE;
    }

}
