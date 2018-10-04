package com.composum.pages.commons.servlet;

import com.composum.pages.commons.PagesConfiguration;
import com.composum.pages.commons.model.Container;
import com.composum.pages.commons.model.GenericModel;
import com.composum.pages.commons.model.Model;
import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.service.EditService;
import com.composum.pages.commons.service.PageManager;
import com.composum.pages.commons.service.ResourceManager;
import com.composum.pages.commons.service.SiteManager;
import com.composum.pages.commons.service.VersionsService;
import com.composum.pages.commons.util.RequestUtil;
import com.composum.pages.commons.util.ResourceTypeUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.mapping.MappingRules;
import com.composum.sling.core.servlet.NodeTreeServlet;
import com.composum.sling.core.servlet.ServletOperation;
import com.composum.sling.core.servlet.ServletOperationSet;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.core.util.ResponseUtil;
import com.composum.sling.cpnl.CpnlElFunctions;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.request.RequestParameter;
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
import javax.jcr.ItemExistsException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.version.VersionManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import static com.composum.pages.commons.PagesConstants.PROP_TEMPLATE;
import static com.composum.pages.commons.util.ResourceTypeUtil.CONTEXT_ACTIONS_PATH;
import static com.composum.pages.commons.util.ResourceTypeUtil.CONTEXT_CONTAINER_PATH;
import static com.composum.pages.commons.util.ResourceTypeUtil.EDIT_DIALOG_PATH;
import static com.composum.pages.commons.util.ResourceTypeUtil.EDIT_TILE_PATH;
import static com.composum.pages.commons.util.ResourceTypeUtil.EDIT_TOOLBAR_PATH;
import static com.composum.pages.commons.util.ResourceTypeUtil.NEW_DIALOG_PATH;
import static com.composum.pages.commons.util.ResourceTypeUtil.TREE_ACTIONS_PATH;
import static com.composum.pages.commons.util.ResourceTypeUtil.isSyntheticResource;

@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Pages Edit Servlet",
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/cpm/pages/edit",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_POST,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_PUT,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_DELETE
        })
public class EditServlet extends NodeTreeServlet {

    private static final Logger LOG = LoggerFactory.getLogger(EditServlet.class);

    public static final String PARAM_FILTER = "filter";
    public static final String DEFAULT_FILTER = "page";

    public static final String EDIT_RESOURCE_KEY = EditServlet.class.getName() + "_resource";
    public static final String EDIT_RESOURCE_TYPE_KEY = EditServlet.class.getName() + "_resourceType";

    public static final String PAGE_COMPONENTS_RES_TYPE = "composum/pages/stage/edit/tools/main/components";
    public static final String PAGE_COMPONENT_TYPES = "composum-pages-page-component-types";

    public static final String CONTEXT_TOOLS_RES_TYPE = "composum/pages/stage/edit/sidebar/context";
    public static final String VERSIONS_RESOURCE_TYPE = "composum/pages/stage/edit/tools/page/versions";

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
        pageComponents, targetContainers, isAllowedElement,
        insertElement, moveElement, copyElement,
        createPage, deletePage, moveContent, renameContent, copyContent,
        createSite, deleteSite,
        contextTools, context,
        versions, restoreVersion, checkpoint, setVersionLabel
    }

    protected BundleContext bundleContext;

    @Reference
    protected ResourceManager resourceManager;

    @Reference
    protected EditService editService;

    @Reference
    protected PageManager pageManager;

    @Reference
    protected SiteManager siteManager;

    @Reference
    protected VersionsService versionsService;

    @Reference
    protected PagesConfiguration pagesConfiguration;

    protected MoveElementOperation moveElementOperation;

    @Activate
    private void activate(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    protected PagesEditOperationSet operations = new PagesEditOperationSet();

    @Override
    protected ServletOperationSet getOperations() {
        return operations;
    }

    @Override
    protected boolean isEnabled() {
        return true;
    }

    /** setup of the servlet operation set for this servlet instance */
    @Override
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
                Operation.moveContent, new MoveContent());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.renameContent, new RenameContent());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.copyContent, new CopyContent());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.createSite, new CreateSite());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.deleteSite, new DeleteSite());

        // PUT
        operations.setOperation(ServletOperationSet.Method.PUT, Extension.html,
                Operation.pageComponents, new GetPageComponents());
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

    protected class CheckpointOperation implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response, ResourceHandle resource)
                throws IOException {
            try {
                final ResourceResolver resolver = request.getResourceResolver();
                final JackrabbitSession session = (JackrabbitSession) resolver.adaptTo(Session.class);
                final RequestParameter paths = request.getRequestParameter("paths");
                if (session != null && paths != null) {
                    final VersionManager versionManager = session.getWorkspace().getVersionManager();
                    for (String path : paths.getString().split(",")) {
                        if (versionManager.isCheckedOut(path + "/jcr:content")) {
                            versionManager.checkpoint(path + "/jcr:content");
                        }
                    }
                    ResponseUtil.writeEmptyArray(response);
                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "no 'paths' parameter found");
                }
            } catch (final RepositoryException ex) {
                LOG.error(ex.getMessage(), ex);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            }

        }
    }

    protected class GetResourceInfo implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {

            if (LOG.isDebugEnabled()) {
                LOG.debug("GetResourceInfo(" + resource + ")...");
            }

            String type = RequestUtil.getParameter(request, PARAM_TYPE, (String) null);
            ResourceManager.ResourceReference reference = resourceManager.getReference(resource, type);

            response.setStatus(HttpServletResponse.SC_OK);
            JsonWriter jsonWriter = ResponseUtil.getJsonWriter(response);
            jsonWriter.beginObject();
            jsonWriter.name("name").value(resource.getName());
            jsonWriter.name("path").value(resource.getPath());
            jsonWriter.name("type").value(reference.getType());
            jsonWriter.name("prim").value(reference.getPrimaryType());
            jsonWriter.name("synthetic").value(ResourceTypeUtil.isSyntheticResource(resource));
            jsonWriter.name("title").value(resource.getProperty("title",
                    resource.getProperty(ResourceUtil.PROP_TITLE, resource.getName())));
            jsonWriter.endObject();

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

            BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
            Page page = null;
            if (resource.isValid()) {
                page = pageManager.createBean(context, pageManager.getContainingPageResource(resource));
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

    /**
     * hierarchy check for the content hierarchy (Site, Pages, Folder, Files)
     */
    protected class CheckIsAllowedChild implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {

            if (resource != null) {

                ResourceResolver resolver = request.getResourceResolver();
                String parentPath = request.getParameter(PARAM_PATH);
                Resource parent;
                if (StringUtils.isNotBlank(parentPath) && (parent = resolver.getResource(parentPath)) != null) {

                    boolean allowed = resourceManager.isAllowedChild(resolver, parent, resource);

                    response.setStatus(HttpServletResponse.SC_OK);
                    JsonWriter jsonWriter = ResponseUtil.getJsonWriter(response);
                    jsonWriter.beginObject();
                    jsonWriter.name("isAllowed").value(allowed);
                    addAllowedChildInfo(request, response, parent, resource, jsonWriter, allowed);
                    jsonWriter.endObject();

                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "invalid parent");
                }
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }

    //
    // JSON helpers
    //

    public void writeJsonResource(@Nonnull JsonWriter writer, @Nonnull TreeNodeStrategy nodeStrategy, Resource resource)
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
            writer.name("isTemplate").value(resourceManager.isTemplate(handle));
            writer.endObject();
        }
        writer.endObject();
    }

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

    /**
     * sort children of orderable nodes
     */
    @Override
    protected List<Resource> prepareTreeItems(ResourceHandle resource, List<Resource> items) {
        if (!pagesConfiguration.getOrderableNodesFilter().accept(resource)) {
            Collections.sort(items, new Comparator<Resource>() {
                @Override
                public int compare(Resource r1, Resource r2) {
                    return getSortName(r1).compareTo(getSortName(r2));
                }
            });
        }
        return items;
    }

    //
    //
    //

    protected abstract class GetEditResource implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws ServletException, IOException {
            ResourceResolver resolver = request.getResourceResolver();

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
            Resource editResource = ResourceTypeUtil.getSubtype(resolver, contentResource, paramType,
                    getResourcePath(request), selectors);

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
    }

    protected class GetContextResource extends GetEditResource {

        @Override
        protected String getSelectors(SlingHttpServletRequest request) {
            return RequestUtil.getSelectorString(request, null, 2);
        }

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
     * get a list of allowend components (element type) for one page
     * (depends on the current content an the hierarchical rules of the pages elements)
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

                final java.util.List allowedElements =
                        editService.getAllowedElementTypes(context.getResolver(), containerRefs, true);
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

            } catch (RepositoryException | PersistenceException ex) {
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

    //
    // Content manipulation (Page, Folder, File)
    //

    /**
     * send a failed answer with error hints on changes which are forbidden by the structure rules
     *
     * @param parent the designated parent resource
     * @param child  the child resource which is not allowed as the parents child
     */
    protected void sendNotAllowedChild(SlingHttpServletRequest request, SlingHttpServletResponse response,
                                       Resource parent, Resource child)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        JsonWriter jsonWriter = ResponseUtil.getJsonWriter(response);
        jsonWriter.beginObject();
        addAllowedChildInfo(request, response, parent, child, jsonWriter, false);
        jsonWriter.endObject();
    }

    /**
     * add validation hints to a JSON validation answer
     *
     * @param parent  the designated parent resource
     * @param child   the child resource which is not allowed as the parents child
     * @param allowed 'true' if the child can be a child of the designated parent
     */
    protected void addAllowedChildInfo(SlingHttpServletRequest request, SlingHttpServletResponse response,
                                       Resource parent, Resource child, JsonWriter jsonWriter, boolean allowed)
            throws IOException {
        if (!allowed) {
            jsonWriter.name("response").beginObject();
            jsonWriter.name("level").value("error");
            jsonWriter.name("text").value(CpnlElFunctions.i18n(request, "Invalid Target"));
            jsonWriter.endObject();
            jsonWriter.name("messages").beginArray();
            jsonWriter.beginObject();
            jsonWriter.name("level").value("error");
            jsonWriter.name("text").value(CpnlElFunctions.i18n(request, "Target path not allowed"));
            jsonWriter.name("hint").value(CpnlElFunctions.i18n(request, "this change is breaking the resource hierarchy policy rules"));
            jsonWriter.endObject();
            jsonWriter.endArray();
        }
        jsonWriter.name("parent");
        writeJsonResource(jsonWriter, new DefaultTreeNodeStrategy(pagesConfiguration.getPageNodeFilter()), parent);
        jsonWriter.name("child");
        writeJsonResource(jsonWriter, new DefaultTreeNodeStrategy(pagesConfiguration.getPageNodeFilter()), child);
    }

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

        protected void sendResponse(SlingHttpServletResponse response, Resource result)
                throws IOException {
            JsonWriter jsonWriter = ResponseUtil.getJsonWriter(response);
            response.setStatus(HttpServletResponse.SC_OK);
            jsonWriter.beginObject();
            jsonWriter.name("reference").beginObject();
            jsonWriter.name("name").value(result.getName());
            jsonWriter.name("path").value(result.getPath());
            jsonWriter.name("type").value(result.getResourceType());
            jsonWriter.name("prim").value(result.getValueMap().get(JcrConstants.JCR_PRIMARYTYPE,""));
            jsonWriter.name("synthetic").value(isSyntheticResource(result));
            jsonWriter.endObject();
            jsonWriter.endObject();
        }
    }

    protected class MoveContent extends ChangeContentOperation {

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

                } else if (resourceManager.isAllowedChild(resolver, target, resource)) {

                    Resource before = getRequestedSibling(request, target, resource);
                    try {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("MoveContent(" + resource.getPath() + " > " + targetPath + " < "
                                    + (before != null ? before.getName() : "<end>") + ")...");
                        }

                        Resource root = resolver.getResource("/content");
                        if (root != null) {
                            Resource result = resourceManager.moveContentResource(resolver, root,
                                    resource, target, name, before);
                            resolver.commit();

                            sendResponse(response, result);
                        }
                    } catch (ItemExistsException itex) {
                        jsonAnswerItemExists(request, response);

                    } catch (RepositoryException ex) {
                        LOG.error(ex.getMessage(), ex);
                        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
                    }

                } else {
                    sendNotAllowedChild(request, response, target, resource);
                }
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "target doesn't exist: '" + targetPath + "'");
            }
        }
    }

    protected class RenameContent extends ChangeContentOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {

            String name = request.getParameter(PARAM_NAME);

            if (LOG.isDebugEnabled()) {
                LOG.debug("RenameContent(" + resource.getPath() + " > " + name + ")...");
            }

            ResourceResolver resolver = request.getResourceResolver();

            try {
                Resource root = resolver.getResource("/content");
                if (root != null) {
                    Resource result = resourceManager.moveContentResource(resolver, root,
                            resource, resource.getParent(), name, null);
                    resolver.commit();

                    sendResponse(response, result);
                }
            } catch (ItemExistsException itex) {
                jsonAnswerItemExists(request, response);

            } catch (RepositoryException ex) {
                LOG.error(ex.getMessage(), ex);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
            }
        }
    }

    protected class CopyContent extends ChangeContentOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {

            String targetPath = request.getParameter("targetPath");
            String name = request.getParameter(PARAM_NAME);

            ResourceResolver resolver = request.getResourceResolver();
            Resource target = resolver.getResource(targetPath);
            if (target != null) {

                if (resourceManager.isAllowedChild(resolver, target, resource)) {

                    Resource before = getRequestedSibling(request, target, resource);

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("CopyContent(" + resource.getPath() + " > " + targetPath + " < "
                                + (before != null ? before.getName() : "<end>") + ")...");
                    }

                    try {
                        Resource result = resourceManager.copyContentResource(resolver, resource, target, name, before);
                        resolver.commit();

                        sendResponse(response, result);

                    } catch (PersistenceException pex) {

                        if (pex.getCause() instanceof ItemExistsException) {
                            jsonAnswerItemExists(request, response);
                        } else {
                            throw pex;
                        }
                    }

                } else {
                    sendNotAllowedChild(request, response, target, resource);
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

    //
    // Versions
    //

    static class VersionPutParameters {

        public String path;
        public String version;
        public String label;

        public void setPath(String path) {
            this.path = path;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }

    protected class GetVersions implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws ServletException, IOException {

            String selectors = RequestUtil.getSelectorString(request, null, 1);
            if (LOG.isDebugEnabled()) {
                LOG.debug("GetVersions(" + resource + "," + selectors + ")...");
            }

            RequestDispatcherOptions options = new RequestDispatcherOptions();
            options.setForceResourceType(VERSIONS_RESOURCE_TYPE);
            if (StringUtils.isNotBlank(selectors)) {
                options.setReplaceSelectors(selectors);
            }

            forward(request, response, resource, null, options);
        }
    }

    protected class RestoreVersion implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {

            final Gson gson = new Gson();
            final VersionPutParameters params = gson.fromJson(
                    new InputStreamReader(request.getInputStream(), MappingRules.CHARSET.name()),
                    VersionPutParameters.class);

            if (LOG.isDebugEnabled()) {
                LOG.debug("RestoreVersion(" + params.path + "," + params.version + ")...");
            }

            try {
                BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
                versionsService.restoreVersion(context, params.path, params.version);
                ResponseUtil.writeEmptyArray(response);

            } catch (RepositoryException ex) {
                LOG.error(ex.getMessage(), ex);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            }
        }
    }

    protected class SetVersionLabel implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {

            final Gson gson = new Gson();
            final VersionPutParameters params = gson.fromJson(
                    new InputStreamReader(request.getInputStream(), MappingRules.CHARSET.name()),
                    VersionPutParameters.class);

            if (LOG.isDebugEnabled()) {
                LOG.debug("SetVersionLabel(" + params.path + "," + params.version + "," + params.label + ")...");
            }

            try {
                BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
                versionsService.setVersionLabel(context, params.path, params.version, params.label);
                ResponseUtil.writeEmptyArray(response);

            } catch (RepositoryException ex) {
                LOG.error(ex.getMessage(), ex);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            }
        }
    }

    //

    protected void forward(SlingHttpServletRequest request, SlingHttpServletResponse response,
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
}
