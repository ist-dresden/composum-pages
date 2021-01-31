/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.options.assets.api;

import com.composum.assets.commons.handle.ImageAsset;
import com.composum.pages.commons.model.ContentDriven;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;

public class Asset extends ContentDriven<AssetContent> {

    private transient ImageAsset imageAsset;

    /**
     * check the 'asset' type for a resource
     */
    public static boolean isAsset(Resource resource) {
        return ResourceUtil.isResourceType(resource, "cpa:Asset");
    }

    public Asset() {
    }

    public Asset(BeanContext context, String path, String resourceType) {
        initialize(context, path, resourceType);
    }

    public Asset(BeanContext context, Resource resource) {
        initialize(context, resource);
    }

    @Nonnull
    @Override
    protected AssetContent createContentModel(BeanContext context, Resource contentResource) {
        return new AssetContent(context, contentResource);
    }

    public ImageAsset getImageAsset() {
        if (imageAsset == null) {
            imageAsset = new ImageAsset(context, getResource());
        }
        return imageAsset;
    }
}
