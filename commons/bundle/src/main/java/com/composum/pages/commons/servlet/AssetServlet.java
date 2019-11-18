package com.composum.pages.commons.servlet;

import com.composum.pages.commons.AssetsConfiguration;
import com.composum.pages.commons.PagesConfiguration;
import com.composum.pages.commons.model.Folder;
import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.service.PageManager;
import com.composum.pages.commons.service.ResourceManager;
import com.composum.pages.commons.service.SiteManager;
import com.composum.pages.commons.util.ResolverUtil;
import com.composum.pages.commons.util.ResourceTypeUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.servlet.AbstractServiceServlet;
import com.composum.sling.core.servlet.ServletOperation;
import com.composum.sling.core.servlet.ServletOperationSet;
import com.composum.sling.core.util.MimeTypeUtil;
import com.composum.sling.core.util.ResponseUtil;
import com.composum.sling.platform.staging.versions.PlatformVersionsService;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.tika.mime.MimeType;
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
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.composum.pages.commons.util.ResourceTypeUtil.EDIT_TILE_PATH;
import static com.composum.pages.commons.util.ResourceTypeUtil.TREE_ACTIONS_PATH;

@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Assets Servlet",
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/cpm/pages/assets",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_POST,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_PUT,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_DELETE
        })
public class AssetServlet extends PagesContentServlet {

    private static final Logger LOG = LoggerFactory.getLogger(AssetServlet.class);

    public static final String DEFAULT_FILTER = "page";
    public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Reference
    protected PagesConfiguration pagesConfiguration;

    @Reference
    protected AssetsConfiguration assetsConfiguration;

    @Reference
    protected ResourceManager resourceManager;

    @Reference
    protected SiteManager siteManager;

    @Reference
    protected PageManager pageManager;

    @Reference
    protected PlatformVersionsService platformVersionsService;

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
        filterSet, assetTree, treeActions, assetData, resourceInfo,
        targetContainers, isAllowedChild,
        editTile,
        moveContent, renameContent, copyContent,
        fileCreate, fileUpdate
    }

    protected PagesAssetOperationSet operations = new PagesAssetOperationSet();

    @Override
    protected ServletOperationSet getOperations() {
        return operations;
    }

    /**
     * setup of the servlet operation set for this servlet instance
     */
    @Override
    public void init() throws ServletException {
        super.init();

        // GET
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.filterSet, new GetFilterSet());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.assetTree, new TreeOperation());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.html,
                Operation.treeActions, new GetTreeActions());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.resourceInfo, new GetResourceInfo());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.assetData, new GetAssetData());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.isAllowedChild, new CheckIsAllowedChild());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.targetContainers, new GetTargetContainers());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.html,
                Operation.editTile, new GetEditTile());

        // POST
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.targetContainers, new GetTargetContainers());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.moveContent, new MoveContentOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.renameContent, new RenameContentOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.copyContent, new CopyContentOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.fileCreate, new FileCreateOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.fileUpdate, new FileUpdateOperation());

        // PUT

        // DELETE
    }

    public class PagesAssetOperationSet extends ServletOperationSet<Extension, Operation> {

        public PagesAssetOperationSet() {
            super(Extension.json);
        }
    }

    //
    // Tree
    //

    protected class GetFilterSet implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response, ResourceHandle resource)
                throws IOException {
            Set<String> filterSet = assetsConfiguration.getNodeFilterKeys();
            response.setStatus(HttpServletResponse.SC_OK);
            JsonWriter jsonWriter = ResponseUtil.getJsonWriter(response);
            jsonWriter.beginArray();
            for (String key : filterSet) {
                jsonWriter.value(key);
            }
            jsonWriter.endArray();
        }
    }

    @Override
    protected ResourceFilter getNodeFilter(SlingHttpServletRequest request) {
        return assetsConfiguration.getRequestNodeFilter(request, PARAM_FILTER, DEFAULT_FILTER);
    }

    protected class GetTreeActions extends GetEditResource {

        @Override
        protected String getResourcePath(SlingHttpServletRequest request) {
            return TREE_ACTIONS_PATH;
        }

        @Override
        protected Resource getEditResource(@Nonnull SlingHttpServletRequest request, @Nonnull Resource contentResource,
                                           @Nonnull String selectors, @Nullable String type) {
            Resource result = null;
            ResourceResolver resolver = request.getResourceResolver();
            ResourceFilter assetFileFilter = assetsConfiguration.getAssetFileFilter();
            if (assetFileFilter != null && assetFileFilter.accept(contentResource)) {
                return ResolverUtil.getResourceType(resolver, ResourceTypeUtil.ASSETS_ASSET_ACTIONS);
            } else if (Folder.isFolder(contentResource)) {
                return ResolverUtil.getResourceType(resolver, ResourceTypeUtil.ASSETS_FOLDER_ACTIONS);
            } else if (assetsConfiguration.getAnyFileFilter().accept(contentResource)) {
                return ResolverUtil.getResourceType(resolver, ResourceTypeUtil.DEFAULT_FILE_ACTIONS);
            } else {
                return super.getEditResource(request, contentResource, selectors, type);
            }
        }
    }

    //
    // Asset
    //

    protected class GetAssetData implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {

            if (LOG.isDebugEnabled()) {
                LOG.debug("GetAssetData({})...", resource);
            }

            if (resource.isValid()) {
                BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);

                response.setStatus(HttpServletResponse.SC_OK);
                JsonWriter jsonWriter = ResponseUtil.getJsonWriter(response);
                writeJsonAsset(jsonWriter, context, pagesConfiguration.getPageNodeFilter(), resource);

            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }

    /**
     * get a list of allowed containers on a page to insert (drop) a given asset
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

            if (LOG.isDebugEnabled()) {
                LOG.debug("GetTargetContainers({}): {}", resource, targetList);
            }

            JsonWriter jsonWriter = ResponseUtil.getJsonWriter(response);
            response.setStatus(HttpServletResponse.SC_OK);
            targetList.toJson(jsonWriter);
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

    //
    // Files...
    //

    protected static final Map<String, Object> FILE_PROPERTIES = new HashMap<String, Object>() {{
        put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_FILE);
    }};

    protected static final Map<String, Object> FILE_CONTENT_PROPS = new HashMap<String, Object>() {{
        put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_RESOURCE);
        put(JcrConstants.JCR_MIXINTYPES, new String[]{
                JcrConstants.MIX_VERSIONABLE
        });
    }};

    /**
     * The 'fileCreate' via POST (multipart form) implementation expects:
     * <ul>
     * <li>the request suffix with the path of the files parent</li>
     * <li>the 'file' part (form element / parameter) with the binary content</li>
     * <li>an optional 'name' part (form element / parameter) name of the new resource</li>
     * </ul>
     * The 'mix:versionable' type is set if not present and the 'jcr:lastModified' and 'jcr:mimeType' are adjusted.
     */
    protected class FileCreateOperation implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws RepositoryException, IOException {


            // use resource as content if name is always 'jcr:content' else use child 'jcr:content'
            if (resource.isValid()) {

                BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
                ResourceResolver resolver = context.getResolver();
                RequestParameterMap parameters = request.getRequestParameterMap();

                String name = request.getParameter(PARAM_NAME);
                RequestParameter file = parameters.getValue(AbstractServiceServlet.PARAM_FILE);
                if (file != null && (StringUtils.isNotBlank(name) || StringUtils.isNotBlank(name = file.getFileName()))) {

                    Resource fileResource = resolver.create(resource, name, FILE_PROPERTIES);
                    Map<String, Object> content = new HashMap<>(FILE_CONTENT_PROPS);

                    InputStream input = file.getInputStream();
                    content.put(JcrConstants.JCR_DATA, input);

                    MimeType mimeType = MimeTypeUtil.getMimeType(file.getFileName());
                    if (mimeType != null) {
                        content.put(JcrConstants.JCR_MIMETYPE, mimeType.getName());
                    }

                    GregorianCalendar now = new GregorianCalendar();
                    now.setTime(new Date());
                    content.put(JcrConstants.JCR_LASTMODIFIED, now);
                    content.put(JcrConstants.JCR_LASTMODIFIED + "By", resolver.getUserID());

                    resolver.create(fileResource, JcrConstants.JCR_CONTENT, content);

                    resolver.commit();

                    JsonWriter jsonWriter = ResponseUtil.getJsonWriter(response);
                    writeJsonAsset(jsonWriter, context, pagesConfiguration.getPageNodeFilter(), resource);

                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "file or name not available '" + file + "'");
                }

            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "no valid parent resource '" + resource.getPath() + "'");
            }
        }
    }

    /**
     * The 'fileUpdate' via POST (multipart form) implementation expects:
     * <ul>
     * <li>the request suffix with the path of the file</li>
     * <li>the 'file' part (form element / parameter) with the binary content (optional)</li>
     * </ul>
     * The 'mix:versionable' type is set if not present and the 'jcr:lastModified' and 'jcr:mimeType' are adjusted.
     */
    protected class FileUpdateOperation implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws RepositoryException, IOException {

            Resource content = resource;

            // use resource as content if name is always 'jcr:content' else use child 'jcr:content'
            if (resource.isValid() && (JcrConstants.JCR_CONTENT.equals(content.getName()) ||
                    (content = resource.getChild(JcrConstants.JCR_CONTENT)) != null)) {

                ModifiableValueMap values = content.adaptTo(ModifiableValueMap.class);
                if (values != null) {

                    BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
                    ResourceResolver resolver = context.getResolver();
                    RequestParameterMap parameters = request.getRequestParameterMap();

                    RequestParameter file = parameters.getValue(AbstractServiceServlet.PARAM_FILE);
                    if (file != null) {
                        InputStream input = file.getInputStream();
                        values.put(JcrConstants.JCR_DATA, input);

                        MimeType mimeType = MimeTypeUtil.getMimeType(file.getFileName());
                        if (mimeType != null) {
                            values.put(JcrConstants.JCR_MIMETYPE, mimeType.getName());
                        }
                    }

                    List<String> mixins = new ArrayList<>(Arrays.asList(values.get(JcrConstants.JCR_MIXINTYPES, new String[0])));
                    if (!mixins.contains(JcrConstants.MIX_VERSIONABLE)) {
                        mixins.add(JcrConstants.MIX_VERSIONABLE);
                        values.put(JcrConstants.JCR_MIXINTYPES, mixins.toArray(new String[0]));
                    }

                    GregorianCalendar now = new GregorianCalendar();
                    now.setTime(new Date());
                    values.put(JcrConstants.JCR_LASTMODIFIED, now);
                    values.put(JcrConstants.JCR_LASTMODIFIED + "By", resolver.getUserID());

                    resolver.commit();

                    JsonWriter jsonWriter = ResponseUtil.getJsonWriter(response);
                    writeJsonAsset(jsonWriter, context, pagesConfiguration.getPageNodeFilter(), resource);

                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "can't change file '" + resource.getPath() + "'");
                }

            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "no valid file resource '" + resource.getPath() + "'");
            }
        }
    }

    // JSON response

    public void writeJsonAsset(JsonWriter writer, BeanContext context, ResourceFilter filter, Resource assetResource)
            throws IOException {
        writer.beginObject();
        TreeNodeStrategy nodeStrategy = new DefaultTreeNodeStrategy(filter);
        writeJsonNodeData(writer, nodeStrategy, ResourceHandle.use(assetResource), LabelType.name, false);
        Resource contentResource = assetResource.getChild(JcrConstants.JCR_CONTENT);
        if (contentResource != null) {
            ValueMap values = contentResource.getValueMap();
            writer.name("jcrContent");
            writeJsonNode(writer, nodeStrategy, ResourceHandle.use(contentResource), LabelType.name, false);
            writer.name("meta").beginObject();
            Site site = siteManager.getContainingSite(context, assetResource);
            writer.name("site").value(site != null ? site.getPath() : null);
            writer.endObject();
            writer.name("asset").beginObject();
            String string;
            Calendar time;
            if ((time = values.get(JcrConstants.JCR_LASTMODIFIED, Calendar.class)) != null) {
                writer.name("lastModified").value(new SimpleDateFormat(TIME_FORMAT).format(time.getTime()));
            }
            if (StringUtils.isNotBlank(string = values.get(JcrConstants.JCR_MIMETYPE, String.class))) {
                writer.name("mimeType").value(string);
            }
            writer.endObject();
        }
        writer.endObject();
    }
}
