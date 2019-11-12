package com.composum.pages.commons.service;

import com.composum.pages.commons.model.ContentVersion;
import com.composum.pages.commons.model.SiteRelease;
import com.composum.sling.platform.staging.versions.PlatformVersionsService;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface VersionFactory {

    /**
     * creates an appropriate version bean instance id the factory supports the resource and type
     *
     * @param siteRelease     the requested release
     * @param contentResource the versionable resource
     * @param type            the resources primary type
     * @param status          the current release status of the versionable
     * @return the version object or 'null' if the factory can't create the version instance
     */
    @Nullable
    ContentVersion getContentVersion(@Nonnull SiteRelease siteRelease,
                                     @Nonnull Resource contentResource, @Nonnull String type,
                                     @Nonnull PlatformVersionsService.Status status);
}
