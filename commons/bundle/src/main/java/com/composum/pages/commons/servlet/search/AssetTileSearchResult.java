/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.servlet.search;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import static com.composum.pages.commons.util.ResourceTypeUtil.DEFAULT_FILE_TILE;

/**
 * the rendering strategy for a search result list which is generating an HTML list with links to the assets and
 * asset information rendered by delegation to the asset itself using the subtype 'edit/tile' of the asset
 */
@Component(
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Asset Tile Search Result"
        }
)
public class AssetTileSearchResult extends AbstractTileSearchResult implements SearchResultRenderer {

    public static final String TYPE = "asset";
    public static final String KEY = TYPE + ".tile.html";

    public String rendererKey() {
        return KEY;
    }

    @Override
    protected String getType() {
        return TYPE;
    }

    /**
     * @return the resource type to render a tile of an item
     */
    @Override
    protected String getTileType(ResourceResolver resolver, Resource content, String selectors) {
        return DEFAULT_FILE_TILE;
    }
}
