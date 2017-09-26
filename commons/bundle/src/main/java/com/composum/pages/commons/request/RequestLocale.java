package com.composum.pages.commons.request;

import com.composum.sling.core.BeanContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.Locale;

import static com.composum.pages.commons.PagesConstants.PAGES_PREFIX;

public class RequestLocale extends RequestAspect<Locale> {

    public static final String PARAMETER_NAME = "pages.locale";
    public static final String ATTRIBUTE_KEY = PAGES_PREFIX + "locale";

    public static RequestLocale instance = new RequestLocale();

    /**
     * returns the locale instance determined using the given context (request)
     */
    public static Locale get(BeanContext context) {
        return instance.getAspect(context);
    }

    // request aspect

    protected RequestLocale() {
    }

    @Override
    protected String getValue(Locale instance) {
        return instance.toString();
    }

    @Override
    protected String getParameterName() {
        return PARAMETER_NAME;
    }

    @Override
    protected String getAttributeKey() {
        return ATTRIBUTE_KEY;
    }

    @Override
    protected Locale createInstance(String value) {
        return getLocale(value);
    }

    @Override
    protected Locale createInstance(SlingHttpServletRequest request) {
        return request.getLocale();
    }

    /**
     * transforms a string value into the best matching Locale instance
     */
    public static Locale getLocale(String rule) {
        Locale locale = null;
        if (StringUtils.isNotBlank(rule)) {
            rule = rule.trim().replaceAll(" +", "_").replaceAll("-", "_");
            String[] values = StringUtils.split(rule.trim(), '_');
            switch (values.length) {
                case 0:
                    break;
                case 1:
                    locale = new Locale(values[0]);
                    break;
                case 2:
                    locale = new Locale(values[0], values[1]);
                    break;
                default:
                    locale = new Locale(values[0], values[1], values[2]);
                    break;
            }
        }
        return locale;
    }
}
