package com.composum.pages.commons.servlet;

import com.composum.sling.core.BeanContext;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import java.util.regex.Pattern;

import static com.composum.pages.commons.PagesConstants.PAGES_EDITOR_PATH;
import static com.composum.pages.commons.PagesConstants.PAGES_EDITOR_SELECTOR;

/**
 * The general hook (servlet) for the Pages edit stage; provides the path '/bin/edit.html/...'.
 */
@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Standalone Servlet",
                ServletResolverConstants.SLING_SERVLET_PATHS + "=" + PAGES_EDITOR_PATH,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET
        })
public class PagesStandaloneServlet extends PagesFrameServlet {

    public static final Pattern PATH_PATTERN = Pattern.compile("^(" + PAGES_EDITOR_PATH + "(\\.[^/]+)?\\.html)(/.*)?$");

    @Override
    protected String getServletPath(BeanContext context) {
        return PAGES_EDITOR_PATH;
    }

    @Override
    protected Pattern getPathPattern(BeanContext context) {
        return PATH_PATTERN;
    }

    @Override
    protected void prepareForward(BeanContext context, RequestDispatcherOptions options) {
        super.prepareForward(context, options);
        options.setReplaceSelectors(PAGES_EDITOR_SELECTOR);
    }

    @Override
    protected String getConsolePath(BeanContext context) {
        return null;
    }
}