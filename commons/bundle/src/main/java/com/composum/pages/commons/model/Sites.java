package com.composum.pages.commons.model;

import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.tenant.Tenant;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Sites extends AbstractModel {

    private transient Map<String, String> tenants;
    private transient Collection<Site> sites;
    private transient Collection<Site> templates;

    public Sites() {
    }

    public Sites(BeanContext context, Resource resource) {
        initialize(context, resource);
    }

    public boolean isTenantSupport() {
        return getSiteManager().isTenantSupport();
    }

    public Map<String, String> getTenantOptions() {
        if (tenants == null) {
            tenants = new LinkedHashMap<>();
            for (Map.Entry<String, Tenant> entry : getSiteManager().getTenants(context).entrySet()) {
                tenants.put(entry.getKey(), entry.getValue().getName());
            }
        }
        return tenants;
    }

    public Collection<Site> getSites() {
        if (sites == null) {
            sites = getSiteManager().getSites(context);
        }
        return sites;
    }

    public Collection<Site> getTemplates() {
        if (templates == null) {
            templates = getSiteManager().getSiteTemplates(context, "");
        }
        return templates;
    }

    public boolean isEditingAllowed() {
        return getContext().getResolver().getResource("/libs/composum/pages/stage/edit/console") != null;
    }
}
