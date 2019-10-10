package com.composum.pages.commons.model;

import com.composum.pages.commons.service.PageManager;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * the Page model for a sites homepage
 */
public class Homepage extends Page {

    private static final Logger LOG = LoggerFactory.getLogger(Homepage.class);

    public Homepage() {
    }

    protected Homepage(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public Homepage(PageManager manager, BeanContext context, Resource resource) {
        this.pageManager = manager;
        initialize(context, resource);
    }

    public boolean isTheSiteItself() {
        return getPath().equals(getSite().getPath());
    }
}
