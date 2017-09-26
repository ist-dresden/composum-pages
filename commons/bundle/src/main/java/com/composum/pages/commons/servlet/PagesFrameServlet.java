package com.composum.pages.commons.servlet;

import com.composum.pages.commons.request.DisplayMode;
import com.composum.pages.commons.request.RequestAspect;
import com.composum.pages.commons.request.RequestLocale;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.servlet.AbstractConsoleServlet;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

import java.util.regex.Pattern;

/**
 * The general hook (servlet) for the Pages edit stage; provides the path '/bin/pages.html/...'.
 */
@SlingServlet(
        paths = "/bin/pages",
        methods = {"GET"}
)
public class PagesFrameServlet extends AbstractConsoleServlet {

    public static final String RESOURCE_TYPE = "composum/pages/stage/{mode}/frame";

    public static final Pattern PATH_PATTERN = Pattern.compile("^(/bin/pages(\\.[^/]+)?\\.html)(/.*)?$");

    protected Pattern getPathPattern(BeanContext context) {
        return PATH_PATTERN;
    }

    /**
     * Adds some hints that this request is a 'Pages Frame' request; used to control the 'persistence' of
     * the selected display mode and selected language (locale)
     * @see RequestAspect#isFrameContext(BeanContext, String)
     */
    @Override
    protected BeanContext createContext(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        BeanContext context = super.createContext(request, response);
        context.setAttribute(RequestAspect.FRAME_CONTEXT_ATTR + ":" + DisplayMode.ATTRIBUTE_KEY,
                Boolean.TRUE, BeanContext.Scope.request);
        context.setAttribute(RequestAspect.FRAME_CONTEXT_ATTR + ":" + RequestLocale.ATTRIBUTE_KEY,
                Boolean.TRUE, BeanContext.Scope.request);
        return context;
    }

    /**
     * Returns the resource type for the Pages stage frame according to the defined display mode.
     */
    protected String getResourceType(BeanContext context) {
        DisplayMode.Value mode = DisplayMode.current(context);
        if (DisplayMode.Value.NONE == mode &&
                !DisplayMode.Value.NONE.name().equalsIgnoreCase(
                        context.getRequest().getParameter(DisplayMode.PARAMETER_NAME))) {
            // replace stored 'NONE' by 'EDIT' if this 'Pages' servlet is used and store the new mode
            mode = DisplayMode.Value.EDIT;
            DisplayMode.instance.setValue(context, mode.name());
        }
        switch (mode) {
            case DEVELOP: // no extra view for the 'develop' mode
                mode = DisplayMode.Value.EDIT;
                break;
        }
        return RESOURCE_TYPE.replaceAll("\\{mode}", mode.name().toLowerCase());
    }
}