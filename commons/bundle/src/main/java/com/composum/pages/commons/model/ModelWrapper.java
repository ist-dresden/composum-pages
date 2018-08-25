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

    @Override
    public BeanContext getContext() {
        return delegate.getContext();
    }

    @Override
    public Resource getResource() {
        return delegate.getResource();
    }

    @Override
    public String getPath() {
        return delegate.getPath();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public String getType() {
        return delegate.getType();
    }

    @Override
    public String getPathHint() {
        return delegate.getPathHint();
    }

    @Override
    public String getTypeHint() {
        return delegate.getTypeHint();
    }

    //

    @Override
    public String getTitle() {
        return delegate.getTitle();
    }

    @Override
    public String getDescription() {
        return delegate.getDescription();
    }

    // component

    @Override
    public Component getComponent() {
        return delegate.getComponent();
    }

    @Override
    public PagesConstants.ComponentType getComponentType() {
        return delegate.getComponentType();
    }

    // Sites & Pages

    @Override
    public Page getCurrentPage() {
        return delegate.getCurrentPage();
    }

    @Override
    public Page getContainingPage() {
        return delegate.getContainingPage();
    }

    //

    @Override
    public String getUrl() {
        return delegate.getUrl();
    }

    @Override
    public String getCssBase() {
        return delegate.getCssBase();
    }

    // properties & i18n

    @Override
    public Language getLanguage() {
        return delegate.getLanguage();
    }

    // resource properties

    @Override
    public <T> T getProperty(String key, T defaultValue) {
        return delegate.getProperty(key, defaultValue);
    }

    @Override
    public <T> T getProperty(String key, Class<T> type) {
        return delegate.getProperty(key, type);
    }

    @Override
    public <T> T getProperty(Locale locale, T defaultValue, String... keys) {
        return delegate.getProperty(locale, defaultValue, keys);
    }

    // inherited properties

    @Override
    public <T> T getInherited(String key, T defaultValue) {
        return delegate.getInherited(key, defaultValue);
    }

    @Override
    public <T> T getInherited(String key, Class<T> type) {
        return delegate.getInherited(key, type);
    }

    @Override
    public <T> T getInherited(Locale locale, T defaultValue, String... keys) {
        return delegate.getInherited(locale, defaultValue, keys);
    }
}
