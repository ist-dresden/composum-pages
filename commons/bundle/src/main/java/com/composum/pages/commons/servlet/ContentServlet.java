package com.composum.pages.commons.servlet;

import com.composum.pages.commons.service.ResourceManager;
import com.composum.pages.commons.util.RequestUtil;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.servlet.NodeTreeServlet;
import com.composum.sling.core.servlet.ServletOperation;
import com.composum.sling.core.util.ResponseUtil;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;

import static com.composum.pages.commons.PagesConstants.PROP_TEMPLATE;
import static com.composum.pages.commons.util.ResourceTypeUtil.isSyntheticResource;

public abstract class ContentServlet extends NodeTreeServlet {

    private static final Logger LOG = LoggerFactory.getLogger(ContentServlet.class);

    protected BundleContext bundleContext;

    protected abstract ResourceManager getResourceManager();

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
            writer.name("isTemplate").value(getResourceManager().isTemplate(handle));
            writer.endObject();
        }
        writer.endObject();
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

        protected void sendResponse(SlingHttpServletResponse response, Resource result)
                throws IOException {
            JsonWriter jsonWriter = ResponseUtil.getJsonWriter(response);
            response.setStatus(HttpServletResponse.SC_OK);
            jsonWriter.beginObject();
            jsonWriter.name("reference").beginObject();
            jsonWriter.name("name").value(result.getName());
            jsonWriter.name("path").value(result.getPath());
            jsonWriter.name("type").value(result.getResourceType());
            jsonWriter.name("prim").value(result.getValueMap().get(JcrConstants.JCR_PRIMARYTYPE, ""));
            jsonWriter.name("synthetic").value(isSyntheticResource(result));
            jsonWriter.endObject();
            jsonWriter.endObject();
        }
    }
}
