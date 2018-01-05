package com.composum.pages.commons.model;

import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

import java.util.List;

public class Sites extends AbstractModel {

    private transient List<Site> sites;
    private transient List<Site> templates;

    public Sites() {
    }

    public Sites(BeanContext context, Resource resource) {
        initialize(context, resource);
    }

    public List<Site> getSites() {
        if (sites == null) {
            sites = getSiteManager().getSites(context, "");
        }
        return sites;
    }

    public List<Site> getTemplates() {
        if (templates == null) {
            templates = getSiteManager().getSiteTemplates(context, "");
        }
        return templates;
    }
}