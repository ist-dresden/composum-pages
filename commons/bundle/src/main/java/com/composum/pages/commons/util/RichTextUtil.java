package com.composum.pages.commons.util;

import com.composum.sling.core.util.LinkUtil;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RichTextUtil {

    public static final Pattern CONTENT_LINK_PATTERN =
            Pattern.compile("(<a\\s+[^>]*href\\s*=\\s*[\"'])(/[^\"']+)([\"'][^>]*>)");

    public static String prepareRichText(SlingHttpServletRequest request, String text) {
        text = prepareContentLinks(request, text);
        return text;
    }

    public static String prepareContentLinks(SlingHttpServletRequest request, String text) {
        StringBuilder buffer = new StringBuilder();
        Matcher matcher = CONTENT_LINK_PATTERN.matcher(text);
        int len = text.length();
        int pos = 0;
        while (matcher.find(pos)) {
            String unmapped = matcher.group(2);
            String mapped = LinkUtil.getUrl(request, unmapped);
            buffer.append(text.substring(pos, matcher.start()));
            buffer.append(matcher.group(1));
            buffer.append(mapped);
            buffer.append(matcher.group(3));
            pos = matcher.end();
        }
        if (pos >= 0 && pos < len) {
            buffer.append(text.substring(pos));
        }
        return buffer.toString();
    }
}
