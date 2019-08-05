/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.servlet;

import com.composum.pages.commons.AssetsConfiguration;
import com.composum.pages.commons.PagesConfiguration;
import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.service.ComponentManager;
import com.composum.pages.commons.service.PageManager;
import com.composum.pages.commons.service.ResourceManager;
import com.composum.pages.commons.service.SiteManager;
import com.composum.pages.commons.util.RequestUtil;
import com.composum.pages.commons.util.ResolverUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.servlet.ServletOperation;
import com.composum.sling.core.servlet.ServletOperationSet;
import com.composum.sling.core.util.ResponseUtil;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Pages Get Servlet",
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/cpm/pages/get",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET
        })
public class GetServlet extends ContentServlet {

    private static final Logger LOG = LoggerFactory.getLogger(GetServlet.class);

    public static final String PARAM_SUFFIX = "suffix";

    @Reference
    protected PagesConfiguration pagesConfiguration;

    @Reference
    protected AssetsConfiguration assetsConfiguration;

    @Reference
    protected ComponentManager componentManager;

    @Reference
    protected ResourceManager resourceManager;

    @Reference
    protected PageManager pageManager;

    @Reference
    protected SiteManager siteManager;

    protected BundleContext bundleContext;

    @Activate
    private void activate(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    // ContentServlet

    @Override
    protected boolean isEnabled() {
        return true;
    }

    @Override
    protected ResourceManager getResourceManager() {
        return resourceManager;
    }

    @Override
    protected PageManager getPageManager() {
        return pageManager;
    }

    @Override
    protected ResourceFilter getNodeFilter(SlingHttpServletRequest request) {
        return pagesConfiguration.getPageNodeFilter();
    }

    //
    // Servlet operations
    //

    public enum Extension {
        html, json
    }

    public enum Operation {
        include, pageData
    }

    protected PagesEditOperationSet operations = new PagesEditOperationSet();

    @Override
    protected ServletOperationSet getOperations() {
        return operations;
    }

    /**
     * setup of the servlet operation set for this servlet instance
     */
    @Override
    @SuppressWarnings("Duplicates")
    public void init() throws ServletException {
        super.init();

        // GET
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.pageData, new GetPageData());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.html,
                Operation.include, new Include());

    }

    public class PagesEditOperationSet extends ServletOperationSet<Extension, Operation> {

        public PagesEditOperationSet() {
            super(Extension.html);
        }
    }

    //
    // Page
    //

    /**
     * Retrieves the data JSON object of a Pages page.
     * #suffix the pages path, overridden by a 'url' parameter
     * #param url a page url to resolve the page resource (optional)
     */
    protected class GetPageData implements ServletOperation {

        @Override
        public void doIt(@Nonnull final SlingHttpServletRequest request,
                         @Nonnull final SlingHttpServletResponse response,
                         @Nonnull ResourceHandle resource)
                throws IOException {
            BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);

            String urlParam = request.getParameter(PARAM_URL);
            if (StringUtils.isNotBlank(urlParam)) {
                ResourceResolver resolver = context.getResolver();
                Resource urlResource = ResolverUtil.getUrlResource(resolver, urlParam);
                if (urlResource != null) {
                    resource = ResourceHandle.use(urlResource);
                }
            }

            Page page = null;
            Resource pageResource = pageManager.getContainingPageResource(resource);
            if (pageResource != null) {
                page = pageManager.createBean(context, pageResource);
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("GetPageData({},{})...", resource, page);
            }

            if (page != null && page.isValid()) {

                response.setStatus(HttpServletResponse.SC_OK);
                JsonWriter jsonWriter = ResponseUtil.getJsonWriter(response);
                writeJsonPage(context, jsonWriter, pagesConfiguration.getPageNodeFilter(), page);

            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }

    //
    //
    //

    /**
     * a include like rendering provided vor ajax requests
     * /bin/cpm/pages/get.include[.selectors].html/path/to/the/resource?resourceType=the/resource/type
     */
    public class Include implements ServletOperation {

        @Override
        public void doIt(final SlingHttpServletRequest request, final SlingHttpServletResponse response,
                         final ResourceHandle resource)
                throws ServletException, IOException {

            final String path = request.getRequestPathInfo().getSuffix();
            if (StringUtils.isNotBlank(path)) {
                final RequestDispatcherOptions options = new RequestDispatcherOptions();

                final String resourceType = RequestUtil.getParameter(request, PARAM_RESOURCE_TYPE,
                        RequestUtil.getParameter(request, PARAM_TYPE, ""));
                options.setForceResourceType(StringUtils.isNotBlank(resourceType) ? resourceType : null);

                final String selectors = RequestUtil.getSelectorString(request, null, 1);
                options.setReplaceSelectors(StringUtils.isNotBlank(selectors) ? selectors : null);

                final String suffix = RequestUtil.getParameter(request, PARAM_SUFFIX,
                        RequestUtil.getParameter(request, PARAM_TYPE, ""));
                options.setReplaceSuffix(StringUtils.isNotBlank(suffix) ? suffix : null);

                final RequestDispatcher dispatcher = request.getRequestDispatcher(resource, options);
                if (dispatcher != null) {
                    dispatcher.forward(request, response);
                    return;
                }
            }
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
