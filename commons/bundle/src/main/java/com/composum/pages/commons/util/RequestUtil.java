package com.composum.pages.commons.util;

import com.composum.sling.core.filter.StringFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.wrappers.SlingHttpServletRequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.composum.pages.commons.servlet.PagesContentServlet.EDIT_RESOURCE_KEY;
import static com.composum.pages.commons.servlet.PagesContentServlet.EDIT_RESOURCE_TYPE_KEY;

public class RequestUtil extends com.composum.sling.core.util.RequestUtil {

    private static final Logger LOG = LoggerFactory.getLogger(RequestUtil.class);

    public static class GetWrapper extends SlingHttpServletRequestWrapper {

        public GetWrapper(SlingHttpServletRequest request) {
            super(request);
        }

        @Override
        public String getMethod() {
            return "GET";
        }
    }

    public static List<String> getSelectors(SlingHttpServletRequest request, StringFilter filter, int index, int max) {
        List<String> result = new ArrayList<>();
        String[] selectors = request.getRequestPathInfo().getSelectors();
        for (int i = index; i < selectors.length && (max < 1 || result.size() < max); i++) {
            if (filter == null || filter.accept(selectors[i])) {
                result.add(selectors[i]);
            }
        }
        return result;
    }

    public static String getSelectorString(SlingHttpServletRequest request, StringFilter filter, int index) {
        return getSelectorString(request, filter, index, 0);
    }

    public static String getSelectorString(SlingHttpServletRequest request, StringFilter filter, int index, int max) {
        return StringUtils.join(getSelectors(request, filter, index, max), '.');
    }

    /**
     * forward a request with type and content path cange
     *
     * @param isEditResource mark the forward as a forward to an edit resource to modify a content resource
     */
    public static void forward(@Nonnull final SlingHttpServletRequest request, @Nonnull final SlingHttpServletResponse response,
                               @Nonnull final String resourcePath, boolean isEditResource, @Nullable final String resourceType,
                               @Nullable final String selectors, @Nullable final String suffix)
            throws ServletException, IOException {

        if (LOG.isInfoEnabled()) {
            RequestPathInfo pathInfo = request.getRequestPathInfo();
            LOG.info(request.getRequestURI() + "." + pathInfo.getSelectorString() + "." + pathInfo.getExtension() + pathInfo.getSuffix() + "...");
        }
        ResourceResolver resolver = request.getResourceResolver();
        Resource resource = resolver.resolve(resourcePath);
        forward(request, response, resource, isEditResource, resourceType, selectors, suffix);
    }

    /**
     * forward a request with type and content path cange
     *
     * @param isEditResource mark the forward as a forward to an edit resource to modify a content resource
     */
    public static void forward(@Nonnull final SlingHttpServletRequest request, @Nonnull final SlingHttpServletResponse response,
                               @Nonnull final Resource resource, boolean isEditResource, @Nullable final String resourceType,
                               @Nullable final String selectors, @Nullable final String suffix)
            throws ServletException, IOException {
        if (LOG.isInfoEnabled()) {
            RequestPathInfo pathInfo = request.getRequestPathInfo();
            LOG.info(request.getRequestURI() + "." + pathInfo.getSelectorString() + "." + pathInfo.getExtension() + pathInfo.getSuffix() + "...");
        }
        RequestDispatcherOptions options = new RequestDispatcherOptions();
        if (StringUtils.isNotBlank(resourceType)) {
            options.setForceResourceType(resourceType);
        }
        if (StringUtils.isNotBlank(selectors)) {
            options.setReplaceSelectors(selectors);
        }
        if (StringUtils.isNotBlank(suffix)) {
            options.setReplaceSuffix(suffix);
        }
        if (LOG.isInfoEnabled()) {
            RequestPathInfo pathInfo = request.getRequestPathInfo();
            LOG.info(">>> " + resource.getPath() + "." + options.getReplaceSelectors() + "." + pathInfo.getExtension() + options.getReplaceSuffix() + "!");
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher(resource, options);
        if (dispatcher != null) {
            Resource savedEditResource = null;
            String saveEditResourceType = null;
            if (isEditResource) {
                savedEditResource = (Resource) request.getAttribute(EDIT_RESOURCE_KEY);
                saveEditResourceType = (String) request.getAttribute(EDIT_RESOURCE_TYPE_KEY);
                request.setAttribute(EDIT_RESOURCE_KEY, resource);
                if (StringUtils.isNotBlank(resourceType)) {
                    request.setAttribute(EDIT_RESOURCE_TYPE_KEY, resourceType);
                }
            }
            dispatcher.forward(request, response);
            if (isEditResource) {
                if (savedEditResource != null) {
                    request.setAttribute(EDIT_RESOURCE_KEY, savedEditResource);
                } else {
                    request.removeAttribute(EDIT_RESOURCE_KEY);
                }
                if (saveEditResourceType != null) {
                    request.setAttribute(EDIT_RESOURCE_TYPE_KEY, saveEditResourceType);
                } else {
                    request.removeAttribute(EDIT_RESOURCE_TYPE_KEY);
                }
            }
        }
    }
}
