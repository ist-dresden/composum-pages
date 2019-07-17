package com.composum.pages.commons.servlet;

import com.composum.pages.commons.AssetsConfiguration;
import com.composum.pages.commons.PagesConfiguration;
import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.service.PageManager;
import com.composum.pages.commons.service.ResourceManager;
import com.composum.pages.commons.service.SiteManager;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.servlet.ServletOperation;
import com.composum.sling.core.servlet.ServletOperationSet;
import com.composum.sling.core.util.ResponseUtil;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;

@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Pages Assets Servlet",
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

    @Activate
    private void activate(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
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
        filterSet, assetTree, assetData, resourceInfo,
        targetContainers, isAllowedChild,
        moveContent, renameContent, copyContent
    }

    protected PagesAssetOperationSet operations = new PagesAssetOperationSet();

    @Override
    protected ServletOperationSet getOperations() {
        return operations;
    }

    /** setup of the servlet operation set for this servlet instance */
    @Override
    public void init() throws ServletException {
        super.init();

        // GET
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.filterSet, new GetFilterSet());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.assetTree, new TreeOperation());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.resourceInfo, new GetResourceInfo());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.assetData, new GetAssetData());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.isAllowedChild, new CheckIsAllowedChild());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.targetContainers, new GetTargetContainers());

        // POST
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.targetContainers, new GetTargetContainers());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.moveContent, new MoveContentOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.renameContent, new RenameContentOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.copyContent, new CopyContentOperation());

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
