package com.composum.pages.commons.servlet;

import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.service.PageManager;
import com.composum.pages.commons.service.ResourceManager;
import com.composum.pages.commons.util.PagesUtil;
import com.composum.pages.commons.util.RequestUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.servlet.NodeTreeServlet;
import com.composum.sling.core.servlet.ServletOperation;
import com.composum.sling.core.servlet.Status;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static com.composum.pages.commons.PagesConstants.PROP_TEMPLATE;

public abstract class ContentServlet extends NodeTreeServlet {

    private static final Logger LOG = LoggerFactory.getLogger(ContentServlet.class);

    protected BundleContext bundleContext;

    protected abstract ResourceManager getResourceManager();

    protected abstract PageManager getPageManager();

    //
    // JSON helpers
    //

    public void writeJsonPage(@Nonnull final BeanContext context, @Nonnull final JsonWriter writer,
                              @Nonnull final ResourceFilter filter, @Nonnull final Page page)
            throws IOException {
        writer.beginObject();
        if (page.isValid()) {
            TreeNodeStrategy nodeStrategy = new DefaultTreeNodeStrategy(filter);
            writeJsonNodeData(writer, nodeStrategy, ResourceHandle.use(page.getResource()), LabelType.name, false);
            writer.name("reference");
            PagesUtil.write(writer, PagesUtil.getReference(page.getResource(), null));
            Resource contentResource = page.getContent().getResource();
            writer.name("jcrContent");
            writeJsonNode(writer, nodeStrategy, ResourceHandle.use(contentResource), LabelType.name, false);
            writer.name("meta").beginObject();
            Site site = page.getSite();
            writer.name("site").value(site != null ? site.getPath() : null);
            writer.name("template").value(page.getTemplatePath());
            writer.name("isTemplate").value(getResourceManager().isTemplate(context, page.getResource()));
            writer.name("language").value(page.getLocale().getLanguage());
            writer.name("defaultLanguage").value(page.getPageLanguages().getDefaultLanguage().getKey());
            writer.endObject();
        }
        writer.endObject();
    }

    public void writeJsonResource(@Nonnull final BeanContext context, @Nonnull final JsonWriter writer,
                                  @Nonnull final TreeNodeStrategy nodeStrategy, Resource resource)
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
            writer.name("isTemplate").value(getResourceManager().isTemplate(context, handle));
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

        /**
         * Writes the JSON response
         *
         * @param result           the main result resource which was changed
         * @param updatedResources additional resources that have been updated (e.g. referrers) and need to be refreshed because of possibly changed stati etc.
         */
        protected void sendResponse(Status status, Resource result, @Nullable List<Resource> updatedResources)
                throws IOException {
            status.reference("reference", result);
            if (updatedResources != null) {
                status.list("updated", updatedResources);
            }
            status.sendJson();
        }

        /**
         * Writes the JSON response
         *
         * @param result the main result resource which was changed
         */
        protected void sendResponse(Status status, Resource result)
                throws IOException {
            sendResponse(status, result, null);
        }
    }
}
