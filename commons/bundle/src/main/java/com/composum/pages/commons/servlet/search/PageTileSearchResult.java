/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.servlet.search;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

/**
 * the rendering strategy for a search result list which is generating an HTML list with links to the pages and
 * page information rendered by delegation to the page itself using the subtype 'edit/tile' of the page
 */
@Component(
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Page Tile Search Result"
        }
)
public class PageTileSearchResult extends AbstractTileSearchResult implements SearchResultRenderer {

    public static final String TYPE = "page";
    public static final String KEY = TYPE + ".tile.html";

    public String rendererKey() {
        return KEY;
    }

    @Override
    protected String getType() {
        return TYPE;
    }
}
