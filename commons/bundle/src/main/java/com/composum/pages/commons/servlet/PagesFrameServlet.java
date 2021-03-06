package com.composum.pages.commons.servlet;

import com.composum.pages.commons.request.DisplayMode;
import com.composum.pages.commons.request.PagesLocale;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.servlet.AbstractConsoleServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import javax.servlet.http.HttpSession;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.composum.pages.commons.PagesConstants.DISPLAY_MODE_ATTR;
import static com.composum.pages.commons.PagesConstants.DISPLAY_MODE_SELECT_PARAM;
import static com.composum.pages.commons.PagesConstants.PAGES_FRAME_PATH;
import static com.composum.pages.commons.PagesConstants.RA_PAGES_LOCALE;

/**
 * The general hook (servlet) for the Pages edit stage; provides the path '/bin/pages.html/...'.
 */
@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Frame Servlet",
                ServletResolverConstants.SLING_SERVLET_PATHS + "=" + PAGES_FRAME_PATH,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET
        })
public class PagesFrameServlet extends AbstractConsoleServlet {

    public static final String RESOURCE_TYPE = "composum/pages/stage/{mode}/frame";

    public static final String CONSOLE_PATH = "/libs/composum/pages/stage/edit/console/content";

    public static final Pattern PATH_PATTERN = Pattern.compile("^(" + PAGES_FRAME_PATH + "(\\.[^/]+)?\\.html)(/.*)?$");

    @Override
    protected String getServletPath(BeanContext context) {
        return PAGES_FRAME_PATH;
    }

    @Override
    protected Pattern getPathPattern(BeanContext context) {
        return PATH_PATTERN;
    }

    /**
     * Returns the resource type for the Pages stage frame according to the defined display mode.
     */
    @Override
    protected String getResourceType(BeanContext context) {
        SlingHttpServletRequest request = context.getRequest();
        DisplayMode.Value mode = getDisplayMode(context);
        // set the selected mode for the frame rendering
        Objects.requireNonNull(request.adaptTo(DisplayMode.class)).reset(mode);
        //noinspection SwitchStatementWithTooFewBranches
        switch (mode) {
            case DEVELOP: // no extra view for the 'develop' mode
                mode = DisplayMode.Value.EDIT;
                break;
        }
        return RESOURCE_TYPE.replaceAll("\\{mode}", mode.name().toLowerCase());
    }

    /**
     * manages the display mode of the frame controlled by the 'pages.mode' parameter;
     * the selected mode is stored in the session and used up to the next switch
     */
    protected DisplayMode.Value getDisplayMode(BeanContext context) {
        SlingHttpServletRequest request = context.getRequest();
        HttpSession session = request.getSession(true);
        DisplayMode.Value modeSwitch = DisplayMode.Value.displayModeValue(
                request.getParameter(DISPLAY_MODE_SELECT_PARAM), null);
        if (modeSwitch != null) {
            session.setAttribute(DISPLAY_MODE_ATTR, modeSwitch.name());
            return modeSwitch;
        } else {
            return DisplayMode.Value.displayModeValue(
                    session.getAttribute(DISPLAY_MODE_ATTR), DisplayMode.Value.EDIT);
        }
    }

    @Override
    protected void prepareForward(BeanContext context, RequestDispatcherOptions options) {
        super.prepareForward(context, options);
        SlingHttpServletRequest request = context.getRequest();
        HttpSession session = request.getSession();
        if (session != null) {
            try {
                Locale locale = (Locale) session.getAttribute(RA_PAGES_LOCALE);
                if (locale != null) {
                    // predefine the sessions language for the request in an edit context
                    request.setAttribute(RA_PAGES_LOCALE, new PagesLocale(locale));
                }
            } catch (ClassCastException ignore){
            }
        }
    }

    @Override
    protected String getConsolePath(BeanContext context) {
        return CONSOLE_PATH;
    }
}