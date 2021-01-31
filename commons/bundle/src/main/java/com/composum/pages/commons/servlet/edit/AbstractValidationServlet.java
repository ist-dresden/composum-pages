package com.composum.pages.commons.servlet.edit;

import com.composum.sling.core.servlet.Status;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * the abstract base to implement a form validation servlet
 */
public abstract class AbstractValidationServlet extends SlingAllMethodsServlet {

    protected abstract void doValidate(@Nonnull final SlingHttpServletRequest request,
                                       @Nonnull final SlingHttpServletResponse response,
                                       @Nonnull final Status status,
                                       @Nonnull final PostServletProperties properties);

    @Override
    public void doGet(@Nonnull final SlingHttpServletRequest request,
                      @Nonnull final SlingHttpServletResponse response)
            throws IOException {
        Status status = new Status(request, response);
        doValidate(request, response, status, new PostServletProperties(request));
        status.sendJson(HttpServletResponse.SC_OK);
    }

    @Override
    public void doPost(@Nonnull final SlingHttpServletRequest request,
                       @Nonnull final SlingHttpServletResponse response)
            throws IOException {
        Status status = new Status(request, response);
        doValidate(request, response, status, new PostServletProperties(request));
        status.sendJson(HttpServletResponse.SC_OK);
    }
}
