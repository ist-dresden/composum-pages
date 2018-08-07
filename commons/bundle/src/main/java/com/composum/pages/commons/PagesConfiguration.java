package com.composum.pages.commons;

import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.servlet.AbstractServiceServlet;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.Dictionary;

/**
 * The configuration service for all servlets in the pages bundle.
 */
public interface PagesConfiguration {

    String SITE_NODE_FILTER_KEY = "pages.site.filter";
    String PAGE_NODE_FILTER_KEY = "pages.page.filter";
    String CONTAINER_NODE_FILTER_KEY = "pages.container.filter";
    String ELEMENT_NODE_FILTER_KEY = "pages.element.filter";
    String DEVELOPMENT_TREE_FILTER_KEY = "pages.development.filter";

    String SITE_INTERMEDIATE_FILTER_KEY = "pages.site.filter.intermediate";
    String COMPONENT_INTERMEDIATE_FILTER_KEY = "pages.component.filter.intermediate";
    String DEV_INTERMEDIATE_FILTER_KEY = "pages.development.filter.intermediate";
    String ORDERABLE_NODES_FILTER_KEY = "pages.orderable.filter";

    boolean isEnabled(AbstractServiceServlet servlet);

    ResourceFilter getSiteNodeFilter();

    ResourceFilter getPageNodeFilter();

    ResourceFilter getContainerNodeFilter();

    ResourceFilter getElementNodeFilter();

    ResourceFilter getDevelopmentTreeFilter();

    ResourceFilter getTreeIntermediateFilter();

    ResourceFilter getOrderableNodesFilter();

    ResourceFilter getRequestNodeFilter(SlingHttpServletRequest request, String paramaName, String defaultFilter);

    Dictionary getProperties();
}
