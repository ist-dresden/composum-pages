package com.composum.pages.commons.model;

import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.SyntheticResource;

import java.util.Locale;

import static com.composum.pages.commons.PagesConstants.NP_SETTINGS;

/**
 * Created by rw on 13.01.17.
 */
public class SiteConfiguration extends ContentModel<Site> {

    /**
     * the 'settings' submodel with its own 'i18n' handling
     */
    public static class Settings extends AbstractModel {

        protected Settings() {
        }
    }

    private transient Settings settings;

    public SiteConfiguration() {
    }

    public Settings getSettings() {
        if (settings == null) {
            Resource resource = getResource();
            Resource settingsRes = resource.getChild(NP_SETTINGS);
            if (settingsRes == null) {
                settingsRes = new SyntheticResource(resource.getResourceResolver(), resource.getPath() + "/" + NP_SETTINGS, null);
            }
            settings = new Settings();
            settings.initialize(getContext(), settingsRes);
        }
        return settings;
    }

    public SiteConfiguration(BeanContext context, Resource resource) {
        initialize(context, resource);
    }

    public boolean isThumbnailAvailable() {
        return getResource().getChild("thumbnail/image") != null;
    }

    public <T> T getSettingsProperty(String key, Locale locale, Class<T> type) {
        return getSettings().getProperty(key, locale, type);
    }

    public <T> T getSettingsProperty(String key, Locale locale, T defaultValue) {
        return getSettings().getProperty(key, locale, defaultValue);
    }
}
