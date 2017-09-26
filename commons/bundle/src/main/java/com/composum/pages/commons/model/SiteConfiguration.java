package com.composum.pages.commons.model;

import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

/**
 * Created by rw on 13.01.17.
 */
public class SiteConfiguration extends ContentModel<Site> {

    public SiteConfiguration() {
    }

    public SiteConfiguration(BeanContext context, Resource resource) {
        initialize(context, resource);
    }
}
