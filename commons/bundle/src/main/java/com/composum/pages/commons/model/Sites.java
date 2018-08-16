package com.composum.pages.commons.model;

import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

import java.util.Collection;

public class Sites extends AbstractModel {

    private transient Collection<Site> sites;
    private transient Collection<Site> templates;

    public Sites() {
    }

    public Sites(BeanContext context, Resource resource) {
        initialize(context, resource);
    }

    public Collection<Site> getSites() {
        if (sites == null) {
            sites = getSiteManager().getSites(context, "");
        }
        return sites;
    }

    public Collection<Site> getTemplates() {
        if (templates == null) {
            templates = getSiteManager().getSiteTemplates(context, "");
        }
        return templates;
    }
}
