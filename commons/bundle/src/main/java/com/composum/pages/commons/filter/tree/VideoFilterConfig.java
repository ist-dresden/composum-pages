package com.composum.pages.commons.filter.tree;

import com.composum.pages.commons.AssetsConfiguration;
import com.composum.sling.core.config.FilterConfiguration;
import com.composum.sling.core.filter.ResourceFilter;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true)
public class VideoFilterConfig implements FilterConfiguration {

    @Reference
    private AssetsConfiguration assetsConfig;

    @Override
    public String getName() {
        return AssetsConfiguration.ASSET_FILTER_VIDEO;
    }

    @Override
    public ResourceFilter getFilter() {
        return assetsConfig.getVideoNodeFilter();
    }
}
