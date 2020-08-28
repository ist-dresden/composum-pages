package com.composum.pages.commons.filter.tree;

import com.composum.pages.commons.AssetsConfiguration;
import com.composum.sling.core.config.FilterConfiguration;
import com.composum.sling.core.filter.ResourceFilter;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true)
public class DocumentFilterConfig implements FilterConfiguration {

    @Reference
    private AssetsConfiguration assetsConfig;

    @Override
    public String getName() {
        return AssetsConfiguration.ASSET_FILTER_DOCUMENT;
    }

    @Override
    public ResourceFilter getFilter() {
        return assetsConfig.getDocumentNodeFilter();
    }
}
