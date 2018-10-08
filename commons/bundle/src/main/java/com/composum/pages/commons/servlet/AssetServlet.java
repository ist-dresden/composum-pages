package com.composum.pages.commons.servlet;

import com.composum.pages.commons.PagesConfiguration;
import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.service.ResourceManager;
import com.composum.pages.commons.service.SiteManager;
import com.composum.pages.commons.service.VersionsService;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.servlet.ServletOperation;
import com.composum.sling.core.servlet.ServletOperationSet;
import com.composum.sling.core.util.ResponseUtil;
import com.google.gson.stream.JsonWriter;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
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

    @Reference
    protected PagesConfiguration pagesConfiguration;

    @Reference
    protected ResourceManager resourceManager;

    @Reference
    protected SiteManager siteManager;

    @Reference
    protected VersionsService versionsService;

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
        assetTree, assetData, resourceInfo,
        targetContainers, isAllowedChild,
        moveContent, renameContent, copyContent,
        versions, restoreVersion, setVersionLabel, checkpoint
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
                Operation.assetTree, new TreeOperation());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.resourceInfo, new GetResourceInfo());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.assetData, new GetAssetData());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.isAllowedChild, new CheckIsAllowedChild());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.targetContainers, new GetTargetContainers());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.html,
                Operation.versions, new GetVersions());

        // POST
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.checkpoint, new CheckpointOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.targetContainers, new GetTargetContainers());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.moveContent, new MoveContentOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.renameContent, new RenameContentOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.copyContent, new CopyContentOperation());

        // PUT
        operations.setOperation(ServletOperationSet.Method.PUT, Extension.json,
                Operation.restoreVersion, new RestoreVersion());
        operations.setOperation(ServletOperationSet.Method.PUT, Extension.json,
                Operation.setVersionLabel, new SetVersionLabel());

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

    @Override
    protected ResourceFilter getNodeFilter(SlingHttpServletRequest request) {
        return pagesConfiguration.getRequestNodeFilter(request, PARAM_FILTER, DEFAULT_FILTER);
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
                LOG.debug("GetAssetData(" + resource + ")...");
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
                LOG.debug("GetTargetContainers(" + resource + ", " + targetList + ")...");
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("GetTargetContainers(" + resource + "): " + targetList);
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
            writer.name("jcrContent");
            writeJsonNode(writer, nodeStrategy, ResourceHandle.use(contentResource), LabelType.name, false);
            writer.name("meta").beginObject();
            Site site = siteManager.getContainingSite(context, assetResource);
            writer.name("site").value(site != null ? site.getPath() : null);
            writer.endObject();
        }
        writer.endObject();
    }
}
