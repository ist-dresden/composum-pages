package com.composum.pages.commons.service;

import org.apache.sling.api.resource.ResourceResolver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * the central service to determine and manage the available Themes
 */
public interface ThemeManager {

    @Nonnull
    Collection<Theme> getThemes(@Nonnull ResourceResolver resolver);

    @Nullable
    Theme getTheme(@Nonnull ResourceResolver resolver, @Nonnull String name);
}
