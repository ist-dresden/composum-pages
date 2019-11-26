package com.composum.pages.commons.service;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.ContentVersion;
import com.composum.pages.commons.model.FileVersion;
import com.composum.pages.commons.model.PageVersion;
import com.composum.pages.commons.model.SiteRelease;
import com.composum.pages.commons.model.SiteVersion;
import com.composum.sling.platform.staging.versions.PlatformVersionsService;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Component(
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Version Factory"
        },
        immediate = true
)
public class PagesVersionFactory implements VersionFactory {

    @Override
    @Nullable
    public ContentVersion getContentVersion(@Nonnull final SiteRelease siteRelease,
                                            @Nonnull final Resource contentResource, @Nonnull final String type,
                                            @Nonnull final PlatformVersionsService.Status status) {
        switch (type) {
            case PagesConstants.NODE_TYPE_PAGE:
            case PagesConstants.NODE_TYPE_PAGE_CONTENT:
                return new PageVersion(siteRelease, status);
            case PagesConstants.NODE_TYPE_SITE:
            case PagesConstants.NODE_TYPE_SITE_CONFIGURATION:
                return new SiteVersion(siteRelease, status);
            case JcrConstants.NT_FILE:
            case JcrConstants.NT_RESOURCE:
                return new FileVersion(siteRelease, status);
        }
        return null;
    }
}
