package com.composum.pages.commons.model.properties;

import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.service.PageManager;
import com.composum.sling.core.BeanContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;

import static com.composum.pages.commons.PagesConstants.DEFAULT_LANGUAGES;
import static com.composum.pages.commons.PagesConstants.LANGUAGES_ATTR;
import static com.composum.pages.commons.PagesConstants.LANGUAGES_PATH;
import static com.composum.pages.commons.PagesConstants.LANGUAGES_TYPE;
import static com.composum.pages.commons.taglib.DefineObjectsTag.CURRENT_PAGE;

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
     * search for the language definition to use (existing {page}/jcr:content/languages)
     */
    public static Resource findLanguages(final BeanContext context, final Resource resource) {
        Resource languagesResource = null;
        final Page currentPage = context.getAttribute(CURRENT_PAGE, Page.class);
        Resource pageResource;
        if (currentPage != null) {
            pageResource = currentPage.getResource();
        } else {
            final PageManager pageManager = context.getService(PageManager.class);
            pageResource = pageManager.getContainingPageResource(resource);
        }
        while (pageResource != null && (languagesResource = pageResource.getChild(LANGUAGES_PATH)) == null) {
            pageResource = pageResource.getParent();
        }
        if (languagesResource == null) {
            languagesResource = context.getResolver().getResource(DEFAULT_LANGUAGES);
        }
        return languagesResource;
    }

    // PropertyNodeSet implementation

    protected HashMap<String, Language> languageSet;

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
        languageSet = new HashMap<>();
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
    public Language getLanguage(final Locale locale) {
        return retrieveLanguage(locale.toString());
    }

    /**
     * returns the language for a language key; 'null' if not declared for the key
     */
    public Language getLanguage(String key) {
       return languageSet.get(key);
    }

    /**
     * returns the language for a language key
     * or the default language if the set doesn't contain an appropriate language
     */
    public Language retrieveLanguage(String key) {
        while (StringUtils.isNotBlank(key)) {
            Language language = getLanguage(key);
            if (language != null) {
                return language;
            }
            int delimiter = key.lastIndexOf('_');
            key = delimiter > 0 ? key.substring(0, delimiter) : "";
        }
        return getDefaultLanguage();
    }

    /**
     * returns the default language of the set, simply the first element in the set
     */
    public Language getDefaultLanguage() {
        Collection<Language> languages = getLanguageList();
        return languages.size() > 0 ? languages.iterator().next() : null;
    }

    public Collection<Language> getLanguageList() {
        return propertySet.values();
    }
}
