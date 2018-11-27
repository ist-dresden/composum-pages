/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.components.model.map;

import com.composum.pages.commons.model.Element;
import com.composum.pages.commons.model.Page;
import com.composum.sling.core.CoreAdapterFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class GoogleMap extends Element {

    private static final Logger LOG = LoggerFactory.getLogger(CoreAdapterFactory.class);

    public static final String PN_LOCATION = "location";
    public static final String PN_PARAMETERS = "parameters";

    public static final String SP_API_ = "google/api/";
    public static final String SP_API_KEY = SP_API_ + "key";

    public static final String SP_MAPS_URL = SP_API_ + "mapsUrl";
    public static final String DEFAULT_MAPS_URL = "https://maps.google.com/maps";

    public static final String SP_EMBED_URL = SP_API_ + "embedUrl";
    public static final String DEFAULT_EMBED_URL = "https://www.google.com/maps/embed/v1/place";

    private transient String frameUrl;

    private transient String apiKey;
    private transient String location;
    private transient String[] parameters;

    public String getFrameUrl() {
        if (frameUrl == null) {
            StringBuilder url = new StringBuilder();
            String apiKey = getApiKey();
            if (StringUtils.isNotBlank(apiKey)) {
                String location = getLocation();
                String[] parameters = getParameters();
                url.append(getCurrentPage().getSettingsProperty(SP_EMBED_URL, null, DEFAULT_EMBED_URL));
                url.append("?key=").append(apiKey);
                try {
                    if (StringUtils.isNotBlank(location)) {
                        url.append("&q=").append(URLEncoder.encode(location, "UTF-8"));
                    }
                    for (String parameter : parameters) {
                        url.append("&").append(parameter);
                    }
                } catch (UnsupportedEncodingException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
            frameUrl = url.toString();
        }
        return frameUrl;
    }

    public String getApiKey() {
        if (apiKey == null) {
            apiKey = getCurrentPage().getSettingsProperty(SP_API_KEY, null, "");
        }
        return apiKey;
    }

    public String getLocation() {
        if (location == null) {
            location = getProperty(PN_LOCATION, null, "");
        }
        return location;
    }

    public String[] getParameters() {
        if (parameters == null) {
            parameters = getProperty(PN_PARAMETERS, null, new String[0]);
        }
        return parameters;
    }

    public static String getMapsUrl(Page currentPage, String location) {
        try {
            if (StringUtils.isNotBlank(location)) {
                return currentPage.getSettingsProperty(SP_MAPS_URL, null, DEFAULT_MAPS_URL) + "?q=" + URLEncoder.encode(location, "UTF-8");
            }
        } catch (UnsupportedEncodingException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return "";
    }
}
