package com.composum.pages.commons.model.properties;

import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.service.SiteManager;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.SyntheticResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Locale;

import static com.composum.pages.commons.PagesConstants.DEFAULT_LANGUAGES;
import static com.composum.pages.commons.PagesConstants.LANGUAGES_ATTR;
import static com.composum.pages.commons.PagesConstants.LANGUAGES_PATH;
import static com.composum.pages.commons.PagesConstants.LANGUAGES_TYPE;

public class Languages extends PropertyNodeSet<Language> {

    // static access in the current requests context

    public static Languages get(final BeanContext context) {
        return context.getAttribute(LANGUAGES_ATTR, Languages.class);
    }

    public static void set(final BeanContext context, final Resource resource) {
        final Languages instance = new Languages(context, findLanguages(context, resource));
        set(context, instance);
    }

    public static void set(final BeanContext context, final Languages instance) {
        context.setAttribute(LANGUAGES_ATTR, instance, BeanContext.Scope.request);
    }

    /**
     * search for the language definition to use (existing {site}/jcr:content/languages)
     */
    public static Resource findLanguages(final BeanContext context, final Resource resource) {
        SiteManager siteManager = context.getService(SiteManager.class);
        Resource languagesResource = null;
        Site site = siteManager.getContainingSite(context, resource);
        if (site != null) {
            languagesResource = site.getResource().getChild(LANGUAGES_PATH);
        }
        if (languagesResource == null) {
            languagesResource = context.getResolver().getResource(DEFAULT_LANGUAGES);
        }
        return languagesResource;
    }

    // PropertyNodeSet implementation

    protected LanguageSet languageSet;

    public Languages() {
    }

    public Languages(final BeanContext context, final Resource resource) {
        super(context, resource);
    }

    @Override
    public void initialize(final BeanContext context, final Resource resource) {
        super.initialize(context, resource.isResourceType(LANGUAGES_TYPE)
                ? resource
                : findLanguages(context, resource));
    }

    @Override
    protected void initialize() {
        languageSet = new LanguageSet() {
            @Override
            @Nonnull
            public Language getDefaultLanguage() {
                return Languages.this.getDefaultLanguage();
            }
        };
        super.initialize();
    }

    @Override
    protected Language createProperty(final Resource resource) {
        return new Language(context, resource);
    }

    @Override
    protected Language add(final Resource resource) {
        Language language = super.add(resource);
        languageSet.put(language.getKey(), language);
        return language;
    }

    /**
     * returns the requested language from the element model
     */
    public Language getLanguage() {
        return model.getLanguage();
    }

    /**
     * returns the language for a locale
     * or the default language if the set doesn't contain an appropriate language
     */
    @Nonnull
    public Language getLanguage(final Locale locale) {
        return languageSet.retrieveLanguage(locale.toString());
    }

    /**
     * returns the language for a language key; 'null' if not declared for the key
     */
    @Nullable
    public Language getLanguage(String key) {
        return languageSet.get(key);
    }

    @Nonnull
    public Collection<Language> getLanguages() {
        return languageSet.values();
    }

    @Nonnull
    public LanguageSet getLanguageSet() {
        return languageSet;
    }

    /**
     * returns the default language of the set, simply the first element in the set
     */
    @Nonnull
    public Language getDefaultLanguage() {
        Collection<Language> languages = getLanguages();
        return languages.size() > 0 ? languages.iterator().next() : new SyntheticLanguage("en", "english");
    }

    protected class SyntheticLanguage extends Language {

        public SyntheticLanguage(String key, String label) {
            super(Languages.this.context, new SyntheticResource(Languages.this.context.getResolver(),
                    Languages.this.resource.getPath() + "/" + key, ""));
            this.name = this.key = key;
            this.label = label;
        }
    }
}
