package com.composum.pages.commons.model;

import com.composum.pages.commons.service.PageManager;
import com.composum.sling.platform.staging.versions.PlatformVersionsService;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import java.util.Objects;

public class PageVersion extends ContentVersion<PageContent> {

    private transient Page page;

    private transient PageManager pageManager;

    public PageVersion(SiteRelease siteRelease, PlatformVersionsService.Status status) {
        super(siteRelease, status);
    }

    @Override
    public PageContent getContent() {
        Page page = getPage();
        if (page != null) {
            return page.getContent();
        }
        return null;
    }

    public Page getPage() {
        if (page == null) {
            Resource resource = status.getWorkspaceResource();
            if (resource != null) {
                page = getPageManager().createBean(context, resource);
            }
        }
        return page;
    }

    @Override
    public String getTitle() {
        Page page = getPage();
        if (page != null) {
            return page.getTitle();
        }
        return null;
    }

    /**
     * Returns the URL to reference this page version: if this is about workspace, the page path (null if the page is deleted),
     */
    @Override
    @Nonnull
    public String getViewerUrl() {
        return "";
    }

    @Nonnull
    public String getPreviewUrl() {
        Page page = getPage();
        if (page != null) {
            return page.getUrl();
        }
        // return LinkUtil.getUrl(context.getRequest(), getPath());
        return null; // e.g. deleted page, cannot be previewed.
    }

    @Nonnull
    public PageManager getPageManager() {
        if (pageManager == null) {
            pageManager = Objects.requireNonNull(context.getService(PageManager.class));
        }
        return pageManager;
    }
}
