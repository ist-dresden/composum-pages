/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.options.assets.model;

import com.composum.assets.commons.AssetsConstants;
import com.composum.assets.commons.config.AssetConfig;
import com.composum.assets.commons.handle.ImageAsset;
import com.composum.assets.commons.util.ImageUtil;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

public class AdaptiveImage extends AssetImage {

    private transient String variation;
    private transient String rendition;

    private transient String imageUri;

    public AdaptiveImage(BeanContext context, Resource resource,
                         String variation, String rendition, AssetConfig config) {
        this(context, resource);
        this.variation = variation;
        this.rendition = rendition;
    }

    public AdaptiveImage(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public AdaptiveImage() {
    }

    public String getVariation() {
        if (variation == null) {
            variation = getProperty(AssetsConstants.VARIATION, "");
        }
        return variation;
    }

    public String getRendition() {
        if (rendition == null) {
            rendition = getProperty(AssetsConstants.RENDITION, "");
        }
        return rendition;
    }

    @Override
    public String getImageUri() {
        if (imageUri == null) {
            imageUri = getImageUri(getVariation(), getRendition());
        }
        return imageUri;
    }

    public String getImageUri(String variationKey, String renditionKey) {
        String uri = null;
        ImageAsset asset = getAsset();
        if (asset != null) {
            uri = ImageUtil.getImageUri(asset, variationKey, renditionKey);
        }
        return uri;
    }
}
