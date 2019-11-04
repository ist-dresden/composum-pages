package com.composum.pages.stage.servlet;

import com.composum.pages.commons.util.RequestUtil;
import com.composum.pages.stage.model.tools.PropertiesComparatorModel;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.filter.StringFilter;
import com.composum.sling.core.servlet.AbstractServiceServlet;
import com.composum.sling.core.servlet.ServletOperation;
import com.composum.sling.core.servlet.ServletOperationSet;
import com.composum.sling.core.servlet.Status;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.ResourceResolver;
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
import java.util.Locale;

/**
 * compare content properties - traverses the hierarchy and lists properties side by side
 * <p>
 * /bin/cpm/pages/compare.{properties|text|i18n}.{html|json}{/left/path}?parameters
 * <p>
 * parameters:
 * <dl>
 * <dt>left, right</dt><dd>the left and/or the right content path (both optional)</dd>
 * <dt>leftLocale, rightLocale</dt><dd>the language key to use for comparation (both optional)</dd>
 * <dt>leftVersion, rightVersion</dt><dd>the version UUID to use for comparation (both optional)</dd>
 * <dt>property</dt><dd>the property to compare; use '*' for a full drilldown</dd>
 * <dt>equal</dt><dd>true/false; show equal properties (default: true)</dd>
 * <dt>highlight</dt><dd>true/false; highlight differences (default: false)</dd>
 * </dl>
 */
@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Content Comparator Servlet",
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/cpm/pages/compare",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET
        })
public class ContentComparatorServlet extends AbstractServiceServlet {

    private static final Logger LOG = LoggerFactory.getLogger(ContentComparatorServlet.class);

    enum Extension {
        html, json
    }

    enum Operation {
        properties,
        text,
        i18n
    }

    private ReleaseOperationSet operations = new ReleaseOperationSet();

    protected BundleContext bundleContext;

    @Activate
    private void activate(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
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
        operations.setOperation(ServletOperationSet.Method.GET, Extension.html, Operation.properties,
                new CompareProperties());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.html, Operation.text,
                new CompareText());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.html, Operation.i18n,
                new CompareI18n());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json, Operation.properties,
                new CompareProperties());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json, Operation.text,
                new CompareText());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json, Operation.i18n,
                new CompareI18n());
    }

    protected class CompareProperties implements ServletOperation {

        @Override
        public void doIt(@Nonnull final SlingHttpServletRequest request,
                         @Nonnull final SlingHttpServletResponse response,
                         @Nonnull final ResourceHandle resource)
                throws IOException {
            Status status = new Status(request, response);
            try {
                BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
                ResourceResolver resolver = context.getResolver();

                String left = RequestUtil.getParameter(request, "left", RequestUtil.getParameter(request, "path", resource.getPath()));
                String leftVersionUuid = RequestUtil.getParameter(request, "leftVersion", request.getParameter("version"));
                String leftLocale = RequestUtil.getParameter(request, "leftLocale", request.getParameter("locale"));
                String right = RequestUtil.getParameter(request, "right", RequestUtil.getParameter(request, "path", resource.getPath()));
                String rightVersionUuid = RequestUtil.getParameter(request, "rightVersion", request.getParameter("version"));
                String rightLocale = RequestUtil.getParameter(request, "rightLocale", request.getParameter("locale"));
                String property = request.getParameter("property");
                boolean skipEqualProperties = !RequestUtil.getParameter(request, "equal", Boolean.TRUE);
                boolean highlightDifferences = RequestUtil.getParameter(request, "highlight", Boolean.FALSE);

                if (StringUtils.isNotBlank(left) && StringUtils.isNotBlank(right)) {

                    PropertiesComparatorModel model = new PropertiesComparatorModel(context,
                            left, leftVersionUuid, StringUtils.isNotBlank(leftLocale) ? new Locale(leftLocale) : null,
                            right, rightVersionUuid, StringUtils.isNotBlank(rightLocale) ? new Locale(rightLocale) : null,
                            property, getPropertyFilter(), skipEqualProperties, highlightDifferences);

                    RequestPathInfo pathInfo = request.getRequestPathInfo();
                    String ext = pathInfo.getExtension();
                    if ("json".equals(ext)) {

                        response.setStatus(HttpServletResponse.SC_OK);
                        response.setContentType("application/json; charset=UTF-8");
                        JsonWriter writer = new JsonWriter(response.getWriter());
                        model.toJson(writer);
                        return;

                    } else {

                        request.setAttribute("model", model);
                        request.setAttribute("current", model.getRoot());
                        RequestDispatcherOptions options = new RequestDispatcherOptions();
                        options.setForceResourceType("composum/pages/stage/edit/tools/comparator");
                        options.setReplaceSelectors(StringUtils.join(
                                RequestUtil.getSelectors(request, StringFilter.ALL, 1, 0), "."));
                        RequestDispatcher dispatcher = request.getRequestDispatcher(resource, options);
                        if (dispatcher != null) {
                            dispatcher.forward(request, response);
                            return;

                        } else {
                            status.withLogging(LOG).error("can't forward to renderer");
                        }
                    }
                } else {
                    status.withLogging(LOG).error("nothing to compare: '{}' - '{}'", left, right);
                }
            } catch (Exception ex) {
                status.withLogging(LOG).error("error comparing content: {}", ex);
            }
            status.sendJson();
        }

        protected ResourceFilter getPropertyFilter() {
            return ResourceFilter.ALL;
        }
    }

    protected class CompareText extends CompareProperties {

        @Override
        protected ResourceFilter getPropertyFilter() {
            return com.composum.pages.commons.model.Component.TEXT_PROPERTIES;
        }
    }

    protected class CompareI18n extends CompareProperties {

        @Override
        protected ResourceFilter getPropertyFilter() {
            return com.composum.pages.commons.model.Component.I18N_PROPERTIES;
        }
    }
}
