package com.composum.pages.commons.model.properties;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * a set of declared languages mapped for Locale like keys ({lang}[_{country}[_{variant}]])
 */
public abstract class LanguageSet {

    protected final LinkedHashMap<String, Language> set = new LinkedHashMap<>();
    protected final LinkedHashMap<String, Language> defaults = new LinkedHashMap<>();

    @Nonnull
    public abstract Language getDefaultLanguage();

    /**
     * returns the language for a Locale like language key (with fallback to the defaults)
     * or the default language if the set doesn't contain an appropriate language
     */
    @Nonnull
    public Language get(@Nonnull String key) {
        while (StringUtils.isNotBlank(key)) {
            Language language = set.get(key);
            if (language == null) {
                language = defaults.get(key);
            }
            if (language != null) {
                return language;
            }
            int delimiter = key.lastIndexOf('_');
            key = delimiter > 0 ? key.substring(0, delimiter) : "";
        }
        return getDefaultLanguage();
    }

    public void put(@Nonnull String key, @Nonnull final Language language) {
        defaults.remove(key); // remove each explicit declared key from defaults
        set.put(key, language);
        String[] keys = StringUtils.split(key, "_");
        while (key.indexOf('_') > 0) {
            key = StringUtils.substringBeforeLast(key, "_");
            if (!set.containsKey(key)) {
                defaults.putIfAbsent(key, language);
            }
        }
    }

    @Nonnull
    public Collection<Language> values() {
        return set.values();
    }

    public int size() {
        return set.size();
    }
}
