package com.composum.pages.commons.service;

import com.composum.sling.core.BeanContext;
import org.apache.sling.tenant.Tenant;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public interface PagesTenantSupport {

    /**
     * @return the list of id/tenant pairs of the joined tenants in the context of the current request
     */
    @Nonnull
    Map<String, Tenant> getTenants(@Nonnull BeanContext context);

    /**
     * @return the root path of the sites content of the tenant with the specified id
     */
    @Nullable
    String getContentRoot(@Nonnull BeanContext context, @Nonnull String tenantId);

    /**
     * @return the root path of the site templates of the tenant with the specified id
     */
    @Nullable
    String getApplicationRoot(@Nonnull BeanContext context, @Nonnull String tenantId);
}
