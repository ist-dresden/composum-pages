package com.composum.pages.stage.servlet;

import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.Restricted;
import com.composum.sling.core.servlet.AbstractServiceServlet;
import com.composum.sling.core.servlet.ServletOperation;
import com.composum.sling.core.servlet.ServletOperationSet;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.composum.pages.stage.servlet.PagesStageServlet.SERVICE_KEY;

/**
 *
 */
@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Content View Servlet",
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/cpm/pages/stage",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET
        })
@Restricted(key = SERVICE_KEY)
public class PagesStageServlet extends AbstractServiceServlet {

    private static final Logger LOG = LoggerFactory.getLogger(PagesStageServlet.class);

    public static final String SERVICE_KEY = "pages/content/preview";

    enum Extension {
        html
    }

    enum Operation {
        preview
    }

    private ReleaseOperationSet operations = new ReleaseOperationSet();

    protected BundleContext bundleContext;

    @Activate
    private void activate(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Deprecated
    protected boolean isEnabled() {
        return true;
    }

    @Override
    protected ServletOperationSet getOperations() {
        return operations;
    }

    private class ReleaseOperationSet extends ServletOperationSet<Extension, Operation> {
        ReleaseOperationSet() {
            super(Extension.html);
        }
    }

    @Override
    public void init() throws ServletException {
        super.init();

        // GET
        operations.setOperation(ServletOperationSet.Method.GET, Extension.html, Operation.preview,
                new Preview());
    }

    public static final HashMap<String, String> PREVIEW_TYPES = new HashMap<String, String>() {{
        put("file", "composum/pages/stage/edit/default/file/preview");
        put("page", "");
        put("site", "composum/pages/stage/edit/default/site/preview");
    }};

    protected abstract class Forwarder implements ServletOperation {

        @Override
        public void doIt(@Nonnull final SlingHttpServletRequest request,
                         @Nonnull final SlingHttpServletResponse response,
                         @Nonnull final ResourceHandle resource)
                throws IOException {
            try {
                RequestPathInfo pathInfo = request.getRequestPathInfo();
                String[] selectors = pathInfo.getSelectors();
                String resourceType;
                if (selectors.length > 1 && (resourceType = getTypeMap().get(selectors[1])) != null) {
                    RequestDispatcherOptions options = new RequestDispatcherOptions();
                    if (StringUtils.isNotBlank(resourceType)) {
                        options.setForceResourceType(resourceType);
                    }
                    RequestDispatcher dispatcher = request.getRequestDispatcher(resource, options);
                    if (dispatcher != null) {
                        dispatcher.forward(request, response);
                        return;
                    }
                }
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }

        protected abstract Map<String, String> getTypeMap();
    }

    protected class Preview extends Forwarder {

        @Override
        protected Map<String, String> getTypeMap() {
            return PREVIEW_TYPES;
        }
    }
}
