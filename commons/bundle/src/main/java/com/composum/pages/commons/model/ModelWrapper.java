package com.composum.pages.commons.model;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.properties.Language;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

import java.util.Locale;

public class ModelWrapper implements Model {

    protected Model delegate;

    public ModelWrapper(Model model) {
        this.delegate = model;
    }

    protected ModelWrapper() {
    }

    /** @deprecated the normal instantiation mechanism is by using the constructor. */
    @Override
    @Deprecated
    public void initialize(BeanContext context, Resource resource) {
        delegate.initialize(context, resource);
    }

    /** @deprecated the normal instantiation mechanism is by using the constructor. */
    @Override
    @Deprecated
    public void initialize(BeanContext context) {
        delegate.initialize(context);
    }

    public Model getDelegate() {
        return delegate;
    }

    public void setDelegate(Model model) {
        this.delegate = model;
    }

    //

    public BeanContext getContext() {
        return delegate.getContext();
    }

    public Resource getResource() {
        return delegate.getResource();
    }

    public String getPath() {
        return delegate.getPath();
    }

    public String getName() {
        return delegate.getName();
    }

    public String getType() {
        return delegate.getType();
    }

    public String getPathHint() {
        return delegate.getPathHint();
    }

    public String getTypeHint() {
        return delegate.getTypeHint();
    }

    //

    public String getTitle() {
        return delegate.getTitle();
    }

    public String getDescription() {
        return delegate.getDescription();
    }

    // component

    public Component getComponent() {
        return delegate.getComponent();
    }

    public PagesConstants.ComponentType getComponentType() {
        return delegate.getComponentType();
    }

    // Sites & Pages

    public Page getCurrentPage() {
        return delegate.getCurrentPage();
    }

    public Page getContainingPage() {
        return delegate.getContainingPage();
    }

    //

    public String getUrl() {
        return delegate.getUrl();
    }

    public String getCssBase() {
        return delegate.getCssBase();
    }

    // properties & i18n

    public Language getLanguage() {
        return delegate.getLanguage();
    }

    // resource properties

    public <T> T getProperty(String key, T defaultValue) {
        return delegate.getProperty(key, defaultValue);
    }

    public <T> T getProperty(String key, Class<T> type) {
        return delegate.getProperty(key, type);
    }

    public <T> T getProperty(Locale locale, T defaultValue, String... keys) {
        return delegate.getProperty(locale, defaultValue, keys);
    }

    // inherited properties

    public <T> T getInherited(String key, T defaultValue) {
        return delegate.getInherited(key, defaultValue);
    }

    public <T> T getInherited(String key, Class<T> type) {
        return delegate.getInherited(key, type);
    }

    public <T> T getInherited(Locale locale, T defaultValue, String... keys) {
        return delegate.getInherited(locale, defaultValue, keys);
    }
}
