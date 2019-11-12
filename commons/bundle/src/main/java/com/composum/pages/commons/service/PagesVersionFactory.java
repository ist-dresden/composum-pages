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
import org.apache.sling.api.resource.ValueMap;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Component(
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Version Factory"
        }
)
public class PagesVersionFactory implements VersionFactory {

    @Reference
    VersionsService versionsService;

    @Override
    @Nullable
    public ContentVersion getContentVersion(@Nonnull final SiteRelease siteRelease,
                                            @Nonnull final PlatformVersionsService.Status status) {
        ContentVersion result = null;
        Resource resource = versionsService.getResource(siteRelease.getContext(), status);
        if (resource != null) {
            ValueMap values = resource.getValueMap();
            String type = values.get(JcrConstants.JCR_PRIMARYTYPE, "");
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
        }
        return result;
    }
}
