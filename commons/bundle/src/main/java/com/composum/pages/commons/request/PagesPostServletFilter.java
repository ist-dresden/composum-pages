package com.composum.pages.commons.request;

import com.composum.pages.commons.servlet.EditServlet;
import com.composum.sling.core.service.ServiceRestrictions;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.apache.sling.api.servlets.HttpConstants.METHOD_POST;

/**
 * Service restrictions support for POST request in a page context (Sling POST Servlet requests).
 */
@Component(
        service = {Filter.class},
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Post Servlet Filter",
                "sling.filter.scope=REQUEST",
                "service.ranking:Integer=" + 6500
        },
        immediate = true
)
public class PagesPostServletFilter implements Filter {

    protected static final ServiceRestrictions.Key SERVICE_KEY = new ServiceRestrictions.Key(EditServlet.SERVICE_KEY);

    @Reference(cardinality = ReferenceCardinality.OPTIONAL)
    private ServiceRestrictions restrictions;

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
                         final FilterChain chain)
            throws IOException, ServletException {
        if (restrictions != null && servletRequest instanceof SlingHttpServletRequest) {
            final SlingHttpServletRequest request = (SlingHttpServletRequest) servletRequest;
            if (METHOD_POST.equals(request.getMethod())) {
                final String path = request.getResource().getPath();
                if (path.startsWith("/content/")
                        && !restrictions.isPermissible(request, SERVICE_KEY, ServiceRestrictions.Permission.write)) {
                    final SlingHttpServletResponse response = (SlingHttpServletResponse) servletResponse;
                    response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                    return;
                }
            }
        }
        chain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
