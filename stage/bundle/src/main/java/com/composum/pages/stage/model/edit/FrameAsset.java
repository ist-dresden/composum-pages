package com.composum.pages.stage.model.edit;

import com.composum.pages.commons.model.ContentDriven;
import com.composum.pages.commons.model.ContentModel;
import com.composum.pages.commons.model.File;
import com.composum.pages.commons.model.Model;
import com.composum.pages.commons.service.SiteManager;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FrameAsset extends FrameModel {

    public class AssetContent extends ContentModel<AssetDriven> {

        public AssetContent(BeanContext context, Resource contentResource) {
            initialize(context, contentResource);
        }
    }

    public class AssetDriven extends ContentDriven<AssetContent> {

        protected AssetDriven(BeanContext context, Resource resource) {
            initialize(context, resource);
        }

        @Nonnull
        @Override
        protected AssetContent createContentModel(BeanContext context, Resource contentResource) {
            return new AssetContent(context, contentResource);
        }
    }

    @Override
    @Nonnull
    protected Model createDelegate(BeanContext context, Resource resource) {
        if (context.getService(SiteManager.class).isAssetsSupport()) {
            if (ResourceUtil.isResourceType(resource, "cpa:Asset")) {
                return new AssetDriven(context, resource);
            }
        }
        return super.createDelegate(context, resource);
    }

    @Nullable
    public File getFile() {
        Model delegate = getDelegate();
        return delegate instanceof File ? (File) delegate : null;
    }

    public AssetDriven getAsset() {
        Model delegate = getDelegate();
        return delegate instanceof AssetDriven ? (AssetDriven) delegate : null;
    }
}
