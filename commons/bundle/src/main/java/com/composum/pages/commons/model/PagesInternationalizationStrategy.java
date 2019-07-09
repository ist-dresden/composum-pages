package com.composum.pages.commons.model;

import com.composum.pages.commons.request.PagesLocale;
import com.composum.platform.models.annotations.InternationalizationStrategy;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.Locale;

/**
 * The {@link InternationalizationStrategy} for Pages, compatible to the {@link AbstractModel} mechanisms. Differs from
 * {@link InternationalizationStrategy.I18NFOLDER} only in that the locale is calculated for the request.
 *
 * @author Hans-Peter Stoerr
 * @see com.composum.platform.models.annotations.Property
 * @see com.composum.platform.models.annotations.PropertyDefaults
 * @since 09/2017
 */
public class PagesInternationalizationStrategy extends InternationalizationStrategy.I18NFOLDER {

    @Override
    protected Locale getLocale(BeanContext beanContext, SlingHttpServletRequest request, Locale locale) {
        Locale fromRequest = null;
        if (request != null) {
            PagesLocale pagesLocale = request.adaptTo(PagesLocale.class);
            if (pagesLocale != null) {
                fromRequest = pagesLocale.getLocale();
            }
        }
        return fromRequest;
    }
}
