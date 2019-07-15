package com.composum.pages.commons.servlet;

import com.composum.pages.commons.model.Page;
import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import javax.annotation.Nonnull;
import java.io.IOException;

@Component(
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Sling Target Page Dispatcher",
                Constants.SERVICE_RANKING + ":Integer=11"
        }
)
public class SlingTargetPageDispatcher implements PageDispatcher {

    @Nonnull
    @Override
    public Page getForwardPage(@Nonnull Page page) {
        return page;
    }

    /**
     * Sends a redirect response if a 'sling:target' is set for the page.
     *
     * @param page the page to check for a redirect
     * @return 'true' if a redirect is sent
     */
    @Override
    public boolean redirect(@Nonnull Page page) throws IOException {
        String targetUrl = page.getSlingTargetUrl();
        if (StringUtils.isNotBlank(targetUrl)) {
            Page targetPage = page.getPageManager().getPage(page.getContext(), targetUrl);
            if (targetPage != null) {
                targetUrl = targetPage.getUrl();
            }
            page.getContext().getResponse().sendRedirect(targetUrl);
            return true;
        }
        return false;
    }
}
