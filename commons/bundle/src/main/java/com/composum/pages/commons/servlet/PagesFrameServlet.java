package com.composum.pages.commons.servlet;

import com.composum.pages.commons.request.DisplayMode;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.servlet.AbstractConsoleServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import javax.servlet.http.HttpSession;
import java.util.regex.Pattern;

import static com.composum.pages.commons.PagesConstants.DISPLAY_MODE_ATTR;
import static com.composum.pages.commons.PagesConstants.DISPLAY_MODE_REQ_PARAM;
import static com.composum.pages.commons.PagesConstants.PAGES_FRAME_PATH;

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

    public static final String DISPLAY_MODE_SWITCH_PARAM = DISPLAY_MODE_REQ_PARAM + ".switch";

    protected Pattern getPathPattern(BeanContext context) {
        return PATH_PATTERN;
    }

    /**
     * Returns the resource type for the Pages stage frame according to the defined display mode.
     */
    protected String getResourceType(BeanContext context) {
        SlingHttpServletRequest request = context.getRequest();
        DisplayMode.Value mode = getDisplayMode(context);
        request.adaptTo(DisplayMode.class).reset(mode); // yset the selected mode for the frame rendering
        switch (mode) {
            case DEVELOP: // no extra view for the 'develop' mode
                mode = DisplayMode.Value.EDIT;
                break;
        }
        return RESOURCE_TYPE.replaceAll("\\{mode}", mode.name().toLowerCase());
    }

    /**
     * manages the display mode of the frame controlled by the 'pages.mode.switch' parameter;
     * the selected mode is stored in the session and used up to the next switch
     */
    protected DisplayMode.Value getDisplayMode(BeanContext context) {
        SlingHttpServletRequest request = context.getRequest();
        HttpSession session = request.getSession(true);
        DisplayMode.Value modeSwitch = DisplayMode.Value.displayModeValue(
                request.getParameter(DISPLAY_MODE_SWITCH_PARAM), null);
        if (modeSwitch != null) {
            session.setAttribute(DISPLAY_MODE_ATTR, modeSwitch.name());
            return modeSwitch;
        } else {
            return DisplayMode.Value.displayModeValue(
                    session.getAttribute(DISPLAY_MODE_ATTR), DisplayMode.Value.EDIT);
        }
    }

    @Override
    protected String getConsolePath(BeanContext context) {
        return CONSOLE_PATH;
    }
}