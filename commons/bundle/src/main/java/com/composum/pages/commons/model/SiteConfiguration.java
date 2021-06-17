package com.composum.pages.commons.model;

import com.composum.sling.core.BeanContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.SyntheticResource;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static com.composum.pages.commons.PagesConstants.NP_SETTINGS;
import static com.composum.pages.commons.model.Image.PROP_IMAGE_REF;

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
    private transient String thumbnailImageRef;

    public SiteConfiguration() {
    }

    public SiteConfiguration(BeanContext context, Resource resource) {
        initialize(context, resource);
    }

    public boolean isTemplate() {
        return getParent().isTemplate();
    }

    public <T> T getSettingsProperty(String key, Locale locale, Class<T> type) {
        return getSettings().getProperty(key, locale, type);
    }

    public <T> T getSettingsProperty(String key, Locale locale, T defaultValue) {
        return getSettings().getProperty(key, locale, defaultValue);
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

    public boolean isThumbnailAvailable() {
        return StringUtils.isNotBlank(getThumbnailImageRef());
    }

    @NotNull
    public String getThumbnailImageRef() {
        if (thumbnailImageRef == null) {
            thumbnailImageRef = "";
            final Resource thumbnailImage = getResource().getChild("thumbnail/image");
            if (thumbnailImage != null) {
                String imageRef = thumbnailImage.getValueMap().get(PROP_IMAGE_REF, String.class);
                if (StringUtils.isNotBlank(imageRef)) {
                    imageRef = StringUtils.replace(imageRef, "${site}", getParent().getPath());
                    if (thumbnailImage.getResourceResolver().getResource(imageRef) != null) {
                        thumbnailImageRef = imageRef;
                    }
                }
            }
        }
        return thumbnailImageRef;
    }
}
