package com.composum.pages.commons.request;

import javax.annotation.Nonnull;
import java.util.Locale;

/**
 * the facade to bypass the adaptTo(Locale)
 */
public class PagesLocale {

    protected final Locale locale;

    PagesLocale (@Nonnull Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return locale;
    }

    public String toString() {
        return locale.toString();
    }
}
