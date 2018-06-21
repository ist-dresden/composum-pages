package com.composum.pages.options.microsite.servlet;

import com.composum.pages.options.microsite.MicrositeConstants;
import com.composum.pages.options.microsite.service.MicrositeImportService;
import com.composum.pages.options.microsite.service.MicrositeImportStatus;
import com.composum.sling.core.BeanContext;
import com.composum.sling.cpnl.CpnlElFunctions;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.vault.util.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * The servlet of a microsite page (microsite root) implements all features
 * to import (upload) the content AND to deliver the content of the microsite pages.
 */
@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Microsite Servlet",
                ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES + "=composum/pages/options/microsite/page",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_POST,
                ServletResolverConstants.SLING_SERVLET_EXTENSIONS + "=html",
                ServletResolverConstants.SLING_SERVLET_EXTENSIONS + "=json"
        })
public class MicrositeServlet extends SlingAllMethodsServlet implements MicrositeConstants {

    private static final Logger LOG = LoggerFactory.getLogger(MicrositeServlet.class);

    public static final String POST_FORWARD_TYPE = "composum/pages/components/page";

    protected BundleContext bundleContext;

    @Reference
    protected MicrositeImportService importService;

    /**
     * Is delivering the HTML files of the site referenced as suffix of the site page.
     */
    @Override
    public void doGet(@Nonnull SlingHttpServletRequest request, @Nonnull SlingHttpServletResponse response)
            throws IOException {
        RequestPathInfo pathInfo = request.getRequestPathInfo();
        String suffix = pathInfo.getSuffix();
        if (StringUtils.isNotBlank(suffix)) {
            suffix = suffix.substring(1); /* skip leading '/' */
        }
        Resource pageContent = request.getResource();
        ValueMap pageProperties = pageContent.getValueMap();
        String indexPath = pageProperties.get(PN_INDEX_PATH, "");
        Resource contentResource = null;
        if (StringUtils.isNotBlank(suffix)) {
            contentResource = pageContent.getChild(suffix);
            if (contentResource == null && StringUtils.isNotBlank(indexPath)) {
                String intermediatePath = StringUtils.substringBeforeLast(indexPath, "/");
                if (StringUtils.isNotBlank(intermediatePath)) {
                    contentResource = pageContent.getChild(intermediatePath + suffix);
                }
            }
        } else {
            if (StringUtils.isNotBlank(indexPath)) {
                contentResource = pageContent.getChild(indexPath);
            }
        }
        if (contentResource != null && contentResource.isResourceType(JcrConstants.NT_FILE)) {
            contentResource = contentResource.getChild(JcrConstants.JCR_CONTENT);
        }
        if (contentResource != null) {
            ValueMap properties = contentResource.getValueMap();
            InputStream contentStream = (InputStream) properties.get(JcrConstants.JCR_DATA);
            response.setContentType("text/html");
            IOUtils.copy(contentStream, response.getOutputStream());
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * Is importing ZIP files as site content into the content resource of the microsite page.
     */
    @Override
    public void doPost(@Nonnull SlingHttpServletRequest request, @Nonnull SlingHttpServletResponse response)
            throws IOException, ServletException {
        List<String> selectors = Arrays.asList(request.getRequestPathInfo().getSelectors());
        if (selectors.contains(SELECTOR_UPLOAD)) {
            RequestParameter importFile;
            importFile = request.getRequestParameter(PARAM_IMPORT_FILE);
            if (importFile != null) {
                BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
                MicrositeImportStatus importResult = importService.importSiteContent(context, request.getResource(), importFile);
                boolean success = importResult.isSuccessful();
                if (success) {
                    context.getResolver().commit();
                    response.setStatus(HttpServletResponse.SC_CREATED);
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
                response.setContentType("application/json; charset=\"UTF-8\"");
                JsonWriter jsonAnswer = new JsonWriter(response.getWriter());
                jsonAnswer.beginObject();
                jsonAnswer.name("success").value(success);
                jsonAnswer.name("response").beginObject();
                jsonAnswer.name("level").value(success ? "success" : "error");
                jsonAnswer.name("text").value(CpnlElFunctions.i18n(request,
                        success ? "Import sucessful" : "Import failed"));
                jsonAnswer.endObject();
                jsonAnswer.name("messages").beginArray();
                for (MicrositeImportStatus.Message msg : importResult.getMessages()) {
                    jsonAnswer.beginObject();
                    jsonAnswer.name("level").value(msg.getLevel().name());
                    jsonAnswer.name("text").value(msg.toString());
                    jsonAnswer.endObject();
                }
                jsonAnswer.endArray();
                jsonAnswer.endObject();
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "no file to upload");
            }
        } else {
            // forward to normal POST handling (e.g. for the default edit dialog of a page)...
            final RequestDispatcherOptions forwardOptions = new RequestDispatcherOptions();
            forwardOptions.setForceResourceType(POST_FORWARD_TYPE);
            RequestDispatcher dispatcher = request.getRequestDispatcher(request.getResource(), forwardOptions);
            dispatcher.forward(request, response);
        }
    }

    @Activate
    private void activate(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
}