/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.servlet.search;

import com.composum.sling.platform.staging.search.SearchService;

import java.io.IOException;
import java.util.Collection;

/**
 * the service interface to generate the response for a search result list
 */
public interface SearchResultRenderer {

    /**
     * @return a key to find the right renderer, e.g. 'page.myresult.json'
     * - is used for a request with selectors 'page.myresult' and extension 'json'
     */
    String rendererKey();

    void sendResult(SearchRequest searchRequest, Collection<SearchService.Result> result)
            throws IOException;
}
