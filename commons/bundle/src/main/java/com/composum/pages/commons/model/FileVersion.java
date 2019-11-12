package com.composum.pages.commons.model;

import com.composum.sling.platform.staging.versions.PlatformVersionsService;
import org.apache.sling.api.resource.Resource;

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
                file = new File(context, resource);
            }
        }
        return file;
    }

    @Override
    public String getTitle() {
        File file = getFile();
        if (file != null) {
            return file.getTitle();
        }
        return null;
    }

    /**
     * Returns the URL to reference this page version: if this is about workspace, the page path (null if the page is deleted),
     */
    @Override
    public String getUrl() {
        String url = null;
        if (status.getNextRelease() == null) { // workspace page if not deleted
            if (getFile() != null) {
                return getFile().getUrl();
            }
        }
        return null;
    }
}
