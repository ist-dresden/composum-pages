package com.composum.pages.stage.model.edit;

import com.composum.pages.commons.AssetsConfiguration;

import java.util.Collection;

/**
 * a model of a frame component to handle asset resources
 */
public class AssetsModel extends FrameModel {

    public Collection<AssetsConfiguration.ConfigurableFilter> getAssetFilterSet() {
        return getContext().getService(AssetsConfiguration.class).getNodeFilters();
    }
}
