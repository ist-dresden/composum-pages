package com.composum.pages.commons.util;

import com.composum.sling.core.util.LinkUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RichTextUtil {

    public static final Pattern CONTENT_LINK_PATTERN =
            Pattern.compile("(<a\\s+[^>]*href\\s*=\\s*[\"'])(/[^\"']+)([\"'][^>]*>)");

    /**
     * build the value to render of a rich text property
     * @param request the current request to determine the domain context
     * @param text the text value
     * @return the value transformed for rendering
     */
    public static String prepareRichText(SlingHttpServletRequest request, String text) {
        // ensure that a rich text value is always enclosed with a HTML paragraph tag
        if (StringUtils.isNotBlank(text) && !text.trim().startsWith("<p>")) {
            text = "<p>" + text + "</p>";
        }
        // transform embedded resource links (paths) to mapped URLs
        text = prepareContentLinks(request, text);
        return text;
    }

    /**
     * transforms each linked resource path of a text value into the mapped URL to the path
     * @param request the current request to determine the domain context
     * @param text the text value (rich text with probably embedded links)
     * @return the text value with transformed resource links
     */
    public static String prepareContentLinks(SlingHttpServletRequest request, String text) {
        StringBuilder buffer = new StringBuilder();
        Matcher matcher = CONTENT_LINK_PATTERN.matcher(text);
        int len = text.length();
        int pos = 0;
        while (matcher.find(pos)) {
            String unmapped = matcher.group(2);
            String mapped = LinkUtil.getUrl(request, unmapped);
            buffer.append(text, pos, matcher.start());
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
