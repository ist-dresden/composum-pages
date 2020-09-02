package com.composum.pages.commons.servlet;

import com.composum.pages.commons.PagesConfiguration;
import com.composum.pages.commons.service.ResourceManager;
import com.composum.pages.commons.util.RequestUtil;
import com.composum.pages.commons.util.ResourceTypeUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.servlet.ServletOperation;
import com.composum.sling.core.servlet.Status;
import com.composum.sling.core.util.I18N;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.core.util.ResponseUtil;
import com.composum.sling.platform.staging.versions.PlatformVersionsService;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.jcr.ItemExistsException;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public abstract class PagesContentServlet extends ContentServlet {

    private static final Logger LOG = LoggerFactory.getLogger(PagesContentServlet.class);

    public static final String PARAM_ATTR = "attr";

    public static final String PARAM_FILTER = "filter";

    protected abstract PlatformVersionsService getPlatformVersionsService();

    protected abstract PagesConfiguration getPagesConfiguration();

    //
    // Tree
    //

    @Override
    public void writeNodeTreeType(JsonWriter writer, ResourceFilter filter,
                                  ResourceHandle resource, boolean isVirtual)
            throws IOException {
        super.writeNodeTreeType(writer, filter, resource, isVirtual);
        Resource content;
        if (resource.isValid() && (content = resource.getChild(JcrConstants.JCR_CONTENT)) != null
                && ResourceUtil.isResourceType(content, JcrConstants.MIX_VERSIONABLE)) {
            try {
                PlatformVersionsService.Status status = getPlatformVersionsService().getStatus(resource, null);
                if (null != status) {
                    writer.name("release").beginObject();
                    writer.name("status").value(status.getActivationState().name());
                    writer.endObject();
                }
            } catch (RepositoryException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }

    /**
     * sort children of nodes which are not marked 'orderable'
     */
    @Override
    protected List<Resource> prepareTreeItems(ResourceHandle resource, List<Resource> items) {
        if (!getPagesConfiguration().getOrderableNodesFilter().accept(resource)) {
            items.sort(Comparator.comparing(this::getSortName));
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
                LOG.debug("GetResourceInfo({})...", resource);
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
            Status status = new Status(request, response);

            if (resource != null) {
                BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
                ResourceResolver resolver = context.getResolver();

                String parentPath = request.getParameter(PARAM_PATH);
                Resource parent;
                if (StringUtils.isNotBlank(parentPath) && (parent = resolver.getResource(parentPath)) != null) {

                    boolean allowed = getResourceManager().isAllowedChild(resolver, parent, resource);

                    response.setStatus(HttpServletResponse.SC_OK);
                    JsonWriter jsonWriter = ResponseUtil.getJsonWriter(response);
                    jsonWriter.beginObject();
                    jsonWriter.name("isAllowed").value(allowed);
                    addAllowedChildInfo(context, parent, resource, jsonWriter, allowed);
                    jsonWriter.endObject();
                    return; // TODO send result via status

                } else {
                    status.error("invalid parent '{}'", parentPath);
                }
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            status.sendJson();
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
        BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
        response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        JsonWriter jsonWriter = ResponseUtil.getJsonWriter(response);
        jsonWriter.beginObject();
        addAllowedChildInfo(context, parent, child, jsonWriter, false);
        jsonWriter.endObject();
    }

    /**
     * add validation hints to a JSON validation answer
     *
     * @param parent  the designated parent resource
     * @param child   the child resource which is not allowed as the parents child
     * @param allowed 'true' if the child can be a child of the designated parent
     */
    protected void addAllowedChildInfo(@Nonnull final BeanContext context,
                                       @Nonnull final Resource parent, @Nonnull final Resource child,
                                       @Nonnull final JsonWriter jsonWriter, boolean allowed)
            throws IOException {
        PagesConfiguration configuration = getPagesConfiguration();
        if (!allowed) {
            SlingHttpServletRequest request = context.getRequest();
            jsonWriter.name("success").value(false);
            jsonWriter.name("title").value(I18N.get(request, "Invalid Target"));
            jsonWriter.name("messages").beginArray();
            jsonWriter.beginObject();
            jsonWriter.name("level").value("error");
            jsonWriter.name("text").value(I18N.get(request, "Target path not allowed"));
            jsonWriter.name("hint").value(I18N.get(request, "this change is breaking the resource hierarchy policy rules"));
            jsonWriter.endObject();
            jsonWriter.endArray();
        }
        jsonWriter.name("parent");
        writeJsonResource(context, jsonWriter, new DefaultTreeNodeStrategy(configuration.getPageNodeFilter()), parent);
        jsonWriter.name("child");
        writeJsonResource(context, jsonWriter, new DefaultTreeNodeStrategy(configuration.getPageNodeFilter()), child);
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
            Status status = new Status(request, response);
            ResourceManager resourceManager = getResourceManager();
            BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);

            if (resourceManager.isAllowedChild(resolver, target, resource)) {

                Resource before = getRequestedSibling(request, target, resource);
                try {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("MoveContentOperation({} > {} < {})...", resource.getPath(), target.getPath(), before != null ? before.getName() : "<end>");
                    }

                    Resource root = resolver.getResource("/content");
                    if (root != null) {
                        List<Resource> updatedReferrers = new ArrayList<>();
                        Resource result = resourceManager.moveContentResource(resolver, root,
                                resource, target, name, before, updatedReferrers);
                        getPageManager().touch(context, updatedReferrers, null);

                        resolver.commit();

                        sendResponse(status, result, updatedReferrers);
                    }
                } catch (ItemExistsException itex) {
                    jsonAnswerItemExists(request, response);

                } catch (RepositoryException | RuntimeException ex) {
                    status.error(ex.getMessage(), ex);
                    status.sendJson();
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
            Status status = new Status(request, response);
            String name = request.getParameter(PARAM_NAME);

            if (LOG.isDebugEnabled()) {
                LOG.debug("RenameContentOperation({} > {})...", resource.getPath(), name);
            }

            ResourceResolver resolver = request.getResourceResolver();
            BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);

            try {
                Resource root = resolver.getResource("/content");
                if (root != null) {
                    List<Resource> updatedReferrers = new ArrayList<>();
                    Resource result = getResourceManager().moveContentResource(resolver, root,
                            resource, Objects.requireNonNull(resource.getParent()), name, null, updatedReferrers);
                    getPageManager().touch(context, updatedReferrers, null);

                    resolver.commit();

                    sendResponse(status, result, updatedReferrers);
                }
            } catch (ItemExistsException itex) {
                jsonAnswerItemExists(request, response);

            } catch (RepositoryException | RuntimeException ex) {
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
            Status status = new Status(request, response);

            String targetPath = request.getParameter("targetPath");
            String name = request.getParameter(PARAM_NAME);

            ResourceResolver resolver = request.getResourceResolver();
            Resource target = resolver.getResource(targetPath);
            if (target != null) {
                ResourceManager resourceManager = getResourceManager();

                if (resourceManager.isAllowedChild(resolver, target, resource)) {

                    Resource before = getRequestedSibling(request, target, resource);

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("CopyContentOperation({} > {} < {})...", resource.getPath(), targetPath, before != null ? before.getName() : "<end>");
                    }

                    try {
                        Resource result = resourceManager.copyContentResource(resolver, resource, target, name, before);
                        resolver.commit();

                        sendResponse(status, result);

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
}
