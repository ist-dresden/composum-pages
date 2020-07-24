package com.composum.pages.commons.taglib;

import com.composum.pages.commons.request.DisplayMode;
import com.composum.pages.commons.service.Theme;
import com.composum.pages.commons.util.LinkUtil;
import com.composum.pages.commons.util.ResourceTypeUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.ExpressionUtil;
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
import javax.servlet.jsp.tagext.Tag;
import java.io.IOException;
import java.time.Instant;

import static com.composum.pages.commons.PagesConstants.RA_CURRENT_THEME;

@SuppressWarnings("serial")
public class IncludeTag extends IncludeTagHandler {

    /**
     * Takes the {@link #setMode(String)} from the request.
     */
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

    @Override
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

    @Override
    public void setPath(String path) {
        super.setPath(this.path = path);
    }

    @Override
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
        Theme theme = (Theme) pageContext.getRequest().getAttribute(RA_CURRENT_THEME);
        if (theme != null) {
            // resource type overlay driven by a theme...
            final SlingHttpServletRequest request = TagUtil.getRequest(pageContext);
            final ResourceResolver resolver = request.getResourceResolver();
            Resource includeResource = resource != null ? resource : resolver.getResource(path);
            if (includeResource != null) {
                String type = resourceType;
                if (StringUtils.isBlank(type)) {
                    type = includeResource.getResourceType();
                }
                if (StringUtils.isNotBlank(type)) {
                    String overlay = theme.getResourceType(includeResource, type);
                    if (!overlay.equals(type)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("resource type '{}' overlayed by '{}' (theme '{}')", type, overlay, theme.getName());
                        }
                        setResourceType(resourceType = overlay);
                    }
                }
            }
        }
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
    public int doEndTag() {
        if (getTestResult()) {
            int returnValue;
            if (dynamic) {
                returnValue = includeVirtual();
            } else {
                try {
                    returnValue = super.doEndTag();
                } catch (JspException | RuntimeException e) {
                    returnValue = Tag.SKIP_BODY;
                    logIncludeError(e);
                }
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

    /**
     * Logs information about the error and writes some rudimentary information about it into the page such that an user could
     * at least give the timestamp to allow a developer to find the problem in the log.
     */
    // FIXME(hps,20.07.20) should we really never throw up? Perhaps only when response.isCommitted or an attribute is set?
    // In some cases it's better not breaking the page (e.g. component selection dialog) and in some cases better
    // the exception should be thrown to show an error page.
    protected void logIncludeError(Exception exception) {
        StringBuilder msg = new StringBuilder("Error during include");
        msg.append(" in ").append(pageContext.getPage());
        if (resourceType != null) {
            msg.append(" resourceType=").append(resourceType);
        }
        if (path != null && !path.equals(resourceType)) {
            msg.append(" path=").append(path);
        }
        if (resource != null && !resource.getPath().equals(resourceType) && !resource.getPath().equals(path)) {
            msg.append(" resource=").append(resource.getPath());
        }
        if (subtype != null) {
            msg.append(" subtype=").append(subtype);
        }
        LOG.error(msg.toString(), exception);
        try {
            String logType;
            if (resourceType != null ){
                logType = resourceType;
            } else if (resource != null) {
                logType = resource.getResourceType();
            } else if (path != null && context.getResolver() != null && context.getResolver().getResource(path) != null) {
                logType = context.getResolver().getResource(path).getResourceType();
            } else {
                logType = "(unknown)";
            }
            pageContext.getOut().print(" ERROR: include failed at " + Instant.now() + " for resource type " + logType + " ");
        } catch (IOException ioex) {
            LOG.warn("Could not include error message into page - might be OK", ioex);
        }
    }

    /**
     * Returns or creates the expressionUtil. Not null.
     */
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
