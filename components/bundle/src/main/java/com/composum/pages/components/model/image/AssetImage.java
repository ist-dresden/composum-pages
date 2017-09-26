/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.components.model.image;

import com.composum.assets.commons.AssetsConstants;
import com.composum.assets.commons.handle.AssetMetaData;
import com.composum.assets.commons.handle.ImageAsset;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import java.util.Calendar;
import java.util.Date;

public abstract class AssetImage extends AbstractImage {

    public static final String ASSET_PATH = "assetPath";

    private transient ImageAsset asset;
    private transient AssetMetaData metaData;

    public AssetImage(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public AssetImage() {
    }

    public ImageAsset getAsset() {
        if (asset == null) {
            String path = getProperty(ASSET_PATH, "");
            if (StringUtils.isNotBlank(path)) {
                Resource assetResource = context.getResource().getResourceResolver().getResource(path);
                if (assetResource != null) {
                    asset = new ImageAsset(context, assetResource);
                }
            }
            if (asset == null) {
                asset = new ImageAsset(context, resource);
            }
        }
        return asset;
    }

    public AssetMetaData getMetaData() {
        if (metaData == null) {
            Resource metaResource = resource.getChild(AssetsConstants.PATH_META);
            metaData = new AssetMetaData(context, ResourceHandle.use(metaResource));
        }
        return metaData;
    }

    public String getAltText() {
        if (altText == null) {
            altText = super.getAltText();
            if (StringUtils.isBlank(altText)) {
                altText = getMetaData().getAltText();
            }
        }
        return altText;
    }

    public String getCacheHash() {
        StringBuilder builder = new StringBuilder("T");
        Calendar lastModified = getAsset().getLastModified();
        if (lastModified != null) {
            builder.append(lastModified.getTimeInMillis());
        } else {
            builder.append(new Date().getTime());
        }
        return builder.toString();
    }
}
