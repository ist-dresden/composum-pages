package com.composum.pages.commons.servlet;

import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.request.DisplayMode;
import com.composum.pages.commons.service.PageManager;
import com.composum.sling.core.BeanContext;
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
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Page Node Servlet",
                ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES + "=cpp:Page",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET
        })
public class PageNodeServlet extends SlingSafeMethodsServlet {

    protected BundleContext bundleContext;

    @Reference
    protected PageManager pageManager;

    @Activate
    private void activate(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    protected void doGet(@Nonnull SlingHttpServletRequest request,
                         @Nonnull SlingHttpServletResponse response) throws ServletException,
            IOException {

        BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
        Resource resource = request.getResource();
        Page page = pageManager.createBean(context, resource);

        if (page.isValid()) {

            // perform HTTP redirect if a redirect target is set at the page
            DisplayMode.Value displayMode = DisplayMode.current(context);
            if (displayMode != DisplayMode.Value.EDIT && displayMode != DisplayMode.Value.DEVELOP) {
                if (page.redirect()) {
                    return;
                }
            }

            // determine the page content resource to use for the request forward
            Resource forwardContent = page.getForwardPage().getContent().getResource();

            RequestDispatcher dispatcher = request.getRequestDispatcher(forwardContent);
            if (dispatcher != null) {
                dispatcher.forward(request, response);
            }

        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
