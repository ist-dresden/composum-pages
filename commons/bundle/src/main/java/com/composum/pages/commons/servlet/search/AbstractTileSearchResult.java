/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.servlet.search;

import com.composum.pages.commons.service.search.SearchService;
import com.composum.pages.commons.util.ResourceTypeUtil;
import com.composum.pages.commons.util.LinkUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

/**
 * renders a list of links of the search results with a 'tile' of each result as the link content;
 * the tile is included with the extra selector string appended to the request selector '{type}.tile'
 * (e.g. 'xtra' of the request selector string 'page.tile.xtra') - if no such selector is specified
 * the 'search' selector is set in the forward to the tile include; the tile should be decorated with the
 * necessary meta-data (reference data) for the found item together with the 'draggable' attribute,
 * this must be rendered by the tile itself (this depends probably on the items type or status)
 */
public abstract class AbstractTileSearchResult {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTileSearchResult.class);

    public static final String DEFAULT_TILE_SELECTOR = "search";

    protected abstract String getType();

    /**
     * @return the resource type to render a tile of an item
     */
    protected String getTileType(ResourceResolver resolver, Resource content, String selectors) {
        return ResourceTypeUtil.getSubtypePath(resolver, content, null, "edit/tile", selectors);
    }

    public void sendResult(SearchRequest searchRequest, Collection<SearchService.Result> result)
            throws IOException {
        String type = getType();

        ResourceResolver resolver = searchRequest.context.getResolver();
        SlingHttpServletRequest request = searchRequest.context.getRequest();
        SlingHttpServletResponse response = searchRequest.context.getResponse();
        RequestPathInfo pathInfo = request.getRequestPathInfo();

        // use additional selectors for rendering of the tiles
        String tileSelector = type + ".tile";
        String selectors = pathInfo.getSelectorString();
        selectors = selectors != null && selectors.startsWith(tileSelector + ".")
                ? selectors.substring(tileSelector.length() + 1) : DEFAULT_TILE_SELECTOR;

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();
        writer.append("<ul class=\"pages-search-").append(type).append("-result\">");

        for (SearchService.Result item : result) {
            Resource resource = item.getTarget();
            String path = resource.getPath();
            Resource content = item.getTargetContent();

            // determine the resource type to render a tile of an item
            String tileResourceType = getTileType(resolver, content, selectors);

            if (StringUtils.isNotBlank(tileResourceType)) {
                String url = LinkUtil.getUrl(request, path);
                writer.append("<li class=\"pages-search-").append(type).append("-item\"><a href=\"").append(url)
                        .append("\" data-path=\"").append(path).append("\">");

                RequestDispatcherOptions options = new RequestDispatcherOptions();
                options.setForceResourceType(tileResourceType);
                options.setReplaceSelectors(selectors);

                RequestDispatcher dispatcher = request.getRequestDispatcher(resource, options);
                if (dispatcher != null) {
                    try {
                        dispatcher.include(request, response);
                    } catch (ServletException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }

                writer.append("</a></li>");
            } else {
                LOG.warn("no tile resource type found for: " + path);
            }
        }
        writer.append("</ul>");
    }
}
