package com.composum.pages.commons.service;

import com.composum.sling.core.BeanContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public interface PagesTenantSupport {

    /**
     * @return the list of ids ot the assigned tenants in the context of the current request
     */
    @Nonnull
    Collection<String> getTenantIds(@Nonnull BeanContext context);

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
