package com.composum.pages.commons.model;

import com.composum.pages.commons.service.SiteManager;
import com.composum.sling.platform.staging.versions.PlatformVersionsService;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import java.util.Objects;

public class SiteVersion extends ContentVersion<SiteConfiguration> {

    private transient Site site;

    private transient SiteManager siteManager;

    public SiteVersion(SiteRelease siteRelease, PlatformVersionsService.Status status) {
        super(siteRelease, status);
    }

    @Override
    public SiteConfiguration getContent() {
        Site site = getSite();
        if (site != null) {
            return site.getContent();
        }
        return null;
    }

    public Site getSite() {
        if (site == null) {
            Resource resource = status.getWorkspaceResource();
            if (resource != null) {
                site = getSiteManager().createBean(context, resource);
            }
        }
        return site;
    }

    @Override
    public String getTitle() {
        Site site = getSite();
        if (site != null) {
            return site.getTitle();
        }
        return null;
    }

    /**
     * Returns the URL to reference this site version: if this is about workspace, the site path (null if the site is deleted),
     */
    @Override
    @Nonnull
    public String getViewerUrl() {
        return "/bin/cpm/pages/stage.preview.site.html";
    }

    @Nonnull
    public String getPreviewUrl() {
        return getViewerUrl() + getPath();
    }

    @Nonnull
    public SiteManager getSiteManager() {
        if (siteManager == null) {
            siteManager = Objects.requireNonNull(context.getService(SiteManager.class));
        }
        return siteManager;
    }
}
