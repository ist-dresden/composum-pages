/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.servlet;

import com.composum.pages.commons.PagesConfiguration;
import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.service.ComponentManager;
import com.composum.pages.commons.service.EditService;
import com.composum.pages.commons.service.VersionsService;
import com.composum.pages.commons.util.ResolverUtil;
import com.composum.pages.commons.util.ResourceTypeUtil;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.filter.StringFilter;
import com.composum.sling.core.servlet.NodeTreeServlet;
import com.composum.sling.core.servlet.ServletOperationSet;
import org.apache.sling.api.SlingHttpServletRequest;
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
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import static com.composum.pages.commons.model.Component.isComponent;
import static com.composum.pages.commons.util.ResourceTypeUtil.DEVELOP_ACTIONS_PATH;

@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Pages Develop Servlet",
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/cpm/pages/develop",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_POST,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_PUT,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_DELETE
        })
public class DevelopServlet extends NodeTreeServlet {

    private static final Logger LOG = LoggerFactory.getLogger(DevelopServlet.class);

    public static final String DEFAULT_FILTER = "page";

    public static final String PAGE_COMPONENTS_RES_TYPE = "composum/pages/stage/edit/tools/main/components";
    public static final String PAGE_COMPONENT_TYPES = "composum-pages-page-component-types";

    public static final String CONTEXT_TOOLS_RES_TYPE = "composum/pages/stage/edit/sidebar/context";

    @Reference
    protected PagesConfiguration pagesConfiguration;

    @Reference
    protected ComponentManager componentManager;

    @Reference
    protected EditService editService;

    @Reference
    protected VersionsService versionsService;

    protected BundleContext bundleContext;

    @Activate
    private void activate(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
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
        tree, treeActions
    }

    protected PagesEditOperationSet operations = new PagesEditOperationSet();

    @Override
    protected ServletOperationSet getOperations() {
        return operations;
    }

    /** setup of the servlet operation set for this servlet instance */
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

        // PUT

        // DELETE

    }

    public class PagesEditOperationSet extends ServletOperationSet<Extension, Operation> {

        public PagesEditOperationSet() {
            super(Extension.json);
        }
    }

    //
    // Tree
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

    //
    // Editing
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
        protected Resource getEditResource(@Nonnull SlingHttpServletRequest request, @Nonnull Resource contentResource,
                                           @Nonnull String selectors, @Nullable String type) {
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
            } else {
                editResource = ResolverUtil.getResourceType(resolver, ResourceTypeUtil.NO_DEVELOP_ACTIONS);
            }
            return editResource;
        }
    }

}
