package com.composum.pages.options.assets.api;

import com.composum.pages.commons.model.ContentVersion;
import com.composum.pages.commons.model.SiteRelease;
import com.composum.sling.platform.staging.versions.PlatformVersionsService;
import org.apache.sling.api.resource.Resource;

public class AssetVersion extends ContentVersion<AssetContent> {

    private transient Asset asset;

    public AssetVersion(SiteRelease siteRelease, PlatformVersionsService.Status status) {
        super(siteRelease, status);
    }

    @Override
    public AssetContent getContent() {
        Asset asset = getAsset();
        if (asset != null) {
            return asset.getContent();
        }
        return null;
    }

    public Asset getAsset() {
        if (asset == null) {
            Resource resource = status.getWorkspaceResource();
            if (resource != null) {
                asset = new Asset(context, resource);
            }
        }
        return asset;
    }

    @Override
    public String getTitle() {
        Asset asset = getAsset();
        if (asset != null) {
            return asset.getTitle();
        }
        return null;
    }

    /**
     * Returns the URL to reference this page version: if this is about workspace, the page path (null if the page is deleted),
     */
    @Override
    public String getUrl() {
        Asset asset = getAsset();
        if (asset != null) {
            return asset.getUrl();
        }
        return null;
    }
}
