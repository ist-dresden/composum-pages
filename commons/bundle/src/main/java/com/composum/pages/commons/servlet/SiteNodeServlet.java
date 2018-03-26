package com.composum.pages.commons.servlet;

import com.composum.pages.commons.model.Homepage;
import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.request.DisplayMode;
import com.composum.pages.commons.service.SiteManager;
import com.composum.sling.core.BeanContext;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.annotation.Nonnull;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Site Node Servlet",
                ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES + "=cpp:Site",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET
        })
public class SiteNodeServlet extends SlingSafeMethodsServlet {

    protected BundleContext bundleContext;

    @Reference
    protected SiteManager siteManager;

    @Activate
    private void activate(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    protected void doGet(@Nonnull SlingHttpServletRequest request,
                         @Nonnull SlingHttpServletResponse response) throws ServletException,
            IOException {

        Resource resource = request.getResource();
        Resource content = resource.getChild(JcrConstants.JCR_CONTENT);

        if (content != null) {

            BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
            DisplayMode.Value mode = DisplayMode.current(context);

            if (mode == DisplayMode.Value.EDIT || mode == DisplayMode.Value.DEVELOP) {

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
