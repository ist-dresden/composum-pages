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

import javax.servlet.http.HttpSession;
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

    /**
     * Parameter which switches a session to put HTML comments what was included into the page, but only if logger {@value #PARAM_DEBUG_LOG_IN_PAGE} is at debug.
     * Look for &lt;!-- cpp:include begin and &lt;!-- cpp:include end .
     *
     * @see #debugComment(boolean)
     */
    protected static final String PARAM_DEBUG_LOG_IN_PAGE = "com.composum.pages.commons.taglib.IncludeTag-inpage";

    /**
     * Logger {@value #PARAM_DEBUG_LOG_IN_PAGE} that acts as switch that {@link #debugComment(boolean)} inserts HTML-comments into the output that shows what exactly was included.
     */
    protected static final Logger LOGINPAGE = LoggerFactory.getLogger(PARAM_DEBUG_LOG_IN_PAGE);

    protected Resource resource;
    protected String path;
    protected String resourceType;
    protected String subtype;
    protected String mode;
    protected boolean dynamic;
    protected Object test;
    protected String replaceSelectors;
    protected String addSelectors;
    protected String replaceSuffix;

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
        // duplicated since this is annoyingly private in the super class. 8-{
        super.setPath(this.path = path);
    }

    @Override
    public void setResourceType(String type) {
        // duplicated since this is annoyingly private in the super class. 8-{
        super.setResourceType(this.resourceType = type);
    }

    @Override
    public void setReplaceSelectors(String replaceSelectors) {
        // duplicated since this is annoyingly private in the super class. 8-{
        this.replaceSelectors = replaceSelectors;
        super.setReplaceSelectors(replaceSelectors);
    }

    @Override
    public void setAddSelectors(String addSelectors) {
        // duplicated since this is annoyingly private in the super class. 8-{
        this.addSelectors = addSelectors;
        super.setAddSelectors(addSelectors);
    }

    @Override
    public void setReplaceSuffix(String replaceSuffix) {
        // duplicated since this is annoyingly private in the super class. 8-{
        this.replaceSuffix = replaceSuffix;
        super.setReplaceSuffix(replaceSuffix);
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
            debugComment(true);
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
            debugComment(false);
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
        describeInclude(msg);
        // FIXME(hps,10.08.20) log IOExceptions as warning only - that can happen anytime
        LOG.error(msg.toString(), exception);
        try {
            String logType;
            if (resourceType != null) {
                logType = resourceType;
            } else if (resource != null) {
                logType = resource.getResourceType();
            } else if (path != null && context.getResolver() != null && context.getResolver().getResource(path) != null) {
                logType = context.getResolver().getResource(path).getResourceType();
            } else {
                logType = "(unknown)";
            }
            pageContext.getOut().print(" ERROR: include failed at " + Instant.now() + " for resource type " + logType + " ");
        } catch (IOException | RuntimeException ioex) {
            LOG.warn("Could not include error message into page - might be OK", ioex);
        }
    }

    protected void describeInclude(StringBuilder msg) {
        if (path != null && !path.equals(resourceType)) {
            msg.append(" path=").append(path);
        }
        if (resource != null && !resource.getPath().equals(resourceType) && !resource.getPath().equals(path)) {
            msg.append(" resource=").append(resource.getPath());
        }
        if (resourceType != null) {
            msg.append(" resourceType=").append(resourceType);
        } else if (resource != null) {
            msg.append(" resource.getResourceType()=").append(resource.getResourceType());
        }
        if (StringUtils.isNotBlank(replaceSelectors)) {
            msg.append(" replaceSelectors=").append(replaceSelectors);
        }
        if (StringUtils.isNotBlank(addSelectors)) {
            msg.append(" addSelectors=").append(addSelectors);
        }
        if (StringUtils.isNotBlank(replaceSuffix)) {
            msg.append(" replaceSuffix=").append(replaceSuffix);
        }
        if (subtype != null) {
            msg.append(" subtype=").append(subtype);
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
            logIncludeError(ioex);
        }
        return EVAL_PAGE;
    }

    /**
     * Logs an HTML comment with information about the included resource into the page.
     */
    protected void debugComment(boolean start) {
        if (LOGINPAGE.isDebugEnabled() && pageContext.getRequest() != null) {
            SlingHttpServletRequest request = (SlingHttpServletRequest) pageContext.getRequest();
            Boolean dolog = (Boolean) request.getAttribute(PARAM_DEBUG_LOG_IN_PAGE);
            if (dolog == null) {
                String logparam = request.getParameter(PARAM_DEBUG_LOG_IN_PAGE);
                if ("true".equalsIgnoreCase(logparam)) {
                    dolog = true;
                } else if ("false".equalsIgnoreCase(logparam)) {
                    dolog = false;
                }
                HttpSession session = request.getSession(false);
                if (session != null) {
                    if (dolog != null) {
                        session.setAttribute(PARAM_DEBUG_LOG_IN_PAGE, dolog);
                    } else {
                        dolog = Boolean.TRUE.equals(session.getAttribute(PARAM_DEBUG_LOG_IN_PAGE));
                    }
                }
                request.setAttribute(PARAM_DEBUG_LOG_IN_PAGE, dolog);
            }
            if (Boolean.TRUE.equals(dolog)) {
                StringBuilder buf = new StringBuilder(" <!-- cpp:include ");
                buf.append(start ? "begin " : "end ");
                describeInclude(buf);
                buf.append(" --> ");
                try {
                    pageContext.getOut().print(buf.toString());
                } catch (IOException e) {
                    LOG.debug("Probably connection closed - can't write debug comment", e);
                }
            }
        }
    }
}
