package com.composum.pages.commons.servlet;

import com.composum.pages.commons.util.RequestUtil;
import com.composum.sling.core.BeanContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;

/**
 * the stateful object for a single search request
 */
public class SearchRequest {

    public static final String PARAM_BASE = "q.";
    public static final String PARAM_TERM = PARAM_BASE + "term";
    public static final String PARAM_OFFSET = PARAM_BASE + "offset";
    public static final String PARAM_LIMIT = PARAM_BASE + "limit";

    public final BeanContext context;
    public final String rootPath;
    public final String term;
    public final int offset;
    public final Integer limit;

    public SearchRequest(BeanContext context, String rootPath) {
        this.context = context;
        this.rootPath = rootPath;
        SlingHttpServletRequest request = context.getRequest();
        term = RequestUtil.getParameter(request, PARAM_TERM, "");
        offset = RequestUtil.getParameter(request, PARAM_OFFSET, 0);
        limit = RequestUtil.getParameter(request, PARAM_LIMIT, (Integer) null);
    }

    public boolean isValid() {
        return StringUtils.isNotBlank(term);
    }
}
