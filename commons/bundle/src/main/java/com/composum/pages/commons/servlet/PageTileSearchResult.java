package com.composum.pages.commons.servlet;

import com.composum.pages.commons.service.SearchService;
import com.composum.pages.commons.util.ResourceTypeUtil;
import com.composum.sling.core.util.LinkUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

@Component(
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Page Tile Search Result"
        }
)
public class PageTileSearchResult implements SearchResultRenderer {

    private static final Logger LOG = LoggerFactory.getLogger(PageTileSearchResult.class);

    public static final String KEY = "page.tile.html";

    public String rendererKey() {
        return KEY;
    }

    public void sendResult(SearchRequest searchRequest, Collection<SearchService.Result> result)
            throws IOException {
        ResourceResolver resolver = searchRequest.context.getResolver();
        SlingHttpServletRequest request = searchRequest.context.getRequest();
        SlingHttpServletResponse response = searchRequest.context.getResponse();
        RequestPathInfo pathInfo = request.getRequestPathInfo();

        // use additional selectors for rendering of the tiles
        String selectors = pathInfo.getSelectorString();
        selectors = selectors != null && selectors.startsWith("page.tile.") ? selectors.substring(10) : null;

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();
        writer.append("<ul class=\"pages-search-page-result\">");

        for (SearchService.Result item : result) {
            String path = item.getTarget().getPath();
            Resource content = item.getTargetContent();

            // determine the resource type to render a tile of a page
            String editTileResoutrceType = ResourceTypeUtil.getSubtypePath(resolver, content,
                    null, "edit/tile", selectors);

            if (StringUtils.isNotBlank(editTileResoutrceType)) {
                String url = LinkUtil.getUrl(request, path);
                writer.append("<li class=\"pages-search-page-item\"><a href=\"").append(url)
                        .append("\" data-path=\"").append(path).append("\">");

                RequestDispatcherOptions options = new RequestDispatcherOptions();
                options.setForceResourceType(editTileResoutrceType);
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
