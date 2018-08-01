package com.composum.pages.commons.servlet;

import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.properties.Language;
import com.composum.pages.commons.service.PageManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import static com.composum.pages.commons.PagesConstants.LANGUAGE_NAME_SEPARATOR;

/**
 * the dispatcher to route a page request to the designated language variation
 */
@Component(
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Language Page Dispatcher"
        }
)
public class LanguagePageDispatcher implements PageDispatcher {

    @Reference
    protected PageManager pageManager;

    /**
     * @return the target page for the content forward performed by the PageNodeServlet;
     * this can be the language variant in the case of a language split
     */
    @Override
    public Page getForwardPage(Page page) {
        Language language;
        // it's possible that the request has to use a language variant if default language page is addressed
        // and doesn't support the requested language
        if (page.isDefaultLanguagePage() && !page.getPageLanguages().contains(language = page.getLanguage())) {
            String baseName = StringUtils.substringBeforeLast(page.getName(), LANGUAGE_NAME_SEPARATOR);
            for (Resource sibling : page.getResource().getParent().getChildren()) {
                if (Page.isPage(sibling)) {
                    Page alternative = pageManager.createBean(page.getContext(), sibling);
                    if (alternative.isValid()
                            // searching for a sibling with the same 'base name' which can render the language
                            && baseName.equals(StringUtils.substringBeforeLast(alternative.getName(), LANGUAGE_NAME_SEPARATOR))
                            && alternative.getPageLanguages().contains(language)) {
                        return alternative;
                    }
                }
            }
        }
        return page;
    }

    @Override
    public boolean redirect(Page page) {
        return false;
    }
}
