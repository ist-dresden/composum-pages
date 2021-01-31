package com.composum.pages.commons.filter.tree;

import com.composum.pages.commons.PagesConfiguration;
import com.composum.sling.core.config.FilterConfiguration;
import com.composum.sling.core.filter.ResourceFilter;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true)
public class SiteFilterConfig implements FilterConfiguration {

    @Reference
    private PagesConfiguration pagesConfig;

    @Override
    public String getName() {
        return "site";
    }

    @Override
    public ResourceFilter getFilter() {
        return pagesConfig.getSiteNodeFilter();
    }
}
