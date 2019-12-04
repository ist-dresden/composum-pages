package com.composum.pages.commons.model.properties;

import com.composum.pages.commons.PagesConfiguration;
import com.composum.pages.commons.request.PagesLocale;
import com.composum.sling.core.BeanContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import java.util.Locale;

public class Language extends PropertyNode {

    public static final String PROP_KEY = "key";
    public static final String PROP_LABEL = "label";
    public static final String PROP_DIRECTION = "direction";

    protected String name;
    protected String key;
    protected String label;
    protected String direction;

    private transient Locale locale;

    public Language(BeanContext context, Resource resource) {
        super(context, resource);
    }

    protected void initialize() {
        name = resource.getName();
        key = values.get(PROP_KEY, "??");
        label = values.get(PROP_LABEL, key);
        direction = values.get(PROP_DIRECTION, "");
    }

    public String toString() {
        return key;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public String getLanguageKey() {
        return StringUtils.split(getKey(), "_")[0];
    }

    public String getLabel() {
        return label;
    }

    public String getDirection() {
        return direction;
    }

    public Locale getLocale() {
        if (locale == null) {
            String[] key = StringUtils.split(getKey(), "_", 3);
            String language = key[0];
            String country = key.length > 1 ? key[1] : "";
            if (StringUtils.isBlank(country)) {
                SlingHttpServletRequest request = context.getRequest();
                if (request != null) {
                    PagesLocale pagesLocale = request.adaptTo(PagesLocale.class);
                    if (pagesLocale != null) {
                        country = pagesLocale.getLocale().getCountry();
                    }
                    if (StringUtils.isBlank(country)) {
                        country = request.getLocale().getCountry();
                    }
                }
                if (StringUtils.isBlank(country)) {
                    country = context.getService(PagesConfiguration.class).getPreferredCountry(language);
                }
            }
            locale = new Locale(language, country, key.length > 2 ? key[2] : "");
        }
        return locale;
    }

    // Object

    @Override
    public boolean equals(Object object) {
        return object instanceof Language && key.equals(((Language) object).key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
