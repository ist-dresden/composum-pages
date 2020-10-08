package com.composum.pages.commons.model;

import com.composum.sling.platform.staging.versions.PlatformVersionsService;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;

public class FileVersion extends ContentVersion<FileResource> {

    private transient File file;

    public FileVersion(SiteRelease siteRelease, PlatformVersionsService.Status status) {
        super(siteRelease, status);
    }

    @Override
    public FileResource getContent() {
        File file = getFile();
        if (file != null) {
            return file.getContent();
        }
        return null;
    }

    public File getFile() {
        if (file == null) {
            Resource resource = status.getWorkspaceResource();
            if (resource != null) {
                file = new File(context, resource.isResourceType(
                        JcrConstants.NT_RESOURCE) ? resource.getParent() : resource);
            }
        }
        return file;
    }

    @Override
    public String getTitle() {
        File file = getFile();
        if (file != null) {
            String title = file.getTitle();
            if (StringUtils.isNotBlank(title)) {
                return title;
            }
            return file.getName();
        }
        return null;
    }

    /**
     * Returns the URL to reference this page version: if this is about workspace, the page path (null if the page is deleted),
     */
    @Override
    @Nonnull
    public String getViewerUrl() {
        return "/bin/cpm/pages/stage.preview.file.html";
    }

    @Nonnull
    public String getPreviewUrl() { // no preview on deleted resource
        return status.getWorkspaceResource() != null ? getViewerUrl() + getPath() : null;
    }
}
