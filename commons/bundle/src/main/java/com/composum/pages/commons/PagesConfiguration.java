package com.composum.pages.commons;

import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import org.apache.sling.api.SlingHttpServletRequest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The configuration service for all servlets in the pages bundle.
 */
public interface PagesConfiguration {

    @Nonnull
    ResourceFilter getRequestNodeFilter(@Nonnull SlingHttpServletRequest request,
                                        @Nonnull String paramaName, @Nonnull String defaultFilter);

    @Nonnull
    ResourceFilter getSiteNodeFilter();

    @Nonnull
    ResourceFilter getPageNodeFilter();

    @Nonnull
    ResourceFilter getContainerNodeFilter();

    @Nonnull
    ResourceFilter getElementNodeFilter();

    @Nonnull
    ResourceFilter getDevelopmentTreeFilter();

    @Nonnull
    ResourceFilter getTreeIntermediateFilter();

    @Nonnull
    ResourceFilter getOrderableNodesFilter();

    @Nonnull
    ResourceFilter getReplicationRootFilter();

    @Nullable
    public ResourceFilter getPageFilter(@Nonnull BeanContext context, @Nonnull String key);
}
