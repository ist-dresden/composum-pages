package com.composum.pages.commons.model;

import com.composum.pages.commons.util.LinkUtil;
import com.composum.pages.commons.util.PagesUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.platform.staging.StagingReleaseManager;
import com.composum.sling.platform.staging.versions.PlatformVersionsService;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;

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

    /** Gives the release and path linked to in the display. Normally this is the version in the release
     * the status is about, but if the item isn't in that release we fall back to the current version or the previous release. */
    @Nonnull
    protected Pair<StagingReleaseManager.Release, String> getReleaseAndPath() {
        if (status.getActivationInfo() != null)
            return Pair.of(status.getActivationInfo().release(), status.getActivationInfo().path());
        else if (status.getCurrentVersionableInfo() != null)
            return Pair.of(null, release.absolutePath(status.getCurrentVersionableInfo().getRelativePath()));
        else // FIXME(hps,2019-07-03) should that really be the previous release?
            return Pair.of(status.getPreviousRelease(), release.absolutePath(status.getPreviousVersionableInfo().getRelativePath()));
    }

    public String getPath() {
        Pair<StagingReleaseManager.Release, String> releaseAndPath = getReleaseAndPath();
        return releaseAndPath.getRight();
    }

    /** Returns the URL to reference this page version. */
    public String getUrl() {
        Pair<StagingReleaseManager.Release, String> releaseAndPath = getReleaseAndPath();
        String url = LinkUtil.getUrl(context.getRequest(), releaseAndPath.getRight());
        if (releaseAndPath.getLeft() != null) {
            url = url + "?" + PARAM_CPM_RELEASE + "=" + releaseAndPath.getLeft().getNumber();
        }
        return url;
    }

    public String getTitle() {
        if (null == status.getActivationInfo()) return null;
        Resource versionResource = status.getActivationInfo().getVersionResource();
        if (null == versionResource) return null;
        return versionResource.getValueMap().get(JCR_FROZENNODE + "/" + ResourceUtil.PROP_TITLE, String.class);
    }

    public String getLastModifiedString() {
        if (status.getActivationInfo() == null) return null;
        Calendar created = status.getActivationInfo().getVersionCreated();
        return null != created ? new SimpleDateFormat(VERSION_DATE_FORMAT).format(created.getTime()) : null;
    }
}
