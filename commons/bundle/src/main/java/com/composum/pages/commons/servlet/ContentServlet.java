package com.composum.pages.commons.servlet;

import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.service.PageManager;
import com.composum.pages.commons.service.ResourceManager;
import com.composum.pages.commons.util.PagesUtil;
import com.composum.pages.commons.util.RequestUtil;
import com.composum.pages.commons.util.ResourceTypeUtil;
import com.composum.pages.commons.util.ThemeUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.servlet.NodeTreeServlet;
import com.composum.sling.core.servlet.ServletOperation;
import com.composum.sling.core.servlet.Status;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static com.composum.pages.commons.PagesConstants.PROP_TEMPLATE;

public abstract class ContentServlet extends NodeTreeServlet {

    private static final Logger LOG = LoggerFactory.getLogger(ContentServlet.class);

    public static final String EDIT_RESOURCE_KEY = EditServlet.class.getName() + "_resource";
    public static final String EDIT_RESOURCE_TYPE_KEY = EditServlet.class.getName() + "_resourceType";

    protected BundleContext bundleContext;

    protected abstract ResourceManager getResourceManager();

    protected abstract PageManager getPageManager();

    protected abstract BeanContext newBeanContext(@Nonnull SlingHttpServletRequest request,
                                                  @Nonnull SlingHttpServletResponse response,
                                                  @Nullable Resource resource);

    //
    // edit component resources
    //

    public abstract class GetEditResource implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws ServletException, IOException {

            Resource contentResource = resource;
            if (Page.isPage(contentResource) || Site.isSite(contentResource)) {
                contentResource = contentResource.getChild("jcr:content");
                if (contentResource == null) {
                    contentResource = resource;
                }
            }

            String selectors = RequestUtil.getSelectorString(request, null, 1);
            if (StringUtils.isBlank(selectors)) {
                selectors = getDefaultSelectors();
            }
            String paramType = request.getParameter(PARAM_TYPE);
            Resource editResource = getEditResource(request, response, contentResource, selectors, paramType);

            if (LOG.isDebugEnabled()) {
                LOG.debug("GetEditResource({},{})...", contentResource.getPath(), editResource != null ? editResource.getPath() : "null");
            }

            if (editResource != null) {
                RequestDispatcherOptions options = new RequestDispatcherOptions();
                options.setForceResourceType(editResource.getPath());
                options.setReplaceSelectors(selectors);
                SlingHttpServletRequest forwardRequest = prepareForward(request, options);
                forward(forwardRequest, response, contentResource, paramType, options);

            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        }

        protected Resource getEditResource(@Nonnull SlingHttpServletRequest request,
                                           @Nonnull SlingHttpServletResponse response,
                                           @Nonnull Resource contentResource,
                                           @Nonnull String selectors, @Nullable String type) {
            ResourceResolver resolver = request.getResourceResolver();
            return ResourceTypeUtil.getSubtype(resolver, contentResource, type, getResourcePath(request), selectors);
        }

        protected Resource adjustToTheme(@Nonnull final BeanContext context,
                                         @Nonnull final Resource contentResource,
                                         @Nullable final Resource editResource) {
            if (editResource != null) {
                Page containigPage = getPageManager().getContainingPage(context, contentResource);
                return ThemeUtil.getTypeResource(containigPage, editResource);
            }
            return editResource;
        }


        protected String getSelectors(SlingHttpServletRequest request) {
            return RequestUtil.getSelectorString(request, null, 1);
        }

        protected abstract String getResourcePath(SlingHttpServletRequest request);

        protected String getDefaultSelectors() {
            return "";
        }

        protected SlingHttpServletRequest prepareForward(@Nonnull final SlingHttpServletRequest request,
                                                         @Nonnull final RequestDispatcherOptions options) {
            return request;
        }
    }

    //
    // abstract Content manipulation
    //

    protected abstract class ChangeContentOperation implements ServletOperation {

        protected Resource getRequestedSibling(@Nonnull SlingHttpServletRequest request, @Nonnull Resource target,
                                               @Nullable Resource skipThat) {
            ResourceResolver resolver = request.getResourceResolver();
            String beforePath = request.getParameter("before");
            if (StringUtils.isNotBlank(beforePath)) {
                return resolver.getResource(beforePath.startsWith("/")
                        ? beforePath : target.getPath() + "/" + beforePath);
            } else {
                return getResourceAt(target, RequestUtil
                        .getParameter(request, PARAM_INDEX, (Integer) null), skipThat);
            }
        }

        protected Resource getResourceAt(@Nonnull Resource parent, @Nullable Integer index,
                                         @Nullable Resource skipThat) {
            if (index != null && index >= 0) {
                String pathToSkip = skipThat != null ? skipThat.getPath() : null;
                Iterator<Resource> children = parent.listChildren();
                for (int i = 0; i < index && children.hasNext(); ) {
                    if (pathToSkip == null || !children.next().getPath().equals(pathToSkip)) {
                        i++;
                    }
                }
                if (children.hasNext()) {
                    return children.next();
                }
            }
            return null;
        }

        /**
         * Writes the JSON response
         *
         * @param result           the main result resource which was changed
         * @param updatedResources additional resources that have been updated (e.g. referrers) and need to be refreshed because of possibly changed stati etc.
         */
        protected void sendResponse(Status status, Resource result, @Nullable List<Resource> updatedResources)
                throws IOException {
            status.reference("reference", result);
            if (updatedResources != null) {
                status.list("updated", updatedResources);
            }
            status.sendJson();
        }

        /**
         * Writes the JSON response
         *
         * @param result the main result resource which was changed
         */
        protected void sendResponse(Status status, Resource result)
                throws IOException {
            sendResponse(status, result, null);
        }
    }

    //
    // general request forward to render an editing resource of a resource to edit
    //

    /**
     * forward to the edit component
     *
     * @param request  the current request
     * @param response the current response
     * @param resource the resource to edit (maybe synthetic)
     * @param typeHint the type of the resource to edit (maybe overlayed)
     * @param options  sling include options (selectors, suffix, resource type) for the edit component
     */
    protected static void forward(SlingHttpServletRequest request, SlingHttpServletResponse response,
                                  Resource resource, String typeHint, RequestDispatcherOptions options)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher(resource, options);
        if (dispatcher != null) {
            request.setAttribute(EDIT_RESOURCE_KEY, resource);
            request.setAttribute(EDIT_RESOURCE_TYPE_KEY,
                    StringUtils.isNotBlank(typeHint) ? typeHint : resource.getResourceType());
            dispatcher.forward(request, response);
            request.removeAttribute(EDIT_RESOURCE_TYPE_KEY);
            request.removeAttribute(EDIT_RESOURCE_KEY);
        }
    }

    //
    // JSON helpers
    //

    public void writeJsonPage(@Nonnull final BeanContext context, @Nonnull final JsonWriter writer,
                              @Nonnull final ResourceFilter filter, @Nonnull final Page page)
            throws IOException {
        writer.beginObject();
        if (page.isValid()) {
            TreeNodeStrategy nodeStrategy = new DefaultTreeNodeStrategy(filter);
            writeJsonNodeData(writer, nodeStrategy, ResourceHandle.use(page.getResource()), LabelType.name, false);
            writer.name("reference");
            PagesUtil.write(writer, PagesUtil.getReference(page.getResource(), null));
            Resource contentResource = page.getContent().getResource();
            writer.name("jcrContent");
            writeJsonNode(writer, nodeStrategy, ResourceHandle.use(contentResource), LabelType.name, false);
            writer.name("meta").beginObject();
            Site site = page.getSite();
            writer.name("site").value(site != null ? site.getPath() : null);
            writer.name("template").value(page.getTemplatePath());
            writer.name("isTemplate").value(getResourceManager().isTemplate(context, page.getResource()));
            writer.name("language").value(page.getLocale().getLanguage());
            writer.name("defaultLanguage").value(page.getPageLanguages().getDefaultLanguage().getKey());
            writer.endObject();
        }
        writer.endObject();
    }

    public void writeJsonResource(@Nonnull final BeanContext context, @Nonnull final JsonWriter writer,
                                  @Nonnull final TreeNodeStrategy nodeStrategy, Resource resource)
            throws IOException {
        ResourceHandle handle = ResourceHandle.use(resource);
        writer.beginObject();
        if (handle.isValid()) {
            writeJsonNodeData(writer, nodeStrategy, handle, LabelType.name, false);
            Resource contentResource = handle.getChild(JcrConstants.JCR_CONTENT);
            if (contentResource != null) {
                writer.name("jcrContent");
                writeJsonNode(writer, nodeStrategy, ResourceHandle.use(contentResource), LabelType.name, false);
            }
            writer.name("meta").beginObject();
            writer.name("template").value(handle.getProperty(PROP_TEMPLATE));
            writer.name("isTemplate").value(getResourceManager().isTemplate(context, handle));
            writer.endObject();
        }
        writer.endObject();
    }
}
