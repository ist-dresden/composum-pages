package com.composum.pages.options.assets.service;

import com.composum.assets.commons.AssetsConstants;
import com.composum.assets.commons.config.AssetConfig;
import com.composum.pages.commons.model.ContentVersion;
import com.composum.pages.commons.model.SiteRelease;
import com.composum.pages.commons.service.VersionFactory;
import com.composum.pages.options.assets.api.AssetContent;
import com.composum.pages.options.assets.api.AssetVersion;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.platform.staging.versions.PlatformVersionsService;
import org.apache.sling.api.resource.Resource;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Component(
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Assets Version Factory"
        },
        immediate = true
)
public class AssetsVersionFactory implements VersionFactory {

    @Override
    @Nullable
    public ContentVersion<AssetContent> getContentVersion(@Nonnull final SiteRelease siteRelease,
                                                          @Nonnull final Resource contentResource, @Nonnull final String type,
                                                          @Nonnull final PlatformVersionsService.Status status) {
        boolean isAssetsRelated;
        switch (type) {
            case AssetsConstants.NODE_TYPE_ASSET:
            case AssetsConstants.NODE_TYPE_ASSET_CONTENT:
                isAssetsRelated = true;
                break;
            default:
                Resource assetsConfig = contentResource.getChild(AssetConfig.CHILD_NAME);
                isAssetsRelated = ResourceUtil.isNodeType(assetsConfig, AssetsConstants.NODE_TYPE_ASSET_CONFIG);
                break;
        }
        return isAssetsRelated ? new AssetVersion(siteRelease, status) : null;
    }
}
