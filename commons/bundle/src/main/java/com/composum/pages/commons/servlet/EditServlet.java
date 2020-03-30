/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.servlet;

import com.composum.pages.commons.AssetsConfiguration;
import com.composum.pages.commons.PagesConfiguration;
import com.composum.pages.commons.model.Container;
import com.composum.pages.commons.model.GenericModel;
import com.composum.pages.commons.model.Model;
import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.model.properties.DropZone;
import com.composum.pages.commons.service.ComponentManager;
import com.composum.pages.commons.service.EditService;
import com.composum.pages.commons.service.PageManager;
import com.composum.pages.commons.service.ResourceManager;
import com.composum.pages.commons.service.SiteManager;
import com.composum.pages.commons.util.RequestUtil;
import com.composum.pages.commons.util.ResolverUtil;
import com.composum.pages.commons.util.ResourceTypeUtil;
import com.composum.pages.commons.util.ThemeUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.servlet.NodeTreeServlet;
import com.composum.sling.core.servlet.ServletOperation;
import com.composum.sling.core.servlet.ServletOperationSet;
import com.composum.sling.core.servlet.Status;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.core.util.ResponseUtil;
import com.composum.sling.core.util.XSS;
import com.composum.sling.platform.staging.versions.PlatformVersionsService;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.PersistenceException;
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
import javax.annotation.Nullable;
import javax.jcr.RepositoryException;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.composum.pages.commons.util.ResourceTypeUtil.CONTEXT_ACTIONS_PATH;
import static com.composum.pages.commons.util.ResourceTypeUtil.CONTEXT_CONTAINER_PATH;
import static com.composum.pages.commons.util.ResourceTypeUtil.EDIT_DIALOG_PATH;
import static com.composum.pages.commons.util.ResourceTypeUtil.EDIT_TILE_PATH;
import static com.composum.pages.commons.util.ResourceTypeUtil.EDIT_TOOLBAR_PATH;
import static com.composum.pages.commons.util.ResourceTypeUtil.NEW_DIALOG_PATH;
import static com.composum.pages.commons.util.ResourceTypeUtil.TREE_ACTIONS_PATH;

@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Edit Servlet",
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/cpm/pages/edit",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_POST,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_PUT,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_DELETE
        })
public class EditServlet extends PagesContentServlet {

    private static final Logger LOG = LoggerFactory.getLogger(EditServlet.class);

    public static final String DEFAULT_FILTER = "page";

    public static final String PAGE_COMPONENTS_RES_TYPE = "composum/pages/stage/edit/tools/main/components";
    public static final String PAGE_COMPONENTS_SEL_TYPE = "composum/pages/stage/widget/element/type/select";
    public static final String PAGE_COMPONENT_TYPES = "composum-pages-page-component-types";
    public static final String PAGE_COMPONENT_TYPES_SCOPE = PAGE_COMPONENT_TYPES + "_scope";

    public static final String CONTEXT_TOOLS_RES_TYPE = "composum/pages/stage/edit/sidebar/context";

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

    @Reference
    protected EditService editService;

    @Reference
    protected PlatformVersionsService platformVersionsService;

    protected MoveElementOperation moveElementOperation;

    @Activate
    private void activate(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    protected PlatformVersionsService getPlatformVersionsService() {
        return platformVersionsService;
    }

    @Override
    protected PagesConfiguration getPagesConfiguration() {
        return pagesConfiguration;
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
    protected boolean isEnabled() {
        return true;
    }

    //
    // Servlet operations
    //

    public enum Extension {
        html, json
    }

    public enum Operation {
        pageData, isTemplate, isAllowedChild,
        siteTree, pageTree,
        resourceInfo, editDialog, newDialog,
        editTile, editToolbar, treeActions, editResource,
        pageComponents, elementTypes, targetContainers, isAllowedElement, filterDropZones, componentCategories,
        refreshElement, insertElement, moveElement, copyElement,
        createPage, deletePage, moveContent, renameContent, copyContent,
        createSite, deleteSite,
        contextTools, context
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
                Operation.pageTree, new PageTreeOperation());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.siteTree, new SiteTreeOperation());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.resourceInfo, new GetResourceInfo());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.pageData, new GetPageData());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.isTemplate, new CheckIsTemplate());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.isAllowedChild, new CheckIsAllowedChild());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.html,
                Operation.editDialog, new GetEditDialog());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.html,
                Operation.newDialog, new GetNewDialog());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.html,
                Operation.editTile, new GetEditTile());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.html,
                Operation.editToolbar, new GetEditToolbar());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.html,
                Operation.treeActions, new GetTreeActions());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.html,
                Operation.editResource, new GetGenericResource());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.html,
                Operation.context, new GetContextResource());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.html,
                Operation.contextTools, new GetContextTools());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.html,
                Operation.refreshElement, new RefreshElementOperation());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.isAllowedElement, new CheckIsAllowedElement());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.targetContainers, new GetTargetContainers());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.componentCategories, new GetComponentCategories());

        // POST
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.targetContainers, new GetTargetContainers());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.html,
                Operation.insertElement, new InsertElementOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.moveElement, moveElementOperation = new MoveElementOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.copyElement, new CopyElementOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.createPage, new CreatePage());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.deletePage, new DeletePage());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.moveContent, new MoveContentOrElement());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.renameContent, new RenameContentOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.copyContent, new CopyContentOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.createSite, new CreateSite());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.deleteSite, new DeleteSite());

        // PUT
        operations.setOperation(ServletOperationSet.Method.PUT, Extension.html,
                Operation.pageComponents, new GetPageComponents());
        operations.setOperation(ServletOperationSet.Method.PUT, Extension.html,
                Operation.elementTypes, new GetElementTypes());
        operations.setOperation(ServletOperationSet.Method.PUT, Extension.html,
                Operation.editResource, new GetGenericResource());
        operations.setOperation(ServletOperationSet.Method.PUT, Extension.json,
                Operation.filterDropZones, new FilterDropZones());

        // DELETE

    }

    public class PagesEditOperationSet extends ServletOperationSet<Extension, Operation> {

        public PagesEditOperationSet() {
            super(Extension.json);
        }
    }

    protected BeanContext newBeanContext(@Nonnull SlingHttpServletRequest request,
                                         @Nonnull SlingHttpServletResponse response,
                                         @Nullable Resource resource) {
        return new BeanContext.Servlet(getServletContext(), bundleContext, request, response, resource);
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

    protected class RefreshElementOperation implements ServletOperation {

        @Override
        public void doIt(@Nonnull final SlingHttpServletRequest request,
                         @Nonnull final SlingHttpServletResponse response,
                         @Nonnull ResourceHandle resource)
                throws IOException {
            try {
                BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
                RequestDispatcherOptions options = new RequestDispatcherOptions();
                ThemeUtil.applyTheme(getPageManager().getContainingPage(context, resource), resource, options);
                RequestDispatcher dispatcher = request.getRequestDispatcher(resource, options);
                if (dispatcher != null) {
                    dispatcher.forward(request, response);
                    return;
                } else {
                    LOG.error("can't get dispatcher for '{}'", resource.getPath());
                }
            } catch (ServletException ex) {
                LOG.error(ex.getMessage(), ex);
            }
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    protected class CheckIsTemplate implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {

            Model model = null;
            String template = null;
            boolean isTemplate = false;

            if (resource != null) {

                BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
                if (Page.isPage(resource)) {
                    Page page = pageManager.createBean(context, resource);
                    template = page.getTemplatePath();
                    isTemplate = page.isTemplate();
                    model = page;
                } else if (Site.isSite(resource)) {
                    Site site = siteManager.createBean(context, resource);
                    template = site.getTemplatePath();
                    isTemplate = site.isTemplate();
                    model = site;
                } else {
                    model = new GenericModel(context, resource);
                }
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("CheckIsTemplate({},{}): {} ({})", resource, model, isTemplate, template);
            }
            if (model != null) {

                response.setStatus(HttpServletResponse.SC_OK);
                JsonWriter jsonWriter = ResponseUtil.getJsonWriter(response);
                jsonWriter.beginObject();
                jsonWriter.name("name").value(resource.getName());
                jsonWriter.name("path").value(resource.getPath());
                jsonWriter.name("model").value(model.getClass().getSimpleName());
                jsonWriter.name("template").value(template);
                jsonWriter.name("isTemplate").value(isTemplate);
                jsonWriter.endObject();

            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }

    //
    // Tree
    //

    @Override
    protected ResourceFilter getNodeFilter(SlingHttpServletRequest request) {
        return pagesConfiguration.getPageNodeFilter();
    }

    public class SiteTreeOperation extends TreeOperation {

        @Override
        protected ResourceFilter getNodeFilter(SlingHttpServletRequest request) {
            return pagesConfiguration.getSiteNodeFilter();
        }
    }

    public class PageTreeOperation extends TreeOperation {

        /**
         * for a cpp:Page the jcr:content child should be the first in the tree
         */
        protected class PageContentIterable extends ArrayList<Resource> {

            public PageContentIterable(Resource pageResource) {
                for (Resource child : pageResource.getChildren()) {
                    if (JcrConstants.JCR_CONTENT.equals(child.getName())) {
                        add(0, child);
                    } else {
                        add(child);
                    }
                }
            }
        }

        /**
         * use the PageContentIterable in case of a page node resource
         */
        protected class TreeNodeStrategy extends DefaultTreeNodeStrategy {

            public TreeNodeStrategy(ResourceFilter filter) {
                super(filter);
            }

            @Override
            public Iterable<Resource> getChildren(Resource nodeResource) {
                return Page.isPage(nodeResource) ? new PageContentIterable(nodeResource) : nodeResource.getChildren();
            }
        }

        @Override
        protected NodeTreeServlet.TreeNodeStrategy getNodeStrategy(SlingHttpServletRequest request) {
            return new TreeNodeStrategy(getNodeFilter(request));
        }

        @Override
        protected ResourceFilter getNodeFilter(SlingHttpServletRequest request) {
            return pagesConfiguration.getRequestNodeFilter(request, PARAM_FILTER, DEFAULT_FILTER);
        }
    }

    //
    //
    //

    /**
     * response with a forward to GET the rendered resource referenced by the requests suffix
     */
    protected class GetGenericResource extends GetEditResource {

        @Override
        protected String getResourcePath(SlingHttpServletRequest request) {
            RequestPathInfo pathInfo = request.getRequestPathInfo();
            String suffix = XSS.filter(pathInfo.getSuffix());
            if (StringUtils.isNotBlank(suffix) && !"/".equals(suffix)) {
                Resource resource = request.getResourceResolver().getResource(suffix);
                if (resource != null) {
                    return resource.getPath();
                }
            }
            return null;
        }

        /**
         * in case of a PUT request the requests data are provided as request attribute
         * assuming that the data are a JSON array of resource reference objects (multi selection)
         */
        @Override
        protected SlingHttpServletRequest prepareForward(@Nonnull final SlingHttpServletRequest request,
                                                         @Nonnull final RequestDispatcherOptions options) {
            if (HttpConstants.METHOD_PUT.equals(request.getMethod())) {
                String attr = request.getParameter(PARAM_ATTR); // the request attribute name for the data
                if (StringUtils.isNotBlank(attr)) {
                    // prepare a list of references sent via PUT for the rendering of the edit resource
                    try (final JsonReader reader = new JsonReader(request.getReader())) {
                        final ResourceManager.ReferenceList references =
                                resourceManager.getReferenceList(request.getResourceResolver(), reader);
                        // the resource can access the references via request attribute...
                        request.setAttribute(attr, references);
                    } catch (IOException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }
                // forward request with 'GET'! for right component rendering
                return new RequestUtil.GetWrapper(request);
            }
            return request;
        }
    }

    protected class GetEditDialog extends GetEditResource {

        @Override
        protected String getResourcePath(SlingHttpServletRequest request) {
            return EDIT_DIALOG_PATH;
        }

        @Override
        protected Resource getEditResource(@Nonnull final SlingHttpServletRequest request,
                                           @Nonnull final SlingHttpServletResponse response,
                                           @Nonnull final Resource contentResource,
                                           @Nonnull final String selectors, @Nullable final String type) {
            return adjustToTheme(newBeanContext(request, response, contentResource), contentResource,
                    super.getEditResource(request, response, contentResource, selectors, type));
        }
    }

    protected class GetNewDialog extends GetEditDialog {

        @Override
        protected String getResourcePath(SlingHttpServletRequest request) {
            return NEW_DIALOG_PATH;
        }
    }

    protected class GetEditTile extends GetEditResource {

        @Override
        protected String getResourcePath(SlingHttpServletRequest request) {
            return EDIT_TILE_PATH;
        }
    }

    protected class GetEditToolbar extends GetEditResource {

        @Override
        protected String getResourcePath(SlingHttpServletRequest request) {
            return EDIT_TOOLBAR_PATH;
        }

        @Override
        protected Resource getEditResource(@Nonnull final SlingHttpServletRequest request,
                                           @Nonnull final SlingHttpServletResponse response,
                                           @Nonnull final Resource contentResource,
                                           @Nonnull final String selectors, @Nullable final String type) {
            return adjustToTheme(newBeanContext(request, response, contentResource), contentResource,
                    super.getEditResource(request, response, contentResource, selectors, type));
        }
    }

    protected class GetTreeActions extends GetEditResource {

        @Override
        protected String getResourcePath(SlingHttpServletRequest request) {
            return TREE_ACTIONS_PATH;
        }

        @Override
        protected Resource getEditResource(@Nonnull final SlingHttpServletRequest request,
                                           @Nonnull final SlingHttpServletResponse response,
                                           @Nonnull final Resource contentResource,
                                           @Nonnull final String selectors, @Nullable final String type) {
            if (assetsConfiguration.getAnyFileFilter().accept(contentResource)) {
                ResourceResolver resolver = request.getResourceResolver();
                return ResolverUtil.getResourceType(resolver, ResourceTypeUtil.DEFAULT_FILE_ACTIONS);
            } else {
                return super.getEditResource(request, response, contentResource, selectors, type);
            }
        }
    }

    protected class GetContextResource extends GetEditResource {

        @Override
        protected String getSelectors(SlingHttpServletRequest request) {
            return RequestUtil.getSelectorString(request, null, 2);
        }

        @SuppressWarnings("SwitchStatementWithTooFewBranches")
        @Override
        protected String getResourcePath(SlingHttpServletRequest request) {
            switch (RequestUtil.getSelectorString(request, null, 1, 1)) {
                case "container":
                    return CONTEXT_CONTAINER_PATH;
            }
            return CONTEXT_ACTIONS_PATH;
        }
    }

    //
    // Containers & Elements
    //

    /**
     * hierarchy check for the page element hierarchy (Container, Element)
     */
    protected class CheckIsAllowedElement implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {

            if (resource != null) {
                String path = request.getParameter(PARAM_PATH);

                if (StringUtils.isNotBlank(path)) {
                    ResourceResolver resolver = request.getResourceResolver();
                    String type = request.getParameter(PARAM_TYPE);

                    boolean allowed = editService.isAllowedElement(resolver,
                            resourceManager.getReference(resource, null),
                            resourceManager.getReference(resolver, path, type));

                    response.setStatus(HttpServletResponse.SC_OK);
                    JsonWriter jsonWriter = ResponseUtil.getJsonWriter(response);
                    jsonWriter.beginObject();
                    jsonWriter.name("isAllowed").value(allowed);
                    jsonWriter.endObject();

                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "invalid path");
                }
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }

    /**
     * get a list of allowed components (element type) for one page
     * (depends on the current content and the hierarchical rules of the pages elements)
     * see: com.composum.pages.stage.model.edit.page.Components
     */
    protected class GetPageComponents implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws ServletException, IOException {

            final BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);

            try (final JsonReader reader = new JsonReader(request.getReader())) {

                final ResourceManager.ReferenceList containerRefs =
                        resourceManager.getReferenceList(context.getResolver(), reader);
                String selectors = RequestUtil.getSelectorString(request, null, 1);

                if (LOG.isDebugEnabled()) {
                    LOG.debug("GetPageComponents({},{}, {})...", resource, selectors, containerRefs);
                }

                // forward request with 'GET'! for right component rendering
                request = new RequestUtil.GetWrapper(request);

                List<String> allowedElements =
                        editService.getAllowedElementTypes(context.getResolver(),
                                new ComponentManager.ComponentScope(
                                        RequestUtil.getParameter(request, PARAM_FILTER,
                                                ""), request.getParameter(PARAM_QUERY)),
                                containerRefs, true);

                if (allowedElements.size() < 1 && isFallbackAllowed()) {

                    // mark the request: 'scope not useful' for a response status '200'
                    request.setAttribute(PAGE_COMPONENT_TYPES_SCOPE, Boolean.FALSE);
                    allowedElements = editService.getAllowedElementTypes(context.getResolver(),
                            new ComponentManager.ComponentScope("", request.getParameter(PARAM_QUERY)),
                            containerRefs, true);
                    if (allowedElements.size() < 1) {
                        allowedElements = editService.getAllowedElementTypes(context.getResolver(),
                                null, containerRefs, true);
                    }

                } else {
                    // mark the request: 'scope useful' for a response status '202' (ACCEPTED)
                    request.setAttribute(PAGE_COMPONENT_TYPES_SCOPE, Boolean.TRUE);
                }

                // store the result in the request for the rendering template...
                request.setAttribute(PAGE_COMPONENT_TYPES, allowedElements);

                final RequestDispatcherOptions options = new RequestDispatcherOptions();
                options.setForceResourceType(getRenderResourceType());
                if (StringUtils.isBlank(selectors)) {
                    selectors = getDefaultSelectors();
                }
                options.setReplaceSelectors(selectors);

                forward(request, response, resource, null, options);
            }
        }

        protected boolean isFallbackAllowed() {
            return false;
        }

        protected String getRenderResourceType() {
            return PAGE_COMPONENTS_RES_TYPE;
        }

        protected String getDefaultSelectors() {
            return "content";
        }
    }

    /**
     * the derived operation for the element type select wirget
     */
    protected class GetElementTypes extends GetPageComponents {

        @Override
        protected boolean isFallbackAllowed() {
            return true;
        }

        @Override
        protected String getRenderResourceType() {
            return PAGE_COMPONENTS_SEL_TYPE;
        }
    }

    /**
     * get a list of allowed containers on a page to insert a given element or component (type)
     */
    protected class GetTargetContainers implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {
            final BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);

            ResourceManager.ReferenceList targetList =
                    resourceManager.getReferenceList(context.getResolver(), request.getParameter("targetList"));

            if (LOG.isDebugEnabled()) {
                LOG.debug("GetTargetContainers({}, {})...", resource, targetList);
            }

            String type = request.getParameter(PARAM_TYPE);
            ResourceManager.ResourceReference element = resourceManager.getReference(resource, type);
            targetList = editService.filterTargetContainers(context.getResolver(), targetList, element);

            if (LOG.isDebugEnabled()) {
                LOG.debug("GetTargetContainers({}): {}", resource, targetList);
            }

            JsonWriter jsonWriter = ResponseUtil.getJsonWriter(response);
            response.setStatus(HttpServletResponse.SC_OK);
            targetList.toJson(jsonWriter);
        }
    }

    /**
     * filter a list of drop zones and return the list of drop zones matching to the requested resource
     */
    protected class FilterDropZones implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {
            final BeanContext context = new BeanContext.Servlet(
                    getServletContext(), bundleContext, request, response, resource);

            try (final JsonReader reader = new JsonReader(request.getReader())) {

                DropZone.List dropZoneList = new DropZone.List(context, reader);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("FilterDropZones({},{})...", resource, dropZoneList);
                }

                dropZoneList = dropZoneList.getMatchingList(resource);

                JsonWriter jsonWriter = ResponseUtil.getJsonWriter(response);
                response.setStatus(HttpServletResponse.SC_OK);
                dropZoneList.toJson(jsonWriter);
            }
        }
    }

    /**
     * collects a list of all component categories
     */
    protected class GetComponentCategories implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {

            try (final JsonReader reader = new JsonReader(request.getReader())) {

                Collection<String> categories = componentManager.getComponentCategories(request.getResourceResolver());

                JsonWriter jsonWriter = ResponseUtil.getJsonWriter(response);
                response.setStatus(HttpServletResponse.SC_OK);
                jsonWriter.beginArray();
                for (String key : categories) {
                    jsonWriter.value(key);
                }
                jsonWriter.endArray();
            }
        }
    }

    //
    // element changes
    //

    protected abstract class ElementResourceOperation extends ChangeContentOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {
            Status status = new Status(request, response);

            String targetPath = request.getParameter("targetPath");
            String targetType = request.getParameter("targetType");
            String beforePath = request.getParameter("before");

            final BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
            final ResourceResolver resolver = context.getResolver();
            ResourceManager.ResourceReference target = resourceManager.getReference(resolver, targetPath, targetType);
            ResourceManager.ResourceReference object = getReference(request, resource, targetPath);

            if (LOG.isDebugEnabled()) {
                LOG.debug("{}Element({}@{} > {} < {})...", getOperationName(), object.getType(), object.getPath(), targetPath, beforePath);
            }

            if (editService.isAllowedElement(resolver, target, object)) {
                Resource before = StringUtils.isNotBlank(beforePath) ? resolver.getResource(beforePath) : null;

                try {
                    List<Resource> updatedReferrers = new ArrayList<>();
                    Resource result = doIt(resolver, object, target, before, updatedReferrers);
                    resolver.commit();

                    status.reference("reference", result);
                    status.list("updated", updatedReferrers);

                } catch (RepositoryException ex) {
                    LOG.error(ex.getMessage(), ex);
                    status.error(ex.getLocalizedMessage());
                }
            } else {
                LOG.info("{} not allowed: {}@{}", getOperationName(), object.getType(), targetPath);
                sendNotAllowedChild(request, response, target.getResource(), object.getResource());
                return;
            }
            status.sendJson();
        }

        protected abstract ResourceManager.ResourceReference getReference(SlingHttpServletRequest request,
                                                                          ResourceHandle resource, String targetPath);

        protected abstract Resource doIt(ResourceResolver resolver, ResourceManager.ResourceReference source,
                                         ResourceManager.ResourceReference target, Resource before, List<Resource> updatedReferrers)
                throws PersistenceException, RepositoryException;

        protected abstract String getOperationName();
    }

    protected class InsertElementOperation extends ElementResourceOperation {

        @Override
        protected ResourceManager.ResourceReference getReference(SlingHttpServletRequest request,
                                                                 ResourceHandle resource, String targetPath) {
            return resourceManager.getReference(request.getResourceResolver(),
                    targetPath + "/_for_check_only_", request.getParameter("resourceType"));
        }

        @Override
        public Resource doIt(ResourceResolver resolver, ResourceManager.ResourceReference source,
                             ResourceManager.ResourceReference target, Resource before, List<Resource> updatedReferrers)
                throws PersistenceException, RepositoryException {
            return editService.insertElement(resolver, source.getType(), target, before);
        }

        @Override
        protected String getOperationName() {
            return "insert";
        }
    }

    protected class MoveElementOperation extends ElementResourceOperation {

        @Override
        protected ResourceManager.ResourceReference getReference(SlingHttpServletRequest request,
                                                                 ResourceHandle resource, String targetPath) {
            return resourceManager.getReference(resource, null);
        }

        @Override
        public Resource doIt(ResourceResolver resolver, ResourceManager.ResourceReference source,
                             ResourceManager.ResourceReference target, Resource before, List<Resource> updatedReferrers)
                throws PersistenceException, RepositoryException {
            return editService.moveElement(resolver, resolver.getResource("/content"),
                    source.getResource(), target, before, updatedReferrers);
        }

        @Override
        protected String getOperationName() {
            return "move";
        }
    }

    protected class CopyElementOperation extends ElementResourceOperation {

        @Override
        protected ResourceManager.ResourceReference getReference(SlingHttpServletRequest request,
                                                                 ResourceHandle resource, String targetPath) {
            return resourceManager.getReference(resource, null);
        }

        @Override
        public Resource doIt(ResourceResolver resolver, ResourceManager.ResourceReference source,
                             ResourceManager.ResourceReference target, Resource before, List<Resource> updatedReferrers)
                throws PersistenceException, RepositoryException {
            return editService.copyElement(resolver, source.getResource(), target, before);
        }

        @Override
        protected String getOperationName() {
            return "copy";
        }
    }

    //
    // Page Management
    //

    protected class CreatePage implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {

            final BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
            try {
                Page page;
                String name = request.getParameter("name");
                String title = request.getParameter(ResourceUtil.JCR_TITLE);
                String description = request.getParameter(ResourceUtil.JCR_DESCRIPTION);

                Resource template = null;
                String templatePath;
                if (StringUtils.isNotBlank(templatePath = request.getParameter("template"))) {
                    ResourceResolver resolver = context.getResolver();
                    template = ResolverUtil.getTemplate(resolver, templatePath);
                }

                if (template != null) {
                    page = pageManager.createPage(context, resource, template, name, title, description, true);

                } else {
                    String resourceType = request.getParameter("resourceType");
                    if (StringUtils.isNotBlank(resourceType)) {
                        page = pageManager.createPage(context, resource, resourceType, name, title, description, true);

                    } else {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                                "no valid template / type: '" + templatePath + "/" + resourceType + "'");
                        return;
                    }
                }

                JsonWriter jsonWriter = ResponseUtil.getJsonWriter(response);
                response.setStatus(HttpServletResponse.SC_OK);
                jsonWriter.beginObject();
                jsonWriter.name("name").value(page.getName());
                jsonWriter.name("path").value(page.getPath());
                jsonWriter.name("url").value(page.getUrl());
                jsonWriter.name("editUrl").value(page.getEditUrl());
                jsonWriter.endObject();

            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
            }
        }
    }

    protected class DeletePage implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {

            try {
                if (Page.isPageContent(resource)) {
                    resource = resource.getParent();
                }
                if (Page.isPage(resource)) {

                    BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
                    pageManager.deletePage(context, resource, true);

                    JsonWriter jsonWriter = ResponseUtil.getJsonWriter(response);
                    response.setStatus(HttpServletResponse.SC_OK);
                    jsonWriter.beginObject();
                    jsonWriter.name("name").value(resource.getName());
                    jsonWriter.name("path").value(resource.getPath());
                    jsonWriter.endObject();

                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "resource is not a site");
                }

            } catch (PersistenceException ex) {
                LOG.error(ex.getMessage(), ex);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
            }
        }
    }

    protected class MoveContentOrElement extends MoveContentOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {

            String targetPath = request.getParameter("targetPath");
            String name = request.getParameter(PARAM_NAME);

            ResourceResolver resolver = request.getResourceResolver();
            Resource target = resolver.getResource(targetPath);
            if (target != null) {

                if (Container.isContainer(resolver, target, null)) {
                    moveElementOperation.doIt(request, response, resource);
                } else {
                    moveIt(request, response, resolver, resource, target, name);
                }

            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "target doesn't exist: '" + targetPath + "'");
            }
        }
    }

    //
    // Site Management
    //

    protected class CreateSite implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {

            BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
            try {
                Resource template = null;
                String value;
                if (StringUtils.isNotBlank(value = request.getParameter("template"))) {
                    ResourceResolver resolver = context.getResolver();
                    template = resolver.getResource(value);
                }
                String tenant = request.getParameter("tenant");
                String name = request.getParameter("name");
                String title = request.getParameter(ResourceUtil.JCR_TITLE);
                String description = request.getParameter(ResourceUtil.JCR_DESCRIPTION);
                Site site = siteManager.createSite(context, tenant, name, title, description, template, true);

                JsonWriter jsonWriter = ResponseUtil.getJsonWriter(response);
                response.setStatus(HttpServletResponse.SC_OK);
                jsonWriter.beginObject();
                jsonWriter.name("name").value(site.getName());
                jsonWriter.name("path").value(site.getPath());
                jsonWriter.name("url").value(site.getUrl());
                jsonWriter.name("editUrl").value(site.getEditUrl());
                jsonWriter.endObject();

            } catch (RepositoryException | PersistenceException ex) {
                LOG.error(ex.getMessage(), ex);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
            }
        }
    }

    protected class DeleteSite implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {

            try {
                if (Site.isSiteConfiguration(resource)) {
                    resource = resource.getParent();
                }
                if (Site.isSite(resource)) {

                    BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
                    siteManager.deleteSite(context, resource, true);

                    JsonWriter jsonWriter = ResponseUtil.getJsonWriter(response);
                    response.setStatus(HttpServletResponse.SC_OK);
                    jsonWriter.beginObject();
                    jsonWriter.name("name").value(resource.getName());
                    jsonWriter.name("path").value(resource.getPath());
                    jsonWriter.endObject();

                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "resource is not a site");
                }

            } catch (PersistenceException ex) {
                LOG.error(ex.getMessage(), ex);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
            }
        }
    }

    //
    // Context Tools
    //

    protected class GetContextTools implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws ServletException, IOException {

            String selectors = RequestUtil.getSelectorString(request, null, 1);
            if (LOG.isDebugEnabled()) {
                LOG.debug("GetContextTools({},{})...", resource, selectors);
            }

            String paramType = request.getParameter(PARAM_TYPE);

            RequestDispatcherOptions options = new RequestDispatcherOptions();
            options.setForceResourceType(CONTEXT_TOOLS_RES_TYPE);
            options.setReplaceSelectors(selectors);

            forward(request, response, resource, paramType, options);
        }
    }
}
