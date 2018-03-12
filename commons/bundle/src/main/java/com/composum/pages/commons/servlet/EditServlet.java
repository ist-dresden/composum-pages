package com.composum.pages.commons.servlet;

import com.composum.pages.commons.PagesConfiguration;
import com.composum.pages.commons.model.GenericModel;
import com.composum.pages.commons.model.Model;
import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.ResourceReference;
import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.service.EditService;
import com.composum.pages.commons.service.PageManager;
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
import com.composum.sling.core.util.ResponseUtil;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.version.VersionManager;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.composum.pages.commons.util.ResourceTypeUtil.EDIT_DIALOG_PATH;
import static com.composum.pages.commons.util.ResourceTypeUtil.EDIT_TILE_PATH;
import static com.composum.pages.commons.util.ResourceTypeUtil.EDIT_TOOLBAR_PATH;
import static com.composum.pages.commons.util.ResourceTypeUtil.NEW_DIALOG_PATH;
import static com.composum.pages.commons.util.ResourceTypeUtil.TREE_ACTIONS_PATH;

@SlingServlet(paths = "/bin/cpm/pages/edit", methods = {"GET", "POST", "PUT", "DELETE"})
public class EditServlet extends NodeTreeServlet {

    private static final Logger LOG = LoggerFactory.getLogger(EditServlet.class);

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
        pageData, isTemplate,
        siteTree, pageTree, developTree,
        editDialog, newDialog,
        editTile, editToolbar, treeActions,
        pageComponents, targetContainers,
        insertComponent, moveComponent,
        createPage, deletePage,
        createSite, deleteSite,
        contextTools,
        versions, restoreVersion, checkpoint, setVersionLabel
    }

    @Reference
    protected EditService editService;

    @Reference
    protected SiteManager siteManager;

    @Reference
    protected VersionsService versionsService;

    @Reference
    protected PageManager pageManager;

    @Reference
    protected PagesConfiguration pagesConfiguration;

    protected BundleContext bundleContext;

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
                Operation.pageData, new GetPageData());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.isTemplate, new CheckIsTemplate());
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
                Operation.contextTools, new GetContextTools());
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
                Operation.insertComponent, new InsertComponent());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.moveComponent, new MoveComponent());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.createPage, new CreatePage());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.deletePage, new DeletePage());
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

    private static class CheckpointOperation implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response, ResourceHandle resource)
                throws IOException {
            try {
                final ResourceResolver resolver = request.getResourceResolver();
                final JackrabbitSession session = (JackrabbitSession) resolver.adaptTo(Session.class);
                final VersionManager versionManager = session.getWorkspace().getVersionManager();
                final RequestParameter paths = request.getRequestParameter("paths");
                for (String path : paths.getString().split(",")) {
                    if (versionManager.isCheckedOut(path + "/jcr:content")) {
                        versionManager.checkpoint(path + "/jcr:content");
                    }
                }
                ResponseUtil.writeEmptyArray(response);
            } catch (final RepositoryException ex) {
                LOG.error(ex.getMessage(), ex);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            }

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

    //
    // JSON helpers
    //

    public void writeJsonPage(JsonWriter writer, ResourceFilter filter,
                              Page page)
            throws IOException {
        writer.beginObject();
        TreeNodeStrategy nodeStrategy = new DefaultTreeNodeStrategy(filter);
        writeJsonNodeData(writer, nodeStrategy, ResourceHandle.use(page.getResource()), LabelType.name, false);
        if (page.isValid()) {
            Resource contentResource = page.getContent().getResource();
            writer.name("jcrContent");
            writeJsonNode(writer, nodeStrategy, ResourceHandle.use(contentResource), LabelType.name, false);
            writer.name("meta").beginObject();
            Site site = page.getSite();
            writer.name("site").value(site != null ? site.getPath() : null);
            writer.name("template").value(page.getTemplatePath());
            writer.name("isTemplate").value(pageManager.isTemplate(page));
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

        @Override
        protected ResourceFilter getNodeFilter(SlingHttpServletRequest request) {
            return pagesConfiguration.getContainerNodeFilter();
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
            Resource editResource =
                    ResourceTypeUtil.getSubtype(resolver, contentResource, paramType, getResourcePath(), selectors);

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

        protected abstract String getResourcePath();

        protected String getDefaultSelectors() {
            return "";
        }
    }

    protected class GetEditDialog extends GetEditResource {

        @Override
        protected String getResourcePath() {
            return EDIT_DIALOG_PATH;
        }
    }

    protected class GetNewDialog extends GetEditDialog {

        @Override
        protected String getResourcePath() {
            return NEW_DIALOG_PATH;
        }
    }

    protected class GetEditTile extends GetEditResource {

        @Override
        protected String getResourcePath() {
            return EDIT_TILE_PATH;
        }
    }

    protected class GetEditToolbar extends GetEditResource {

        @Override
        protected String getResourcePath() {
            return EDIT_TOOLBAR_PATH;
        }
    }

    protected class GetTreeActions extends GetEditResource {

        @Override
        protected String getResourcePath() {
            return TREE_ACTIONS_PATH;
        }
    }

    //
    // Containers & Elements
    //

    protected class GetPageComponents implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws ServletException, IOException {

            final ResourceResolver resolver = request.getResourceResolver();

            try (final JsonReader reader = new JsonReader(request.getReader())) {

                final ResourceReference.List containerRefs = new ResourceReference.List(resolver, reader);
                String selectors = RequestUtil.getSelectorString(request, null, 1);

                if (LOG.isDebugEnabled()) {
                    LOG.debug("GetPageComponents(" + resource + "," + selectors + ", " + containerRefs + ")...");
                }

                // forward request with 'GET'! for right component rendering
                request = new RequestUtil.GetWrapper(request);

                final java.util.List allowedElements = editService.getAllowedElementTypes(resolver, containerRefs, true);
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

    protected class GetTargetContainers implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {
            final ResourceResolver resolver = request.getResourceResolver();

            ResourceReference.List targetList =
                    new ResourceReference.List(resolver, request.getParameter("targetList"));

            if (LOG.isDebugEnabled()) {
                LOG.debug("GetTargetContainers(" + resource + ", " + targetList + ")...");
            }

            ResourceReference element = new ResourceReference(resource, null);
            targetList = editService.filterTargetContainers(resolver, targetList, element);

            if (LOG.isDebugEnabled()) {
                LOG.debug("GetTargetContainers(" + resource + "): " + targetList);
            }

            JsonWriter jsonWriter = ResponseUtil.getJsonWriter(response);
            response.setStatus(HttpServletResponse.SC_OK);
            targetList.toJson(jsonWriter);
        }
    }

    protected class InsertComponent implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {

            String resourceType = request.getParameter("resourceType");
            String targetPath = request.getParameter("targetPath");
            String targetType = request.getParameter("targetType");
            String beforePath = request.getParameter("before");

            if (LOG.isDebugEnabled()) {
                LOG.debug("InsertComponent(" + resourceType + " > " + targetPath + " < " + beforePath + ")...");
            }

            ResourceResolver resolver = request.getResourceResolver();
            ResourceReference target = new ResourceReference(resolver, targetPath, targetType);
            Resource before = StringUtils.isNotBlank(beforePath) ? resolver.getResource(beforePath) : null;

            try {
                editService.insertComponent(resolver, resourceType, target, before);
                resolver.commit();
                ResponseUtil.writeEmptyObject(response);

            } catch (RepositoryException ex) {
                LOG.error(ex.getMessage(), ex);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
            }
        }
    }

    protected class MoveComponent implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {

            String targetPath = request.getParameter("targetPath");
            String targetType = request.getParameter("targetType");
            String beforePath = request.getParameter("before");

            if (LOG.isDebugEnabled()) {
                LOG.debug("MoveComponent(" + resource.getPath() + " > " + targetPath + " < " + beforePath + ")...");
            }

            ResourceResolver resolver = request.getResourceResolver();
            ResourceReference target = new ResourceReference(resolver, targetPath, targetType);
            Resource before = StringUtils.isNotBlank(beforePath) ? resolver.getResource(beforePath) : null;

            try {
                editService.moveComponent(resolver, resolver.getResource("/content"),
                        resource, target, before);
                resolver.commit();

                JsonWriter jsonWriter = ResponseUtil.getJsonWriter(response);
                response.setStatus(HttpServletResponse.SC_OK);
                jsonWriter.beginObject();
                jsonWriter.endObject();

            } catch (RepositoryException ex) {
                LOG.error(ex.getMessage(), ex);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
            }
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

            BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
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
