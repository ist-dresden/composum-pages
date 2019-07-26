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

/** Represents information about a historical version of a page - especially one that's contained in a release. */
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

    public String getPath() {
        String path = null;
        if (status.getActivationInfo() != null) {
            path = status.getActivationInfo().getPath();
        } else if (status.getPreviousVersionableInfo() != null) {
            // weird unwanted case when a document does not appear at all in a release but in its predecessor
            path = release.absolutePath(status.getPreviousVersionableInfo().getRelativePath());
        }
        if (StringUtils.endsWith(path, '/' + JCR_CONTENT)) {
            path = ResourceUtil.getParent(path);
        }
        return path;
    }

    /** Returns the URL to reference this page version. */
    public String getUrl() {
        String url = null;
        if (status.getActivationInfo() != null && status.getActivationInfo().isActive()) {
            String path = status.getActivationInfo().getPath();
            if (StringUtils.endsWith(path, '/' + JCR_CONTENT)) {
                path = ResourceUtil.getParent(path);
            }
            url = LinkUtil.getUrl(context.getRequest(), path);
            url = url + "?" + PARAM_CPM_RELEASE + "=" + status.getRelease().getNumber();
        }
        return url;
    }

    public String getTitle() {
        if (null == status.getActivationInfo()) {
            return null;
        }
        Resource versionResource = status.getActivationInfo().getVersionResource();
        if (null == versionResource) {
            return null;
        }
        return versionResource.getValueMap().get(JCR_FROZENNODE + "/" + ResourceUtil.PROP_TITLE, String.class);
    }

    public String getLastModifiedString() {
        if (status.getActivationInfo() == null) {
            return null;
        }
        Calendar created = status.getActivationInfo().getVersionCreated();
        return null != created ? new SimpleDateFormat(VERSION_DATE_FORMAT).format(created.getTime()) : null;
    }

    public Page getPage() {
        if (page == null) {
            String path = getPath();
            if (StringUtils.isNotBlank(path)) {
                Resource resource = context.getResolver().getResource(path);
                if (resource != null) {
                    page = getPageManager().createBean(context, resource);
                }
            }
        }
        return page;
    }

    public PlatformVersionsService.ActivationState getPageActivationState() {
        Page page = getPage();
        return page != null ? page.getPageActivationState() : PlatformVersionsService.ActivationState.modified;
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
