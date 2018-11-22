package com.composum.pages.commons.model;

import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

import static com.composum.pages.commons.PagesConstants.PP_SETTINGS;

/**
 * Created by rw on 13.01.17.
 */
public class SiteConfiguration extends ContentModel<Site> {

    public SiteConfiguration() {
    }

    public SiteConfiguration(BeanContext context, Resource resource) {
        initialize(context, resource);
    }

    public boolean isThumbnailAvailable() {
        return getResource().getChild("thumbnail/image") != null;
    }

    public <T> T getSettingsProperty(String key, Class<T> type) {
        return getProperty(PP_SETTINGS + key, null, type);
    }

    public <T> T getSettingsProperty(String key, T defaultValue) {
        return getProperty(PP_SETTINGS + key, null, defaultValue);
    }
}
