package com.composum.pages.commons;

import com.composum.pages.commons.PagesConstants.ReferenceType;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The configuration service for all servlets in the pages bundle.
 */
public interface PagesConfiguration {

    @Nonnull
    Resource getPageMetaDataRoot(@Nonnull ResourceResolver resolver);

    @Nonnull
    ResourceFilter getRequestNodeFilter(@Nonnull SlingHttpServletRequest request,
                                        @Nonnull String paramaName, @Nonnull String defaultFilter);

    @Nonnull
    PagesConfigImpl.Configuration getConfig();

    @Nonnull
    String getPreferredCountry(@Nonnull final String language);

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

    @Nonnull
    ResourceFilter getSiteFilter();

    @Nullable
    ResourceFilter getPageFilter(@Nonnull BeanContext context, @Nonnull String key);

    @Nonnull
    ResourceFilter getReferenceFilter(@Nonnull ReferenceType type);
}
