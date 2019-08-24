/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.options.assets.model;

import com.composum.assets.commons.AssetsConstants;
import com.composum.assets.commons.config.AssetConfig;
import com.composum.assets.commons.config.RenditionConfig;
import com.composum.assets.commons.config.VariationConfig;
import com.composum.assets.commons.handle.ImageAsset;
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
            variation = getProperty(AssetsConstants.PROP_VARIATION, "");
        }
        return variation;
    }

    public String getRendition() {
        if (rendition == null) {
            rendition = getProperty(AssetsConstants.PROP_RENDITION, "");
        }
        return rendition;
    }

    public String getImageUri() {
        if (imageUri == null) {
            imageUri = getImageUri(getVariation(), getRendition());
        }
        return imageUri;
    }

    public String getImageUri(String variationKey, String renditionKey) {
        StringBuilder builder = new StringBuilder();
        ImageAsset asset = getAsset();
        if (asset != null) {
            String mimeType = asset.getMimeType();
            if (mimeType != null) {
                AssetConfig config = asset.getConfig();
                VariationConfig variation = config.findVariation(variationKey);
                RenditionConfig rendition = variation.findRendition(renditionKey);
                String path = asset.getPath();
                String ext = mimeType.substring("image/".length());
                if (ext.equals("jpeg")) {
                    ext = "jpg";
                }
                if (path.endsWith("." + ext)) {
                    path = path.substring(0, path.length() - (ext.length() + 1));
                }
                String name = path.substring(path.lastIndexOf('/') + 1);
                builder.append(path);
                builder.append(".adaptive");
                builder.append('.').append(variation.getName());
                builder.append('.').append(rendition.getName());
                builder.append('.').append(ext);
                builder.append('/').append(getCacheHash());
                builder.append('/').append(name);
                builder.append('.').append(ext);
            }
        }
        return builder.toString();
    }
}
