package com.composum.pages.commons.model;

import com.composum.pages.commons.model.AbstractModel;
import com.composum.pages.commons.model.Site;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

import java.util.List;

public class Sites extends AbstractModel {

    private transient List<Site> sites;

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
        if (sites == null) {
            sites = getSiteManager().getSiteTemplates(context, "");
        }
        return sites;
    }
}
