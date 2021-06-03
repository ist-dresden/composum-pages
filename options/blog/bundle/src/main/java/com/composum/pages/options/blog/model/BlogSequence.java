package com.composum.pages.options.blog.model;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.Page;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BlogSequence extends Page {

    private static final Logger LOG = LoggerFactory.getLogger(BlogSequence.class);

    private transient Page targetPage;

    public boolean redirectRequest() throws IOException {
        if (!isEditMode()) {
            SlingHttpServletResponse response = context.getResponse();
            Page targetPage = getTargetPage();
            if (targetPage != null) {
                BeanContext context = getContext();
                SlingHttpServletRequest request = context.getRequest();
                String canonicalUrl = targetPage.getCanonicalUrl();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("redirect (302) '{}' to '{}' ({})", request.getRequestURL(), canonicalUrl, targetPage.getPath());
                }
                response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
                response.setHeader(HttpHeaders.LOCATION, canonicalUrl);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
            return true;
        }
        return false;
    }

    @Nullable
    public Page getTargetPage() {
        if (targetPage == null) {
            Resource targetResource = null;
            final ValueMap values = resource.getValueMap();
            final String targetPath = values.get(PagesConstants.PROP_SLING_TARGET, String.class);
            if (StringUtils.isNotBlank(targetPath)) {
                final ResourceResolver resolver = context.getResolver();
                targetResource = resolver.getResource(targetPath);
            }
            if (targetResource == null) {
                ResourceFilter pageFilter = new Page.DefaultPageFilter(context);
                for (Resource child : resource.getChildren()) {
                    if (pageFilter.accept(child)) {
                        targetResource = child;
                        break;
                    }
                }
            }
            if (targetResource != null) {
                targetPage = getPageManager().getContainingPage(context, targetResource);
            }
        }
        return targetPage;
    }

    @Nonnull
    public String getTargetPath() {
        Page page = getTargetPage();
        return page != null ? page.getPath() : "";
    }

    @Nonnull
    public String getTargetUrl() {
        Page page = getTargetPage();
        return page != null ? page.getCanonicalUrl() : "";
    }
}
