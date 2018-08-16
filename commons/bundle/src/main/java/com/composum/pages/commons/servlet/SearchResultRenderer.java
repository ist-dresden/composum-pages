package com.composum.pages.commons.servlet;

import com.composum.pages.commons.service.SearchService;

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
