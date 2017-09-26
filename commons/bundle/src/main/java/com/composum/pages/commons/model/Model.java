package com.composum.pages.commons.model;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.properties.Language;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.SlingBean;
import com.composum.sling.core.util.LinkUtil;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

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

    Page getCurrentPage();

    Page getContainingPage();

    //

    String getTitle();

    String getDescription();

    /**
     * Returns the URL to the resource of this model (mapped and with the appropriate extension).
     *
     * @see LinkUtil#getUrl(SlingHttpServletRequest, String)
     */
    String getUrl();

    String getCssBase();

    // i18n

    Language getLanguage();

    // resource properties

    <T> T getProperty(String key, T defaultValue);

    <T> T getProperty(String key, Class<T> type);

    <T> T getProperty(Locale locale, T defaultValue, String... keys);

    // inherited properties

    <T> T getInherited(String key, T defaultValue);

    <T> T getInherited(String key, Class<T> type);

    <T> T getInherited(Locale locale, T defaultValue, String... keys);
}
