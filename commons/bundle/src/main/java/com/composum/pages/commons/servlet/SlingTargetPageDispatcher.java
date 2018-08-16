package com.composum.pages.commons.servlet;

import com.composum.pages.commons.model.Page;
import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import java.io.IOException;

@Component(
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Sling Target Page Dispatcher"
        }
)
public class SlingTargetPageDispatcher implements PageDispatcher {

    @Override
    public Page getForwardPage(Page page) {
        return page;
    }

    /**
     * Sends a redirect response if a 'sling:target' is set for the page.
     *
     * @param page the page to check for a redirect
     * @return 'true' if a redirect is sent
     */
    @Override
    public boolean redirect(Page page) throws IOException {
        String targetUrl = page.getSlingTargetUrl();
        if (StringUtils.isNotBlank(targetUrl)) {
            page.getContext().getResponse().sendRedirect(targetUrl);
            return true;
        }
        return false;
    }
}
