package com.composum.pages.commons.service;

import com.composum.pages.commons.model.ContentVersion;
import com.composum.pages.commons.model.SiteRelease;
import com.composum.sling.platform.staging.versions.PlatformVersionsService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface VersionFactory {

    @Nullable
    ContentVersion getContentVersion(@Nonnull SiteRelease siteRelease,
                                     @Nonnull PlatformVersionsService.Status status);
}
