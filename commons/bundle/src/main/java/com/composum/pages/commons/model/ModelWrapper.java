package com.composum.pages.commons.model;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.properties.Language;
import com.composum.pages.commons.model.properties.Languages;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
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

    @Nonnull
    @Override
    public String getPath() {
        return delegate.getPath();
    }

    @Nonnull
    @Override
    public String getName() {
        return delegate.getName();
    }

    @Nonnull
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

    @Nonnull
    @Override
    public String getTitle() {
        return delegate.getTitle();
    }

    @Nonnull
    @Override
    public String getTileTitle() {
        return delegate.getTileTitle();
    }

    @Nonnull
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

    @Nonnull
    @Override
    public String getUrl() {
        return delegate.getUrl();
    }

    @Nonnull
    @Override
    public String getCssBase() {
        return delegate.getCssBase();
    }

    // properties & i18n

    @Override
    @Nonnull
    public Language getLanguage() {
        return delegate.getLanguage();
    }

    @Override
    @Nonnull
    public Languages getLanguages() {
        return delegate.getLanguages();
    }

    // resource properties

    @Nonnull
    @Override
    public <T> T getProperty(@Nonnull String key, @Nonnull T defaultValue) {
        return delegate.getProperty(key, defaultValue);
    }

    @Override
    public <T> T getProperty(@Nonnull String key, @Nonnull Class<T> type) {
        return delegate.getProperty(key, type);
    }

    @Nonnull
    @Override
    public <T> T getProperty(Locale locale, @Nonnull T defaultValue, String... keys) {
        return delegate.getProperty(locale, defaultValue, keys);
    }

    // inherited properties

    @Nonnull
    @Override
    public <T> T getInherited(@Nonnull String key, @Nonnull T defaultValue) {
        return delegate.getInherited(key, defaultValue);
    }

    @Override
    public <T> T getInherited(@Nonnull String key, @Nonnull Class<T> type) {
        return delegate.getInherited(key, type);
    }

    @Nonnull
    @Override
    public <T> T getInherited(Locale locale, @Nonnull T defaultValue, String... keys) {
        return delegate.getInherited(locale, defaultValue, keys);
    }

    @Nonnull
    @Override
    public String getHashKey() {
        return delegate.getHashKey();
    }
}
