package com.composum.pages.commons.servlet;

import com.composum.pages.commons.request.DisplayMode;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.LinkUtil;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import javax.annotation.Nonnull;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.composum.pages.commons.PagesConstants.PROP_SLING_TARGET;

@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Page Node Servlet",
                ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES + "=cpp:Page",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET
        })
public class PageNodeServlet extends SlingSafeMethodsServlet {

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
        Resource content = resource.getChild(JcrConstants.JCR_CONTENT);

        if (content != null && !ResourceUtil.isNonExistingResource(content)) {

            ValueMap contentValues = content.adaptTo(ValueMap.class);
            String redirectTarget = contentValues.get(PROP_SLING_TARGET, "");
            if (StringUtils.isNotBlank(redirectTarget)) {
                BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
                DisplayMode.Value displayMode = DisplayMode.current(context);
                if (displayMode != DisplayMode.Value.EDIT && displayMode != DisplayMode.Value.DEVELOP) {
                    String targetUrl = LinkUtil.getUrl(request, redirectTarget);
                    response.sendRedirect(targetUrl);
                    return;
                }
            }

            RequestDispatcher dispatcher = request.getRequestDispatcher(content);
            if (dispatcher != null) {
                dispatcher.forward(request, response);
            }

        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
