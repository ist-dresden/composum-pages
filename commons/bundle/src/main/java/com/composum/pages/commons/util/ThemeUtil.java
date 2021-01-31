package com.composum.pages.commons.util;

import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.service.Theme;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ThemeUtil {

    public static String getResourceType(@Nullable final Page page, @Nonnull final Resource element) {
        return page != null ? getResourceType(page.getTheme(), element) : null;
    }

    public static String getResourceType(@Nullable final Theme theme, @Nonnull final Resource element) {
        return theme != null ? getResourceType(theme, element, element.getResourceType()) : null;
    }

    public static String getResourceType(@Nonnull final Theme theme, @Nonnull final Resource element,
                                         @Nonnull final String resourceType) {
        if (StringUtils.isNotBlank(resourceType)) {
            String overlay = theme.getResourceType(element, resourceType);
            if (!resourceType.equals(overlay)) {
                return overlay;
            }
        }
        return resourceType;
    }

    public static Resource getTypeResource(@Nullable final Page page, @Nonnull final Resource editResource) {
        if (page != null) {
            Theme theme = page.getTheme();
            if (theme != null) {
                return getTypeResource(theme, editResource);
            }
        }
        return editResource;
    }

    public static Resource getTypeResource(@Nonnull final Theme theme, @Nonnull final Resource editResource) {
        ResourceResolver resolver = editResource.getResourceResolver();
        String resourceType = ResolverUtil.toResourceType(resolver, editResource.getPath());
        if (StringUtils.isNotBlank(resourceType)) {
            String overlay = theme.getResourceType(editResource, resourceType);
            if (!resourceType.equals(overlay)) {
                return ResolverUtil.getResourceType(editResource.getResourceResolver(), overlay);
            }
        }
        return editResource;
    }

    public static void applyTheme(@Nullable final Page page, @Nonnull final Resource element,
                                  @Nonnull final RequestDispatcherOptions options) {
        if (page != null) {
            applyTheme(page.getTheme(), element, options);
        }
    }

    public static void applyTheme(@Nullable final Theme theme, @Nonnull final Resource element,
                                  @Nonnull final RequestDispatcherOptions options) {
        if (theme != null) {
            applyTheme(theme, element, element.getResourceType(), options);
        }
    }

    public static void applyTheme(@Nonnull final Theme theme, @Nonnull final Resource element,
                                  @Nonnull final String resourceType,
                                  @Nonnull final RequestDispatcherOptions options) {
        if (StringUtils.isNotBlank(resourceType)) {
            String overlay = theme.getResourceType(element, resourceType);
            if (!resourceType.equals(overlay)) {
                options.setForceResourceType(overlay);
            }
        }
    }
}
