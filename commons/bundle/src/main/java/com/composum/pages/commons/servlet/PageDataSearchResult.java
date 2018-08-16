package com.composum.pages.commons.servlet;

import com.composum.pages.commons.service.SearchService;
import com.composum.pages.commons.service.search.SearchTermParseException;
import com.composum.sling.core.filter.StringFilter;
import com.composum.sling.core.util.LinkUtil;
import com.google.gson.stream.JsonWriter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;

/**
 * the rendering strategy for a JSON array search result list with data about the found pages and the page properties
 */
@Component(
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Page Data Search Result"
        }
)
public class PageDataSearchResult implements SearchResultRenderer {

    private static final Logger LOG = LoggerFactory.getLogger(PageDataSearchResult.class);

    public static final String KEY = "page.data.json";

    public static final StringFilter PROPERTY_FILTER = new StringFilter.BlackList("^jcr:.*([Vv]ersion|pred)");

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    public String rendererKey() {
        return KEY;
    }

    public void sendResult(SearchRequest searchRequest, Collection<SearchService.Result> result)
            throws IOException {
        SlingHttpServletRequest request = searchRequest.context.getRequest();
        SlingHttpServletResponse response = searchRequest.context.getResponse();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        JsonWriter writer = new JsonWriter(response.getWriter());
        writer.beginObject();
        writer.name("path").value(searchRequest.rootPath);
        writer.name("term").value(searchRequest.term);
        writer.name("offset").value(searchRequest.offset);
        writer.name("limit").value(searchRequest.limit);
        writer.name("size").value(result.size());
        writer.name("result").beginArray();
        for (SearchService.Result item : result) {
            writer.beginObject();
            Resource target = item.getTarget();
            Resource content = item.getTargetContent();
            ValueMap values = content.getValueMap();
            String path = target.getPath();
            writer.name("name").value(target.getName());
            writer.name("path").value(path);
            writer.name("type").value(content.getResourceType());
            writer.name("url").value(LinkUtil.getUrl(request, path));
            writer.name("score").value(item.getScore());
            writer.name("title").value(item.getTitle());
            try {
                writer.name("excerpt").value(item.getExcerpt());
            } catch (SearchTermParseException ex) {
                LOG.info(ex.getMessage());
            }
            writer.name("properties").beginObject();
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                String name = entry.getKey();
                if (PROPERTY_FILTER.accept(name)) {
                    Object value = entry.getValue();
                    if (value instanceof String) {
                        writer.name(name).value((String) value);
                    } else if (value instanceof Number) {
                        writer.name(name).value((Number) value);
                    } else if (value instanceof Boolean) {
                        writer.name(name).value((Boolean) value);
                    } else if (value instanceof Calendar) {
                        writer.name(name).value(
                                new SimpleDateFormat(DATE_FORMAT).format(((Calendar) value).getTime()));
                    } else if (value instanceof String[]) {
                        writer.name(name).beginArray();
                        for (String val : (String[]) value) {
                            writer.value(val);
                        }
                        writer.endArray();
                    }
                }
            }
            writer.endObject();
            writer.endObject();
        }
        writer.endArray();
        writer.endObject();
    }
}
