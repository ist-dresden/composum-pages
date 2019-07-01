package com.composum.pages.commons.servlet;

import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.service.PageManager;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.SlingHttpServletRequest;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.annotation.Nonnull;
import java.io.IOException;

/**
 * the dispatcher to route a page request to the designated language variation
 */
@Component(
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Language Page Dispatcher",
                Constants.SERVICE_RANKING + ":Integer=999"
        }
)
public class LanguagePageDispatcher implements PageDispatcher {

    @Reference
    protected PageManager pageManager;

    /**
     * @return the target page for the content forward performed by the PageNodeServlet;
     * this can be the language variant in the case of a language split
     */
    @Nonnull
    @Override
    public Page getForwardPage(@Nonnull Page page) {
        /* FIXME deprecated 'hidden language split'
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
        }*/
        return page;
    }

    @Override
    public boolean redirect(@Nonnull Page page) throws IOException {
        BeanContext context = page.getContext();
        SlingHttpServletRequest request = context.getRequest();
        String requestUrl = request.getRequestURL().append("?").append(request.getQueryString()).toString();
        String pageUrl = page.getUrl();
        if (!requestUrl.contains(pageUrl)) {
            context.getResponse().sendRedirect(pageUrl);
            return true;
        }
        return false;
    }
}
