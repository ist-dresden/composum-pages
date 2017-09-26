package com.composum.pages.commons.servlet;

import com.composum.pages.commons.model.Homepage;
import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.request.DisplayMode;
import com.composum.pages.commons.service.SiteManager;
import com.composum.sling.core.BeanContext;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.BundleContext;

import javax.annotation.Nonnull;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SlingServlet(
        resourceTypes = {"cpp:Site"},
        methods = {"GET"}
)
public class SiteServlet extends SlingSafeMethodsServlet {

    @Reference
    protected SiteManager siteManager;

    protected BundleContext bundleContext;

    @Activate
    private void activate(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    protected void doGet(@Nonnull SlingHttpServletRequest request,
                         @Nonnull SlingHttpServletResponse response) throws ServletException,
            IOException {

        Resource resource = request.getResource();
        Resource content = resource.getChild("jcr:content");

        if (content != null) {

            BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
            DisplayMode.Value mode = DisplayMode.current(context);

            if (mode == DisplayMode.Value.EDIT || mode == DisplayMode.Value.DEVELOP){

                RequestDispatcher dispatcher = request.getRequestDispatcher(content);
                if (dispatcher != null) {
                    dispatcher.forward(request, response);
                }

            } else {

                Site site = siteManager.createBean(context, resource);
                Homepage homepage = site.getHomepage();

                if (homepage != null && homepage.isValid()) {

                    response.sendRedirect(homepage.getUrl());
                }
            }

        } else {
            response.sendError(HttpServletResponse.SC_NO_CONTENT);
        }
    }
}
