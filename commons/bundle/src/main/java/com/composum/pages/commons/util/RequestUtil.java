package com.composum.pages.commons.util;

import com.composum.sling.core.filter.StringFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.wrappers.SlingHttpServletRequestWrapper;

import java.util.ArrayList;
import java.util.List;

public class RequestUtil extends com.composum.sling.core.util.RequestUtil {

    public static class GetWrapper extends SlingHttpServletRequestWrapper {

        public GetWrapper(SlingHttpServletRequest request) {
            super(request);
        }

        @Override
        public String getMethod() {
            return "GET";
        }
    }

    public static List<String> getSelectors(SlingHttpServletRequest request, StringFilter filter, int index) {
        List<String> result = new ArrayList<>();
        String[] selectors = request.getRequestPathInfo().getSelectors();
        for (int i = index; i < selectors.length; i++) {
            if (filter == null || filter.accept(selectors[i])) {
                result.add(selectors[i]);
            }
        }
        return result;
    }

    public static String getSelectorString(SlingHttpServletRequest request, StringFilter filter, int index) {
        return StringUtils.join(getSelectors(request, filter, index), '.');
    }
}
