package com.composum.pages.commons.model;

import com.composum.pages.commons.service.PageManager;
import com.composum.pages.commons.service.SiteManager;
import com.composum.pages.commons.util.LinkUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.platform.staging.StagingReleaseManager;
import com.composum.sling.platform.staging.versions.PlatformVersionsService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import static com.composum.pages.commons.PagesConstants.VERSION_DATE_FORMAT;
import static com.composum.sling.platform.staging.impl.ResourceResolverChangeFilter.PARAM_CPM_RELEASE;
import static org.apache.jackrabbit.JcrConstants.JCR_CONTENT;
import static org.apache.jackrabbit.JcrConstants.JCR_FROZENNODE;

/**
 * Can represent either information about the historical version of a page in a release in comparison to the previous release,
 * or information about the status of a page in the workspace in comparison to a release (usually the current release).
 */
public class PageVersion {

    private final StagingReleaseManager.Release release;

    private final BeanContext context;

    private PlatformVersionsService.Status status;

    private transient Page page;

    private transient PageManager pageManager;
    private transient SiteManager siteManager;

    public PageVersion(SiteRelease siteRelease, PlatformVersionsService.Status status) {
        this.release = siteRelease.stagingRelease;
        this.context = siteRelease.getContext();
        this.status = status;
    }

    public PlatformVersionsService.Status getStatus() {
        return status;
    }

    public Page.StatusModel getReleaseStatus() {
        return new Page.StatusModel(status);
    }

    public String getSiteRelativePath() {
        String path = getPath();
        Resource releaseRoot;
        if (Site.isSite(releaseRoot = release.getReleaseRoot())) {
            Site site = getSiteManager().createBean(context, releaseRoot);
            String siteRoot = site.getPath() + "/";
            if (path.startsWith(siteRoot)) {
                path = "./" + path.substring(siteRoot.length());
            }
        }
        return path;
    }

    /**
     * When about workspace: the workspace path (if file was deleted, we take the path in the release);
     * when about a release, this is the path in that release.
     */
    public String getPath() {
        String path = null;
        if (status.getNextVersionable() != null) {
            path = release.absolutePath(status.getNextVersionable().getRelativePath());
        } else if (status.getPreviousVersionable() != null) {
            path = release.absolutePath(status.getPreviousVersionable().getRelativePath());
        }
        path = StringUtils.removeEnd(path, '/' + JCR_CONTENT);
        return path;
    }

    /**
     * Returns the URL to reference this page version: if this is about workspace, the page path (null if the page is deleted),
     */
    public String getUrl() {
        String url = null;
        if (status.getNextRelease() == null) { // workspace page if not deleted
            if (getPage() != null) {
                url = getPage().getUrl();
            }
        } else if (status.getNextVersionable() != null && status.getNextVersionable().isActive()) {
            String path = status.getNextRelease().absolutePath(status.getNextVersionable().getRelativePath());
            path = StringUtils.removeEnd(path, '/' + JCR_CONTENT);
            url = LinkUtil.getUrl(context.getRequest(), path);
            url = url + "?" + PARAM_CPM_RELEASE + "=" + status.getNextRelease().getNumber();
        }
        return url;
    }

    public String getTitle() {
        if (null != getPage()) {
            return getPage().getTitle();
        }
        if (status.getVersionReference() == null) {
            return null;
        }
        Resource versionResource = status.getVersionReference().getVersionResource();
        if (null == versionResource) {
            return null;
        }
        return versionResource.getValueMap().get(JCR_FROZENNODE + "/" + ResourceUtil.PROP_TITLE, String.class);
    }

    public String getLastModifiedString() {
        Calendar lastModified = status.getLastModified();
        return null != lastModified ? new SimpleDateFormat(VERSION_DATE_FORMAT).format(lastModified.getTime()) : null;
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
        if (status != null) {
            return status.getActivationState();
        }
        return PlatformVersionsService.ActivationState.modified; // shouldn't happen
    }

    @Nonnull
    public PageManager getPageManager() {
        if (pageManager == null) {
            pageManager = Objects.requireNonNull(context.getService(PageManager.class));
        }
        return pageManager;
    }

    @Nonnull
    public SiteManager getSiteManager() {
        if (siteManager == null) {
            siteManager = Objects.requireNonNull(context.getService(SiteManager.class));
        }
        return siteManager;
    }
}
