/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.servlet.search;

import com.composum.pages.commons.service.search.SearchService;
import com.composum.pages.commons.util.ResourceTypeUtil;
import com.composum.sling.core.util.LinkUtil;
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

public abstract class AbstractTileSearchResult {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTileSearchResult.class);

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
        String selectors = pathInfo.getSelectorString();
        selectors = selectors != null && selectors.startsWith("asset.tile.") ? selectors.substring(10) : "";

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();
        writer.append("<ul class=\"pages-search-").append(type).append("-result\">");

        for (SearchService.Result item : result) {
            String path = item.getTarget().getPath();
            Resource content = item.getTargetContent();

            // determine the resource type to render a tile of an item
            String tileResourceType = getTileType(resolver, content, selectors);

            if (StringUtils.isNotBlank(tileResourceType)) {
                String url = LinkUtil.getUrl(request, path);
                writer.append("<li class=\"pages-search-").append(type).append("-item\"><a href=\"").append(url)
                        .append("\" data-path=\"").append(path).append("\">");

                RequestDispatcherOptions options = new RequestDispatcherOptions();
                options.setForceResourceType(tileResourceType);
                if (selectors != null) {
                    options.setReplaceSelectors(selectors);
                }

                RequestDispatcher dispatcher = request.getRequestDispatcher(content, options);
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
