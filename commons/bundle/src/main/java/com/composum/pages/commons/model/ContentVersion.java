package com.composum.pages.commons.model;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.service.SiteManager;
import com.composum.pages.commons.service.VersionsService;
import com.composum.sling.core.BeanContext;
import com.composum.sling.platform.staging.StagingReleaseManager;
import com.composum.sling.platform.staging.VersionReference;
import com.composum.sling.platform.staging.versions.PlatformVersionsService;
import com.composum.sling.platform.staging.versions.PlatformVersionsService.ActivationState;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.jcr.RepositoryException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.regex.Matcher;

import static com.composum.pages.commons.PagesConstants.VERSION_DATE_FORMAT;
import static org.apache.jackrabbit.JcrConstants.JCR_CONTENT;

/**
 * Can represent either information about the historical version of a content in a release in comparison to the previous release,
 * or information about the status of a content in the workspace in comparison to a release (usually the current release).
 */
public abstract class ContentVersion<ContentType extends ContentModel> {

    private static final Logger LOG = LoggerFactory.getLogger(ContentVersion.class);

    /**
     * Pages-Adapter around {@link PlatformVersionsService.Status}.
     */
    public static class StatusModel {

        protected final PlatformVersionsService.Status releaseStatus;

        public StatusModel(PlatformVersionsService.Status status) {
            releaseStatus = status;
        }

        public ActivationState getActivationState() {
            return releaseStatus.getActivationState();
        }

        public String getLastModified() {
            Calendar lastModified = releaseStatus.getLastModified();
            return lastModified != null ? new SimpleDateFormat(VERSION_DATE_FORMAT).format(lastModified.getTime()) : "";
        }

        public String getLastModifiedBy() {
            return releaseStatus.getLastModifiedBy();
        }

        public String getReleaseLabel() {
            StagingReleaseManager.Release previous = releaseStatus.getPreviousRelease();
            String label = previous != null ? releaseStatus.getPreviousRelease().getReleaseLabel() : "";
            Matcher matcher = PagesConstants.RELEASE_LABEL_PATTERN.matcher(label);
            return matcher.matches() ? matcher.group(1) : label;
        }

        public Calendar getLastActivatedTime() {
            return releaseStatus.getVersionReference() != null ? releaseStatus.getVersionReference().getLastActivated() : null;
        }

        public String getLastActivated() {
            Calendar calendar = getLastActivatedTime();
            return calendar != null ? new SimpleDateFormat(VERSION_DATE_FORMAT).format(calendar.getTime()) : "";
        }

        public String getLastActivatedBy() {
            return releaseStatus.getVersionReference() != null ? releaseStatus.getVersionReference().getLastActivatedBy() : null;
        }

        public String getLastDeactivated() {
            Calendar calendar = releaseStatus.getVersionReference() != null ? releaseStatus.getVersionReference().getLastDeactivated() : null;
            return calendar != null ? new SimpleDateFormat(VERSION_DATE_FORMAT).format(calendar.getTime()) : "";
        }

        public String getLastDeactivatedBy() {
            return releaseStatus.getVersionReference() != null ? releaseStatus.getVersionReference().getLastDeactivatedBy() : null;
        }
    }

    protected final BeanContext context;
    protected final StagingReleaseManager.Release release;
    protected final PlatformVersionsService.Status status;

    private transient String path;
    private transient String siteRelativePath;
    private transient StatusModel releaseStatus;
    private transient ContentModel workspaceModel;

    private transient Resource versionResource;
    private transient ValueMap versionProperties;

    private transient VersionsService versionsService;
    private transient PlatformVersionsService platformVersionsService;
    private transient SiteManager siteManager;

    public ContentVersion(SiteRelease siteRelease, PlatformVersionsService.Status status) {
        this.context = siteRelease.getContext();
        this.release = siteRelease.stagingRelease;
        this.status = status;
    }

    public PlatformVersionsService.Status getStatus() {
        return status;
    }

    /**
     * The activation state of the content. We have a special case here:
     * if the versionable is modified in the workspace, we want this to take precedence over status
     * activated to alert the user that there is a modification that is not checked in yet.
     *
     * @return 'modified' if the content is modified after last activation in the current release
     */
    public ActivationState getContentActivationState() {
        StatusModel state = getReleaseStatus();
        ActivationState status = state != null ? state.getActivationState() : null;
        if (status != null && status != ActivationState.activated) {
            return status;
        }
        // the state activated can be overridden to modified if the content is modified
        Calendar lastModified = getContent().getLastModified();
        if (lastModified != null && state != null) {
            Calendar lastActivated = state.getLastActivatedTime();
            if (lastActivated != null && lastActivated.before(lastModified)) {
                status = ActivationState.modified;
            }
        }
        return status;
    }

    public abstract ContentType getContent();

    public StatusModel getReleaseStatus() {
        if (releaseStatus == null) {
            releaseStatus = new StatusModel(status);
        }
        return releaseStatus;
    }

    public String getSiteRelativePath() {
        if (siteRelativePath == null) {
            siteRelativePath = getPath();
            Resource releaseRoot;
            if (Site.isSite(releaseRoot = release.getReleaseRoot())) {
                Site site = getSiteManager().createBean(context, releaseRoot);
                String siteRoot = site.getPath();
                if (siteRelativePath.startsWith(siteRoot + "/")) {
                    siteRelativePath = "./" + path.substring(siteRoot.length() + 1);
                } else if (siteRelativePath.equals(siteRoot)) { // site configuration
                    siteRelativePath = "./";
                }
            }
        }
        return siteRelativePath;
    }

    /**
     * When about workspace: the workspace path (if file was deleted, we take the path in the release);
     * when about a release, this is the path in that release.
     */
    @Nonnull
    public String getPath() {
        if (path == null) {
            path = status.getPath();
            if (StringUtils.isBlank(path)) {
                if (status.getNextVersionable() != null) {
                    path = release.absolutePath(status.getNextVersionable().getRelativePath());
                } else if (status.getPreviousVersionable() != null) {
                    path = release.absolutePath(status.getPreviousVersionable().getRelativePath());
                }
            }
            path = StringUtils.removeEnd(path, '/' + JCR_CONTENT);
        }
        return path;
    }

    /**
     * Returns the service URL to view this content version: empty for normal pages (path+'.html'),
     */
    @Nonnull
    public abstract String getViewerUrl();

    /**
     * Returns the complete URL to show this version,
     */
    @Nonnull
    public abstract String getPreviewUrl();

    public abstract String getTitle();

    /**
     * @return the workspace resource of the staging resource of the referenced version
     */
    public Resource getResource() {
        Resource resource = status.getWorkspaceResource();
        if (resource != null) {
            VersionReference reference = status.getVersionReference();
            String uuid;
            if (reference != null && (uuid = reference.getReleasedVersionable().getVersionUuid()) != null) {
                try {
                    resource = getVersionsService().historicalVersion(context.getResolver(), status.getPath(), uuid);
                } catch (RepositoryException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
        }
        return resource;
    }

    public String getLastModifiedString() {
        Calendar lastModified = status.getLastModified();
        return null != lastModified ? new SimpleDateFormat(VERSION_DATE_FORMAT).format(lastModified.getTime()) : null;
    }

    protected VersionsService getVersionsService() {
        if (versionsService == null) {
            versionsService = context.getService(VersionsService.class);
        }
        return versionsService;
    }

    protected PlatformVersionsService getPlatformVersionsService() {
        if (platformVersionsService == null) {
            platformVersionsService = context.getService(PlatformVersionsService.class);
        }
        return platformVersionsService;
    }

    @Nonnull
    public SiteManager getSiteManager() {
        if (siteManager == null) {
            siteManager = Objects.requireNonNull(context.getService(SiteManager.class));
        }
        return siteManager;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ContentVersion.class.getSimpleName() + "[", "]")
                .add("status=" + status)
                .toString();
    }
}
