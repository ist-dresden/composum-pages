package com.composum.pages.commons.request;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.wrappers.SlingHttpServletRequestWrapper;

import java.util.Locale;

/**
 * the rquest wrapper to declare the requets locale
 */
public class LocaleRequestWrapper extends SlingHttpServletRequestWrapper {

    protected final Locale locale;

    public LocaleRequestWrapper(SlingHttpServletRequest wrappedRequest, Locale locale) {
        super(wrappedRequest);
        this.locale = locale;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }
}
