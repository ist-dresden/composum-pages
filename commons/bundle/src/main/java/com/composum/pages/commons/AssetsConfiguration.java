package com.composum.pages.commons;

import com.composum.sling.core.filter.ResourceFilter;
import org.apache.sling.api.SlingHttpServletRequest;

/**
 * The configuration service for the Assets aspect of the Pages implementation.
 */
public interface AssetsConfiguration {

    ResourceFilter getAssetNodeFilter();

    ResourceFilter getImageNodeFilter();

    ResourceFilter getVideoNodeFilter();

    ResourceFilter getAnyAssetFilter();

    ResourceFilter getRequestNodeFilter(SlingHttpServletRequest request, String paramaName, String defaultFilter);
}
