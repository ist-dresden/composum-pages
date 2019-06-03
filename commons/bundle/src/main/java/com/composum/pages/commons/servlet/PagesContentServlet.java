package com.composum.pages.commons.servlet;

import com.composum.pages.commons.PagesConfiguration;
import com.composum.pages.commons.service.ResourceManager;
import com.composum.pages.commons.service.VersionsService;
import com.composum.pages.commons.util.RequestUtil;
import com.composum.pages.commons.util.ResourceTypeUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.mapping.MappingRules;
import com.composum.sling.core.servlet.ServletOperation;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.core.util.ResponseUtil;
import com.composum.sling.cpnl.CpnlElFunctions;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.ItemExistsException;
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

public abstract class PagesContentServlet extends ContentServlet {

    private static final Logger LOG = LoggerFactory.getLogger(PagesContentServlet.class);

    public static final String PARAM_FILTER = "filter";

    public static final String EDIT_RESOURCE_KEY = EditServlet.class.getName() + "_resource";
    public static final String EDIT_RESOURCE_TYPE_KEY = EditServlet.class.getName() + "_resourceType";

    public static final String VERSIONS_RESOURCE_TYPE = "composum/pages/stage/edit/tools/page/versions";

    protected abstract PagesConfiguration getPagesConfiguration();

    protected abstract VersionsService getVersionsService();

    // TreeNodeServlet...

    /**
     * sort children of nodes which are not marked 'orderable'
     */
    @Override
    protected List<Resource> prepareTreeItems(ResourceHandle resource, List<Resource> items) {
        if (!getPagesConfiguration().getOrderableNodesFilter().accept(resource)) {
            Collections.sort(items, new Comparator<Resource>() {
                @Override
                public int compare(Resource r1, Resource r2) {
                    return getSortName(r1).compareTo(getSortName(r2));
                }
            });
        }
        return items;
    }

    // general resource data

    protected class GetResourceInfo implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {

            if (LOG.isDebugEnabled()) {
                LOG.debug("GetResourceInfo(" + resource + ")...");
            }

            String type = RequestUtil.getParameter(request, PARAM_TYPE, (String) null);
            ResourceManager.ResourceReference reference = getResourceManager().getReference(resource, type);

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

                    boolean allowed = getResourceManager().isAllowedChild(resolver, parent, resource);

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
        PagesConfiguration configuration = getPagesConfiguration();
        if (!allowed) {
            jsonWriter.name("success").value(false);
            jsonWriter.name("title").value(CpnlElFunctions.i18n(request, "Invalid Target"));
            jsonWriter.name("messages").beginArray();
            jsonWriter.beginObject();
            jsonWriter.name("level").value("error");
            jsonWriter.name("text").value(CpnlElFunctions.i18n(request, "Target path not allowed"));
            jsonWriter.name("hint").value(CpnlElFunctions.i18n(request, "this change is breaking the resource hierarchy policy rules"));
            jsonWriter.endObject();
            jsonWriter.endArray();
        }
        jsonWriter.name("parent");
        writeJsonResource(jsonWriter, new DefaultTreeNodeStrategy(configuration.getPageNodeFilter()), parent);
        jsonWriter.name("child");
        writeJsonResource(jsonWriter, new DefaultTreeNodeStrategy(configuration.getPageNodeFilter()), child);
    }

    protected class MoveContentOperation extends ChangeContentOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {

            String targetPath = request.getParameter("targetPath");
            String name = request.getParameter(PARAM_NAME);

            ResourceResolver resolver = request.getResourceResolver();
            Resource target = resolver.getResource(targetPath);

            if (target != null) {
                moveIt(request, response, resolver, resource, target, name);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "target doesn't exist: '" + targetPath + "'");
            }
        }

        public void moveIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                           ResourceResolver resolver, ResourceHandle resource,
                           Resource target, String name)
                throws IOException {
            ResourceManager resourceManager = getResourceManager();

            if (resourceManager.isAllowedChild(resolver, target, resource)) {

                Resource before = getRequestedSibling(request, target, resource);
                try {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("MoveContentOperation(" + resource.getPath() + " > " + target.getPath() + " < "
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
        }
    }

    protected class RenameContentOperation extends ChangeContentOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {

            String name = request.getParameter(PARAM_NAME);

            if (LOG.isDebugEnabled()) {
                LOG.debug("RenameContentOperation(" + resource.getPath() + " > " + name + ")...");
            }

            ResourceResolver resolver = request.getResourceResolver();

            try {
                Resource root = resolver.getResource("/content");
                if (root != null) {
                    Resource result = getResourceManager().moveContentResource(resolver, root,
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

    protected class CopyContentOperation extends ChangeContentOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {

            String targetPath = request.getParameter("targetPath");
            String name = request.getParameter(PARAM_NAME);

            ResourceResolver resolver = request.getResourceResolver();
            Resource target = resolver.getResource(targetPath);
            if (target != null) {
                ResourceManager resourceManager = getResourceManager();

                if (resourceManager.isAllowedChild(resolver, target, resource)) {

                    Resource before = getRequestedSibling(request, target, resource);

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("CopyContentOperation(" + resource.getPath() + " > " + targetPath + " < "
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
                getVersionsService().restoreVersion(context, params.path, params.version);
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
