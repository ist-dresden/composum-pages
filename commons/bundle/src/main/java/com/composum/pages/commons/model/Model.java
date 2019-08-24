package com.composum.pages.commons.model;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.properties.Language;
import com.composum.pages.commons.model.properties.Languages;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.SlingBean;
import com.composum.pages.commons.util.LinkUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;

public interface Model extends SlingBean {

    BeanContext getContext();

    Resource getResource();

    String getPathHint();

    String getTypeHint();

    // component

    Component getComponent();

    PagesConstants.ComponentType getComponentType();

    // Sites & Pages

    @Nullable
    Page getCurrentPage();

    @Nullable
    Page getContainingPage();

    //

    @Nonnull
    String getTitle();

    @Nonnull
    String getTileTitle();

    @Nonnull
    String getDescription();

    /**
     * Returns the URL to the resource of this model (mapped and with the appropriate extension).
     *
     * @see LinkUtil#getUrl(SlingHttpServletRequest, String)
     */
    @Nonnull
    String getUrl();

    @Nonnull
    String getCssBase();

    // i18n

    @Nonnull
    Language getLanguage();

    @Nonnull
    Languages getLanguages();

    // resource properties

    @Nonnull
    <T> T getProperty(@Nonnull String key, @Nonnull T defaultValue);

    @Nullable
    <T> T getProperty(@Nonnull String key, @Nonnull Class<T> type);

    @Nonnull
    <T> T getProperty(Locale locale, @Nonnull T defaultValue, String... keys);

    // inherited properties

    @Nonnull
    <T> T getInherited(@Nonnull String key, @Nonnull T defaultValue);

    @Nullable
    <T> T getInherited(@Nonnull String key, @Nonnull Class<T> type);

    @Nonnull
    <T> T getInherited(Locale locale, @Nonnull T defaultValue, String... keys);

    @Nonnull
    String getHashKey();
}
