package com.composum.pages.commons.model;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.properties.Language;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

import java.util.Locale;

public class ModelWrapper implements Model {

    protected Model model;

    public ModelWrapper(Model model) {
        this.model = model;
    }

    protected ModelWrapper() {
    }

    /** @deprecated the normal instantiation mechanism is by using the constructor. */
    @Override
    @Deprecated
    public void initialize(BeanContext context, Resource resource) {
        model.initialize(context, resource);
    }

    /** @deprecated the normal instantiation mechanism is by using the constructor. */
    @Override
    @Deprecated
    public void initialize(BeanContext context) {
        model.initialize(context);
    }

    //

    public BeanContext getContext() {
        return model.getContext();
    }

    public Resource getResource() {
        return model.getResource();
    }

    public String getPath() {
        return model.getPath();
    }

    public String getName() {
        return model.getName();
    }

    public String getType() {
        return model.getType();
    }

    public String getPathHint() {
        return model.getPathHint();
    }

    public String getTypeHint() {
        return model.getTypeHint();
    }

    //

    public String getTitle() {
        return model.getTitle();
    }

    public String getDescription() {
        return model.getDescription();
    }

    // component

    public Component getComponent() {
        return model.getComponent();
    }

    public PagesConstants.ComponentType getComponentType() {
        return model.getComponentType();
    }

    // Sites & Pages

    public Page getCurrentPage() {
        return model.getCurrentPage();
    }

    public Page getContainingPage() {
        return model.getContainingPage();
    }

    //

    public String getUrl() {
        return model.getUrl();
    }

    public String getCssBase() {
        return model.getCssBase();
    }

    // properties & i18n

    public Language getLanguage() {
        return model.getLanguage();
    }

    // resource properties

    public <T> T getProperty(String key, T defaultValue) {
        return model.getProperty(key, defaultValue);
    }

    public <T> T getProperty(String key, Class<T> type) {
        return model.getProperty(key, type);
    }

    public <T> T getProperty(Locale locale, T defaultValue, String... keys) {
        return model.getProperty(locale, defaultValue, keys);
    }

    // inherited properties

    public <T> T getInherited(String key, T defaultValue) {
        return model.getInherited(key, defaultValue);
    }

    public <T> T getInherited(String key, Class<T> type) {
        return model.getInherited(key, type);
    }

    public <T> T getInherited(Locale locale, T defaultValue, String... keys) {
        return model.getInherited(locale, defaultValue, keys);
    }
}
