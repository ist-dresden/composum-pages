/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkUtil extends com.composum.sling.core.util.LinkUtil {

    private static final Logger LOG = LoggerFactory.getLogger(LinkUtil.class);

    public static final Pattern URL_TEXT_PATTERN = Pattern.compile(
            "^(https?://([^/:]+)(:\\d+)?)?((/[^?#;]*)(.*)?)?$", Pattern.CASE_INSENSITIVE);
    public static final Pattern SPECIAL_URL_PATTERN = Pattern.compile(
            "^(mailto|tel):(.+)$", Pattern.CASE_INSENSITIVE);

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
            } else if ((matcher = SPECIAL_URL_PATTERN.matcher(mappedUrl)).matches()) {
                return matcher.group(2);
            } else {
                return mappedUrl;
            }
        }
        return "";
    }

    public static class Parameters {

        protected Map<String, List<String>> set;

        public Parameters() {
            set = new LinkedHashMap<>();
        }

        public Parameters(@Nullable String queryString) {
            this();
            if (queryString != null) {
                if (queryString.startsWith("?")) {
                    queryString = queryString.substring(1);
                }
                for (String part : StringUtils.split(queryString, "&")) {
                    String[] pair = StringUtils.split(part, "=", 2);
                    try {
                        add(pair[0], pair.length > 1 ? URLDecoder.decode(pair[1], StandardCharsets.UTF_8.name()) : null);
                    } catch (UnsupportedEncodingException ex) {
                        LOG.error(ex.toString());
                    }
                }
            }
        }

        public Parameters(@Nonnull final SlingHttpServletRequest request) {
            this();
            for (Map.Entry<String, RequestParameter[]> entry : request.getRequestParameterMap().entrySet()) {
                set(entry.getKey(), (Object[]) entry.getValue());
            }
        }

        public void add(@Nonnull final String name, Object... value) {
            List<String> values = set.computeIfAbsent(name, k -> new ArrayList<>());
            if (value != null) {
                for (Object object : value) {
                    try {
                        values.add(object != null
                                ? (object instanceof RequestParameter
                                ? ((RequestParameter) object).getString(StandardCharsets.UTF_8.name())
                                : object.toString()) : null);
                    } catch (UnsupportedEncodingException ex) {
                        LOG.error(ex.toString());
                    }
                }
            }
        }

        @Nullable
        public List<String> set(@Nonnull final String name, Object... value) {
            List<String> values = new ArrayList<>();
            if (value != null) {
                for (Object object : value) {
                    values.add(object != null ? object.toString() : null);
                }
            }
            return set.put(name, values);
        }

        @Override
        @Nonnull
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (Map.Entry<String, List<String>> entry : set.entrySet()) {
                String name = entry.getKey();
                List<String> values = entry.getValue();
                if (values.size() > 0) {
                    for (String value : values) {
                        append(builder, name, value);
                    }
                } else {
                    append(builder, name, null);
                }
            }
            return builder.toString();
        }

        protected void append(@Nonnull final StringBuilder builder,
                              @Nonnull final String name, @Nullable final String value) {
            builder.append(builder.length() == 0 ? '?' : '&');
            try {
                builder.append(URLEncoder.encode(name, StandardCharsets.UTF_8.name()));
                if (value != null) {
                    builder.append('=').append(URLEncoder.encode(value, StandardCharsets.UTF_8.name()));
                }
            } catch (UnsupportedEncodingException ex) {
                LOG.error(ex.toString());
            }
        }
    }
}
