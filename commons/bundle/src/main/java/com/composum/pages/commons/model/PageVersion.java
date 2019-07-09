package com.composum.pages.commons.model;

import com.composum.pages.commons.util.LinkUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.platform.staging.StagingReleaseManager;
import com.composum.sling.platform.staging.versions.PlatformVersionsService;
import org.apache.sling.api.resource.Resource;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.composum.pages.commons.PagesConstants.VERSION_DATE_FORMAT;
import static com.composum.sling.platform.staging.impl.ResourceResolverChangeFilter.PARAM_CPM_RELEASE;
import static org.apache.jackrabbit.JcrConstants.JCR_FROZENNODE;

/** Represents information about a historical version of a page - especially one that's contained in a release. */
public class PageVersion {

    private final StagingReleaseManager.Release release;

    private final BeanContext context;

    private PlatformVersionsService.Status status;

    public PageVersion(SiteRelease siteRelease, PlatformVersionsService.Status status) {
        this.release = siteRelease.stagingRelease;
        this.context = siteRelease.getContext();
        this.status = status;
    }

    public Page.StatusModel getReleaseStatus() {
        return new Page.StatusModel(status);
    }

    public String getPath() {
        if (status.getActivationInfo() != null) {
            return status.getActivationInfo().getPath();
        } else if (status.getPreviousVersionableInfo() != null) {
            // weird unwanted case when a document does not appear at all in a release but in its predecessor
            return release.absolutePath(status.getPreviousVersionableInfo().getRelativePath());
        }
        return null;
    }

    /** Returns the URL to reference this page version. */
    public String getUrl() {
        String url = null;
        if (status.getActivationInfo() != null && status.getActivationInfo().isActive()) {
            url = LinkUtil.getUrl(context.getRequest(), status.getActivationInfo().getPath());
            url = url + "?" + PARAM_CPM_RELEASE + "=" + status.getRelease().getNumber();
        }
        return url;
    }

    public String getTitle() {
        if (null == status.getActivationInfo()) { return null; }
        Resource versionResource = status.getActivationInfo().getVersionResource();
        if (null == versionResource) { return null; }
        return versionResource.getValueMap().get(JCR_FROZENNODE + "/" + ResourceUtil.PROP_TITLE, String.class);
    }

    public String getLastModifiedString() {
        if (status.getActivationInfo() == null) { return null; }
        Calendar created = status.getActivationInfo().getVersionCreated();
        return null != created ? new SimpleDateFormat(VERSION_DATE_FORMAT).format(created.getTime()) : null;
    }
}
