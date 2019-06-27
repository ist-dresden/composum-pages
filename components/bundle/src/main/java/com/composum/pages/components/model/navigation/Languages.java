package com.composum.pages.components.model.navigation;

import com.composum.pages.commons.model.Element;
import com.composum.pages.commons.model.properties.Language;

import java.util.Collection;

public class Languages extends Element {

    public boolean isUseful() {
        return getLanguages().size() > 1;
    }

    public String getCurrentKey() {
        return getLanguage().getKey().toUpperCase();
    }

    public Collection<Language> getLanguageList() {
        return getLanguages().getLanguageList();
    }
}
