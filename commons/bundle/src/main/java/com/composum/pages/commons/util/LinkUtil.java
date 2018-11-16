package com.composum.pages.commons.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkUtil extends com.composum.sling.core.util.LinkUtil {

    public static final Pattern URL_TEXT_PATTERN = Pattern.compile("(https?://([^/:]+)(:\\d+)?)?((/[^?#;]*)(.*)?)?$");

    @Nonnull
    public static String toText(@Nonnull final SlingHttpServletRequest request, @Nullable final String url) {
        if (StringUtils.isNotBlank(url)) {
            String mappedUrl = getMappedUrl(request, url);
            Matcher matcher = URL_TEXT_PATTERN.matcher(mappedUrl);
            if (matcher.matches()) {
                String host = matcher.group(2);
                String path = matcher.group(5);
                StringBuilder text = new StringBuilder();
                if (StringUtils.isNotBlank(host)) {
                    text.append(host);
                }
                if (StringUtils.isNotBlank(path) && !"/".equals(path = path.trim())) {
                    text.append(path);
                }
                return text.toString();
            } else {
                return mappedUrl;
            }
        }
        return "";
    }
}
