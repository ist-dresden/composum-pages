package com.composum.pages.commons.model;

import com.composum.pages.commons.service.PageManager;
import com.composum.sling.platform.staging.versions.PlatformVersionsService;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import java.util.Objects;

public class PageVersion extends ContentVersion {

    private transient Page page;

    private transient PageManager pageManager;

    public PageVersion(SiteRelease siteRelease, PlatformVersionsService.Status status) {
        super(siteRelease, status);
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

    public String getTitle() {
        if (null != getPage()) {
            return getPage().getTitle();
        }
        return super.getTitle();
    }

    /**
     * Returns the URL to reference this page version: if this is about workspace, the page path (null if the page is deleted),
     */
    public String getUrl() {
        String url = null;
        if (status.getNextRelease() == null) { // workspace page if not deleted
            if (getPage() != null) {
                return getPage().getUrl();
            }
        }
        return super.getUrl();
    }

    /**
     * The activation status of the page. In the special case that a page is activated in the release, but
     * also modified in the workspace this will return 'modified' to alert the user that there is a
     * new version of the page to be published, even if this is used in the display of a release.
     */
    public PlatformVersionsService.ActivationState getPageActivationState() {
        Page page = getPage();
        if (page != null) {
            return page.getPageActivationState();
        }
        return super.getPageActivationState();
    }

    @Nonnull
    public PageManager getPageManager() {
        if (pageManager == null) {
            pageManager = Objects.requireNonNull(context.getService(PageManager.class));
        }
        return pageManager;
    }
}
