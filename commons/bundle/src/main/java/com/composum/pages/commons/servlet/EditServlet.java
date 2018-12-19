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
import com.composum.pages.commons.service.VersionsService;
import com.composum.pages.commons.util.RequestUtil;
import com.composum.pages.commons.util.ResolverUtil;
import com.composum.pages.commons.util.ResourceTypeUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.servlet.NodeTreeServlet;
import com.composum.sling.core.servlet.ServletOperation;
import com.composum.sling.core.servlet.ServletOperationSet;
import com.composum.sling.core.util.ResponseUtil;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestDispatcherOptions;
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
                Constants.SERVICE_DESCRIPTION + "=Pages Edit Servlet",
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
    public static final String PAGE_COMPONENT_TYPES = "composum-pages-page-component-types";

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
    protected VersionsService versionsService;

    protected MoveElementOperation moveElementOperation;

    @Activate
    private void activate(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    protected PagesConfiguration getPagesConfiguration() {
        return pagesConfiguration;
    }

    protected VersionsService getVersionsService() {
        return versionsService;
    }

    protected ResourceManager getResourceManager() {
        return resourceManager;
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
        siteTree, pageTree, developTree,
        resourceInfo, editDialog, newDialog,
        editTile, editToolbar, treeActions,
        pageComponents, targetContainers, isAllowedElement, filterDropZones, componentCategories,
        insertElement, moveElement, copyElement,
        createPage, deletePage, moveContent, renameContent, copyContent,
        createSite, deleteSite,
        contextTools, context,
        versions, restoreVersion, setVersionLabel, checkpoint
    }

    protected PagesEditOperationSet operations = new PagesEditOperationSet();

    @Override
    protected ServletOperationSet getOperations() {
        return operations;
    }

    /** setup of the servlet operation set for this servlet instance */
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
                Operation.developTree, new DevTreeOperation());
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
                Operation.context, new GetContextResource());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.html,
                Operation.contextTools, new GetContextTools());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.isAllowedElement, new CheckIsAllowedElement());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.targetContainers, new GetTargetContainers());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.componentCategories, new GetComponentCategories());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.html,
                Operation.versions, new GetVersions());

        // POST
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.checkpoint, new CheckpointOperation());
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
        operations.setOperation(ServletOperationSet.Method.PUT, Extension.json,
                Operation.filterDropZones, new FilterDropZones());
        operations.setOperation(ServletOperationSet.Method.PUT, Extension.json,
                Operation.restoreVersion, new RestoreVersion());
        operations.setOperation(ServletOperationSet.Method.PUT, Extension.json,
                Operation.setVersionLabel, new SetVersionLabel());

        // DELETE

    }

    public class PagesEditOperationSet extends ServletOperationSet<Extension, Operation> {

        public PagesEditOperationSet() {
            super(Extension.json);
        }
    }

    //
    // Page
    //

    protected class GetPageData implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {

            Page page = null;

            Resource pageResource = pageManager.getContainingPageResource(resource);
            if (pageResource != null) {
                BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
                page = pageManager.createBean(context, pageResource);
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("GetPageData(" + resource + "," + page + ")...");
            }

            if (page != null && page.isValid()) {

                response.setStatus(HttpServletResponse.SC_OK);
                JsonWriter jsonWriter = ResponseUtil.getJsonWriter(response);
                writeJsonPage(jsonWriter, pagesConfiguration.getPageNodeFilter(), page);

            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
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
                LOG.debug("CheckIsTemplate(" + resource + "," + model + "): " + isTemplate + " (" + template + ")");
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

    public class DevTreeOperation extends TreeOperation {

        @Override
        protected ResourceFilter getNodeFilter(SlingHttpServletRequest request) {
            return pagesConfiguration.getDevelopmentTreeFilter();
        }
    }

    //
    //
    //

    protected abstract class GetEditResource implements ServletOperation {

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
            Resource editResource = getEditResource(request, contentResource, selectors, paramType);

            if (LOG.isDebugEnabled()) {
                LOG.debug("GetEditResource(" + contentResource.getPath() + "," + editResource.getPath() + ")...");
            }

            if (editResource != null) {
                RequestDispatcherOptions options = new RequestDispatcherOptions();
                options.setForceResourceType(editResource.getPath());
                options.setReplaceSelectors(selectors);
                forward(request, response, contentResource, paramType, options);

            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        }

        protected Resource getEditResource(@Nonnull SlingHttpServletRequest request, @Nonnull Resource contentResource,
                                           @Nonnull String selectors, @Nullable String type) {
            ResourceResolver resolver = request.getResourceResolver();
            return ResourceTypeUtil.getSubtype(resolver, contentResource, type, getResourcePath(request), selectors);
        }

        protected String getSelectors(SlingHttpServletRequest request) {
            return RequestUtil.getSelectorString(request, null, 1);
        }

        protected abstract String getResourcePath(SlingHttpServletRequest request);

        protected String getDefaultSelectors() {
            return "";
        }
    }

    protected class GetEditDialog extends GetEditResource {

        @Override
        protected String getResourcePath(SlingHttpServletRequest request) {
            return EDIT_DIALOG_PATH;
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

        @Override
        protected Resource getEditResource(@Nonnull SlingHttpServletRequest request, @Nonnull Resource contentResource,
                                           @Nonnull String selectors, @Nullable String type) {
            if (assetsConfiguration.getAnyFileFilter().accept(contentResource)) {
                ResourceResolver resolver = request.getResourceResolver();
                return ResolverUtil.getResourceType(resolver, ResourceTypeUtil.DEFAULT_FILE_TILE);
            } else {
                return super.getEditResource(request, contentResource, selectors, type);
            }
        }
    }

    protected class GetEditToolbar extends GetEditResource {

        @Override
        protected String getResourcePath(SlingHttpServletRequest request) {
            return EDIT_TOOLBAR_PATH;
        }
    }

    protected class GetTreeActions extends GetEditResource {

        @Override
        protected String getResourcePath(SlingHttpServletRequest request) {
            return TREE_ACTIONS_PATH;
        }

        @Override
        protected Resource getEditResource(@Nonnull SlingHttpServletRequest request, @Nonnull Resource contentResource,
                                           @Nonnull String selectors, @Nullable String type) {
            if (assetsConfiguration.getAnyFileFilter().accept(contentResource)) {
                ResourceResolver resolver = request.getResourceResolver();
                return ResolverUtil.getResourceType(resolver, ResourceTypeUtil.DEFAULT_FILE_ACTIONS);
            } else {
                return super.getEditResource(request, contentResource, selectors, type);
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
                    LOG.debug("GetPageComponents(" + resource + "," + selectors + ", " + containerRefs + ")...");
                }

                // forward request with 'GET'! for right component rendering
                request = new RequestUtil.GetWrapper(request);

                final List<String> allowedElements =
                        editService.getAllowedElementTypes(context.getResolver(),
                                new ComponentManager.ComponentScope(
                                        RequestUtil.getParameter(request, PARAM_FILTER,
                                                ""), request.getParameter(PARAM_QUERY)),
                                containerRefs, true);
                request.setAttribute(PAGE_COMPONENT_TYPES, allowedElements);

                final RequestDispatcherOptions options = new RequestDispatcherOptions();
                options.setForceResourceType(PAGE_COMPONENTS_RES_TYPE);
                if (StringUtils.isBlank(selectors)) {
                    selectors = getDefaultSelectors();
                }
                options.setReplaceSelectors(selectors);

                forward(request, response, resource, null, options);
            }
        }

        protected String getDefaultSelectors() {
            return "content";
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
                LOG.debug("GetTargetContainers(" + resource + ", " + targetList + ")...");
            }

            String type = request.getParameter(PARAM_TYPE);
            ResourceManager.ResourceReference element = resourceManager.getReference(resource, type);
            targetList = editService.filterTargetContainers(context.getResolver(), targetList, element);

            if (LOG.isDebugEnabled()) {
                LOG.debug("GetTargetContainers(" + resource + "): " + targetList);
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
                    LOG.debug("FilterDropZones(" + resource + "," + dropZoneList + ")...");
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

            String targetPath = request.getParameter("targetPath");
            String targetType = request.getParameter("targetType");
            String beforePath = request.getParameter("before");

            final BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
            final ResourceResolver resolver = context.getResolver();
            ResourceManager.ResourceReference target = resourceManager.getReference(resolver, targetPath, targetType);
            ResourceManager.ResourceReference object = getReference(request, resource, targetPath);

            if (LOG.isDebugEnabled()) {
                LOG.debug(getOperationName() + "Element(" + object.getType() + "@" + object.getPath()
                        + " > " + targetPath + " < " + beforePath + ")...");
            }

            if (editService.isAllowedElement(resolver, target, object)) {
                Resource before = StringUtils.isNotBlank(beforePath) ? resolver.getResource(beforePath) : null;

                try {
                    Resource result = doIt(resolver, object, target, before);
                    resolver.commit();

                    sendResponse(response, result);

                } catch (RepositoryException ex) {
                    LOG.error(ex.getMessage(), ex);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
                }
            } else {
                LOG.info(getOperationName() + " not allowed: " + object.getType() + "@" + targetPath);
                sendNotAllowedChild(request, response, target.getResource(), object.getResource());
            }
        }

        protected abstract ResourceManager.ResourceReference getReference(SlingHttpServletRequest request,
                                                                          ResourceHandle resource, String targetPath);

        protected abstract Resource doIt(ResourceResolver resolver, ResourceManager.ResourceReference source,
                                         ResourceManager.ResourceReference target, Resource before)
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
                             ResourceManager.ResourceReference target, Resource before)
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
                             ResourceManager.ResourceReference target, Resource before)
                throws PersistenceException, RepositoryException {
            return editService.moveElement(resolver, resolver.getResource("/content"),
                    source.getResource(), target, before);
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
                             ResourceManager.ResourceReference target, Resource before)
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
                String title = request.getParameter("title");
                String description = request.getParameter("description");

                Resource template = null;
                String templatePath;
                if (StringUtils.isNotBlank(templatePath = request.getParameter("template"))) {
                    ResourceResolver resolver = context.getResolver();
                    template = resolver.getResource(templatePath);
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
                String title = request.getParameter("title");
                String description = request.getParameter("description");
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
                LOG.debug("GetContextTools(" + resource + "," + selectors + ")...");
            }

            String paramType = request.getParameter(PARAM_TYPE);

            RequestDispatcherOptions options = new RequestDispatcherOptions();
            options.setForceResourceType(CONTEXT_TOOLS_RES_TYPE);
            options.setReplaceSelectors(selectors);

            forward(request, response, resource, paramType, options);
        }
    }

    // JSON response

    public void writeJsonPage(JsonWriter writer, ResourceFilter filter,
                              Page page)
            throws IOException {
        writer.beginObject();
        if (page.isValid()) {
            TreeNodeStrategy nodeStrategy = new DefaultTreeNodeStrategy(filter);
            writeJsonNodeData(writer, nodeStrategy, ResourceHandle.use(page.getResource()), LabelType.name, false);
            Resource contentResource = page.getContent().getResource();
            writer.name("jcrContent");
            writeJsonNode(writer, nodeStrategy, ResourceHandle.use(contentResource), LabelType.name, false);
            writer.name("meta").beginObject();
            Site site = page.getSite();
            writer.name("site").value(site != null ? site.getPath() : null);
            writer.name("template").value(page.getTemplatePath());
            writer.name("isTemplate").value(resourceManager.isTemplate(page.getResource()));
            writer.endObject();
        }
        writer.endObject();
    }
}
