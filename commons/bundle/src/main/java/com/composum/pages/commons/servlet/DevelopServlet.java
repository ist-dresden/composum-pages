/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.servlet;

import com.composum.pages.commons.AssetsConfiguration;
import com.composum.pages.commons.PagesConfiguration;
import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.Component.ComponentPieces;
import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.service.ComponentManager;
import com.composum.pages.commons.service.EditService;
import com.composum.pages.commons.service.PageManager;
import com.composum.pages.commons.service.ResourceManager;
import com.composum.pages.commons.service.VersionsService;
import com.composum.pages.commons.util.RequestUtil;
import com.composum.pages.commons.util.ResolverUtil;
import com.composum.pages.commons.util.ResourceTypeUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.filter.StringFilter;
import com.composum.sling.core.servlet.ServletOperation;
import com.composum.sling.core.servlet.ServletOperationSet;
import com.composum.sling.core.servlet.Status;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
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
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import static com.composum.pages.commons.model.Component.isComponent;
import static com.composum.pages.commons.util.ResourceTypeUtil.DEVELOP_ACTIONS_PATH;

/**
 * the servlet for develop mode retrieval and changes
 */
@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Develop Servlet",
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/cpm/pages/develop",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_POST,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_PUT
        })
public class DevelopServlet extends ContentServlet {

    private static final Logger LOG = LoggerFactory.getLogger(DevelopServlet.class);

    public static final String PARAM_TEMPLATE_PATH = "templatePath";

    public static final String DEFAULT_FILTER = "page";

    public static final String PAGE_COMPONENTS_RES_TYPE = "composum/pages/stage/edit/tools/main/components";
    public static final String PAGE_COMPONENT_TYPES = "composum-pages-page-component-types";

    public static final String CONTEXT_TOOLS_RES_TYPE = "composum/pages/stage/edit/sidebar/context";

    @Reference
    protected PagesConfiguration pagesConfiguration;

    @Reference
    protected AssetsConfiguration assetsConfiguration;

    @Reference
    protected ComponentManager componentManager;

    @Reference
    protected ResourceManager resourceManager;

    @Reference
    protected PageManager pageManager;

    @Reference
    protected EditService editService;

    @Reference
    protected VersionsService versionsService;

    protected BundleContext bundleContext;

    @Activate
    private void activate(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Deprecated
    protected boolean isEnabled() {
        return true;
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
    protected BeanContext newBeanContext(@Nonnull final SlingHttpServletRequest request,
                                         @Nonnull final SlingHttpServletResponse response,
                                         @Nullable final Resource resource) {
        return new BeanContext.Servlet(getServletContext(), bundleContext, request, response, resource);
    }

    //
    // Servlet operations
    //

    public enum Extension {
        html, json
    }

    public enum Operation {
        tree, treeActions,
        createComponent, adjustComponent,
        createPath, updateFile
    }

    protected PagesEditOperationSet operations = new PagesEditOperationSet();

    @Override
    protected ServletOperationSet getOperations() {
        return operations;
    }

    /**
     * setup of the servlet operation set for this servlet instance
     */
    @Override
    @SuppressWarnings("Duplicates")
    public void init() throws ServletException {
        super.init();

        // GET
        operations.setOperation(ServletOperationSet.Method.GET, Extension.json,
                Operation.tree, new DevTreeOperation());
        operations.setOperation(ServletOperationSet.Method.GET, Extension.html,
                Operation.treeActions, new GetTreeActions());

        // POST
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.createComponent, new CreateComponent());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.adjustComponent, new AdjustComponent());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.createPath, new CreatePath());

        // PUT
        operations.setOperation(ServletOperationSet.Method.PUT, Extension.json,
                Operation.updateFile, new UpdateFile());
    }

    public class PagesEditOperationSet extends ServletOperationSet<Extension, Operation> {

        public PagesEditOperationSet() {
            super(Extension.json);
        }
    }

    //
    // 'develop' Tree
    //

    @Override
    protected ResourceFilter getNodeFilter(SlingHttpServletRequest request) {
        return pagesConfiguration.getDevelopmentTreeFilter();
    }

    public class DevTreeOperation extends TreeOperation {

        @Override
        protected ResourceFilter getNodeFilter(SlingHttpServletRequest request) {
            return pagesConfiguration.getDevelopmentTreeFilter();
        }
    }

    /**
     * sort children of nodes which are not marked 'orderable'
     */
    @Override
    protected List<Resource> prepareTreeItems(ResourceHandle resource, List<Resource> items) {
        if (!pagesConfiguration.getOrderableNodesFilter().accept(resource)) {
            items.sort(Comparator.comparing(this::getSortName));
        }
        return items;
    }

    //
    // Editing Action requests
    //

    protected static final ResourceFilter SOURCE_FILE_FILTER =
            new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.and,
                    new ResourceFilter.PrimaryTypeFilter(new StringFilter.WhiteList("^nt:file")),
                    new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.or,
                            new ResourceFilter.MimeTypeFilter(new StringFilter.WhiteList("^text/.*$"))));

    protected class GetTreeActions extends EditServlet.GetEditResource {

        @Override
        protected String getResourcePath(SlingHttpServletRequest request) {
            return DEVELOP_ACTIONS_PATH;
        }

        @Override
        protected Resource getEditResource(@Nonnull final SlingHttpServletRequest request,
                                           @Nonnull final SlingHttpServletResponse response,
                                           @Nonnull final Resource contentResource,
                                           @Nonnull final String selectors, @Nullable final String type) {
            ResourceResolver resolver = request.getResourceResolver();
            Resource editResource;
            if (isComponent(contentResource)) {
                editResource = ResolverUtil.getResourceType(resolver, ResourceTypeUtil.DEVELOP_COMPONENT_ACTIONS);
            } else if (ResourceFilter.FOLDER.accept(contentResource)) {
                editResource = ResolverUtil.getResourceType(resolver, ResourceTypeUtil.DEVELOP_FOLDER_ACTIONS);
            } else if (SOURCE_FILE_FILTER.accept(contentResource)) {
                editResource = ResolverUtil.getResourceType(resolver, ResourceTypeUtil.DEVELOP_SOURCE_ACTIONS);
            } else if (Page.isPage(contentResource) || Page.isPageContent(contentResource)) {
                editResource = ResolverUtil.getResourceType(resolver, ResourceTypeUtil.DEVELOP_PAGE_ACTIONS);
            } else if (assetsConfiguration.getAnyFileFilter().accept(contentResource)) {
                editResource = ResolverUtil.getResourceType(resolver, ResourceTypeUtil.DEVELOP_FILE_ACTIONS);
            } else {
                editResource = ResolverUtil.getResourceType(resolver, ResourceTypeUtil.NO_DEVELOP_ACTIONS);
            }
            return editResource;
        }
    }

    //
    // Component Editing change requests
    //

    protected ComponentPieces getComponentPieces(SlingHttpServletRequest request) {
        return new ComponentPieces(
                RequestUtil.getParameter(request, "editDialog", false),
                RequestUtil.getParameter(request, "createDialog", false),
                RequestUtil.getParameter(request, "deleteDialog", false),
                RequestUtil.getParameter(request, "editTile", false),
                RequestUtil.getParameter(request, "thumbnail", false),
                RequestUtil.getParameter(request, "helpPage", false),
                RequestUtil.getParameter(request, "editToolbar", false),
                RequestUtil.getParameter(request, "treeActions", false),
                RequestUtil.getParameter(request, "contextActions", false));
    }

    protected class CreateComponent implements ServletOperation {

        @Override
        public void doIt(@Nonnull final SlingHttpServletRequest request,
                         @Nonnull final SlingHttpServletResponse response,
                         @Nonnull final ResourceHandle resource)
                throws RepositoryException, IOException, ServletException {
            Status status = new Status(request, response);
            try {
                ResourceResolver resolver = request.getResourceResolver();
                Resource parent;
                String templatePath = request.getParameter(PARAM_TEMPLATE_PATH);
                String path = request.getParameter(PARAM_PATH);
                String name = request.getParameter(PARAM_NAME);
                if (StringUtils.isNotBlank(path)) {
                    parent = resolver.getResource(path);
                } else {
                    parent = resource;
                }
                if (parent != null && StringUtils.isNotBlank(name)) {
                    componentManager.createComponent(resolver,
                            StringUtils.isNotBlank(templatePath) ? resolver.getResource(templatePath) : null,
                            parent, name,
                            request.getParameter(ResourceUtil.JCR_PRIMARYTYPE),
                            request.getParameter(PagesConstants.PN_COMPONENT_TYPE),
                            request.getParameter(ResourceUtil.PROP_RESOURCE_SUPER_TYPE),
                            request.getParameter(ResourceUtil.JCR_TITLE),
                            request.getParameter(ResourceUtil.JCR_DESCRIPTION),
                            request.getParameterValues(PagesConstants.PN_CATEGORY),
                            getComponentPieces(request));
                    resolver.commit();
                } else {
                    status.withLogging(LOG).error("can't create component - path or name missed");
                }
            } catch (Exception ex) {
                status.withLogging(LOG).error("error creating component: {}", ex);
            }
            status.sendJson();
        }
    }

    protected class AdjustComponent implements ServletOperation {

        @Override
        public void doIt(@Nonnull final SlingHttpServletRequest request,
                         @Nonnull final SlingHttpServletResponse response,
                         @Nonnull final ResourceHandle resource)
                throws RepositoryException, IOException, ServletException {
            Status status = new Status(request, response);
            try {
                ResourceResolver resolver = request.getResourceResolver();
                String templatePath = request.getParameter(PARAM_TEMPLATE_PATH);
                componentManager.adjustComponent(resolver,
                        StringUtils.isNotBlank(templatePath) ? resolver.getResource(templatePath) : null,
                        resource, getComponentPieces(request));
                resolver.commit();
            } catch (Exception ex) {
                status.withLogging(LOG).error("error adjusting component: {}", ex);
            }
            status.sendJson();
        }
    }

    //
    // source code editing
    //

    protected class CreatePath implements ServletOperation {

        @Override
        public void doIt(@Nonnull final SlingHttpServletRequest request,
                         @Nonnull final SlingHttpServletResponse response,
                         @Nonnull final ResourceHandle resource)
                throws RepositoryException, IOException, ServletException {
            Status status = new Status(request, response);
            try {
                ResourceResolver resolver = request.getResourceResolver();
                String path = request.getParameter(PARAM_PATH);
                if (StringUtils.isNotBlank(path)) {
                    componentManager.createPath(resolver, resource, path);
                    resolver.commit();
                } else {
                    status.withLogging(LOG).error("can't create resources - path missed");
                }
            } catch (Exception ex) {
                status.withLogging(LOG).error("error creating path: {}", ex);
            }
            status.sendJson();
        }
    }

    protected class UpdateFile implements ServletOperation {

        @Override
        public void doIt(@Nonnull final SlingHttpServletRequest request,
                         @Nonnull final SlingHttpServletResponse response,
                         @Nonnull final ResourceHandle resource)
                throws RepositoryException, IOException, ServletException {
            Status status = new Status(request, response);
            try {
                ResourceResolver resolver = request.getResourceResolver();
                componentManager.updateFile(resolver, resource.getPath(), request.getInputStream());
                resolver.commit();
            } catch (Exception ex) {
                status.withLogging(LOG).error("error updating file: {}", ex);
            }
            status.sendJson();
        }
    }
}
