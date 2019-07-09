package com.composum.pages.commons.model.properties;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedHashMap;

public abstract class LanguageSet extends LinkedHashMap<String, Language> {

    @Nonnull
    public abstract Language getDefaultLanguage();

    /**
     * returns the language for a language key; 'null' if not declared for the key
     */
    @Nullable
    public Language getLanguage(@Nonnull final String key) {
        return get(key);
    }

    /**
     * returns the language for a language key
     * or the default language if the set doesn't contain an appropriate language
     */
    @Nonnull
    public Language retrieveLanguage(@Nonnull String key) {
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

    @Nonnull
    public Collection<Language> getLanguages() {
        return values();
    }
}
