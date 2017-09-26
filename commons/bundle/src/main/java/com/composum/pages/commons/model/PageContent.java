package com.composum.pages.commons.model;

import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

/**
 * Created by rw on 13.01.17.
 */
public class PageContent extends ContentModel<Page> {

    public PageContent() {
    }

    public PageContent(BeanContext context, Resource resource) {
        initialize(context, resource);
    }
}
