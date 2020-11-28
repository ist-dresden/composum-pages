package com.composum.pages.commons.request;

import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.service.PageManager;
import com.composum.pages.commons.service.SiteManager;
import com.composum.platform.commons.request.service.RequestRedirectProvider;
import com.composum.sling.core.BeanContext;
import org.apache.http.HttpHeaders;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.jcr.query.Query;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;

import static com.composum.pages.commons.PagesConstants.PN_ALTERNATIVES;

@Component(
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Redirect Provider"
        },
        immediate = true
)
public class PagesRequestRedirectProvider implements RequestRedirectProvider {

    private static final Logger LOG = LoggerFactory.getLogger(PagesRequestRedirectProvider.class);

    public static final String NAME = "pages";

    @Reference
    private SiteManager siteManager;

    @Reference
    private PageManager pageManager;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean canHandle(@Nonnull SlingHttpServletRequest request) {
        Resource resource = request.getResource();
        Resource site = siteManager.getContainingSiteResource(resource);
        return site != null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean redirectRequest(@Nonnull SlingHttpServletRequest request, @Nonnull SlingHttpServletResponse response) {
        Resource resource = request.getResource();
        Resource site = siteManager.getContainingSiteResource(resource);
        if (site != null) {
            ResourceResolver resolver = request.getResourceResolver();
            String requestUrl = request.getRequestURL().toString();
            String query = "/jcr:root" + site.getPath() + "//element(*,cpp:PageContent)[" + PN_ALTERNATIVES + "='" + requestUrl + "']";
            Iterator<Resource> candidates = resolver.findResources(query, Query.XPATH);
            if (!candidates.hasNext()) {
                String requestUri = request.getRequestURI();
                query = "/jcr:root" + site.getPath() + "//element(*,cpp:PageContent)[" + PN_ALTERNATIVES + "='" + requestUri + "']";
                candidates = resolver.findResources(query, Query.XPATH);
            }
            if (candidates.hasNext()) {
                Resource pageContent = candidates.next();
                BeanContext context = new BeanContext.Service(request, response, pageContent);
                Page page = pageManager.getContainingPage(context, pageContent);
                if (page != null) {
                    String canonicalUrl = page.getCanonicalUrl();
                    if (LOG.isInfoEnabled()) {
                        LOG.info("redirect (301) '{}' to '{}' ({})", request.getRequestURL(), canonicalUrl, page.getPath());
                    }
                    response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
                    response.setHeader(HttpHeaders.LOCATION, canonicalUrl);
                    return true;
                }
            }
        }
        return false;
    }
}
