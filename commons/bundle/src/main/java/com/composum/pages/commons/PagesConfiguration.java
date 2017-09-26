package com.composum.pages.commons;

import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.servlet.AbstractServiceServlet;

import java.util.Dictionary;

/**
 * The configuration service for all servlets in the pages bundle.
 */
public interface PagesConfiguration {

    String SITE_NODE_FILTER_KEY = "pages.site.filter";
    String PAGE_NODE_FILTER_KEY = "pages.page.filter";
    String CONTAINER_NODE_FILTER_KEY = "pages.container.filter";
    String COMPONENT_NODE_FILTER_KEY = "pages.component.filter";
    String DEVELOPMENT_TREE_FILTER_KEY = "pages.development.filter";

    String SITE_INTERMEDIATE_FILTER_KEY = "pages.site.filter.intermediate";
    String COMPONENT_INTERMEDIATE_FILTER_KEY = "pages.component.filter.intermediate";
    String DEV_INTERMEDIATE_FILTER_KEY = "pages.development.filter.intermediate";
    String ORDERABLE_NODES_FILTER_KEY = "pages.orderable.filter";

    boolean isEnabled(AbstractServiceServlet servlet);

    ResourceFilter getSiteNodeFilter();

    ResourceFilter getPageNodeFilter();

    ResourceFilter getContainerNodeFilter();

    ResourceFilter getComponentNodeFilter();

    ResourceFilter getDevelopmentTreeFilter();

    ResourceFilter getTreeIntermediateFilter();

    ResourceFilter getOrderableNodesFilter();

    Dictionary getProperties();
}
