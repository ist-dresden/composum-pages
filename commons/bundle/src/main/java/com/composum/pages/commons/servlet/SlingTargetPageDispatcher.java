package com.composum.pages.commons.servlet;

import com.composum.pages.commons.model.Page;
import com.composum.sling.core.util.LinkUtil;
import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import java.io.IOException;

import static com.composum.pages.commons.PagesConstants.PROP_SLING_TARGET;

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
        String targetUrl = getSlingTargetUrl(page);
        if (StringUtils.isNotBlank(targetUrl)) {
            page.getContext().getResponse().sendRedirect(targetUrl);
            return true;
        }
        return false;
    }

    public String getSlingTargetUrl(Page page) {
        String targetUrl = getSlingTarget(page);
        if (StringUtils.isNotBlank(targetUrl)) {
            targetUrl = LinkUtil.getUrl(page.getContext().getRequest(), targetUrl);
        }
        return targetUrl;
    }

    public String getSlingTarget(Page page) {
        return page.getProperty(PROP_SLING_TARGET, null,"");
    }
}
