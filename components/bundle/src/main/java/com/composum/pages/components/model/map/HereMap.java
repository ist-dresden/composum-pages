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
import java.nio.charset.StandardCharsets;

public class HereMap extends Element {

    private static final Logger LOG = LoggerFactory.getLogger(CoreAdapterFactory.class);

    public static final String PN_COUNTRY = "country";
    public static final String PN_CITY = "city";
    public static final String PN_ZIP = "zip";
    public static final String PN_STREET = "street";
    public static final String PN_NUMBER = "number";
    public static final String PN_WIDTH = "width";
    public static final String PN_HEIGHT = "height";
    public static final String PN_TYPE = "type";
    public static final String PN_ZOOM = "zoom";

    public static final String SP_API_ = "here/api/";
    public static final String SP_API_KEY = SP_API_ + "key";

    public static final String SP_MAPVIEW_URL = SP_API_ + "mapviewUrl";
    public static final String DEFAULT_MAPVIEW_URL = "https://image.maps.ls.hereapi.com/mia/1.6/mapview";

    private transient String country;
    private transient String city;
    private transient String zip;
    private transient String street;
    private transient String number;
    private transient String[] parameters;

    private transient String apiKey;
    private transient String mapviewUrl;

    public String getMapviewUrl() {
        if (mapviewUrl == null) {
            StringBuilder url = new StringBuilder();
            Page currentPage = getCurrentPage();
            String apiKey = getApiKey();
            if (StringUtils.isNotBlank(apiKey) && currentPage != null) {
                url.append(currentPage.getSettingsProperty(SP_MAPVIEW_URL, null, DEFAULT_MAPVIEW_URL));
                url.append("?apiKey=").append(apiKey);
                String value;
                if (StringUtils.isNotBlank(value = getProperty(PN_COUNTRY, String.class))) {
                    url.append("&co=").append(URLEncoder.encode(value, StandardCharsets.UTF_8));
                }
                if (StringUtils.isNotBlank(value = getProperty(PN_ZIP, String.class))) {
                    url.append("&zi=").append(URLEncoder.encode(value, StandardCharsets.UTF_8));
                }
                if (StringUtils.isNotBlank(value = getProperty(PN_CITY, String.class))) {
                    url.append("&ci=").append(URLEncoder.encode(value, StandardCharsets.UTF_8));
                }
                if (StringUtils.isNotBlank(value = getProperty(PN_STREET, String.class))) {
                    url.append("&s=").append(URLEncoder.encode(value, StandardCharsets.UTF_8));
                }
                if (StringUtils.isNotBlank(value = getProperty(PN_NUMBER, String.class))) {
                    url.append("&n=").append(URLEncoder.encode(value, StandardCharsets.UTF_8));
                }
                if (StringUtils.isNotBlank(value = getProperty(PN_WIDTH, "1200"))) {
                    url.append("&w=").append(URLEncoder.encode(value, StandardCharsets.UTF_8));
                }
                if (StringUtils.isNotBlank(value = getProperty(PN_HEIGHT, "600"))) {
                    url.append("&h=").append(URLEncoder.encode(value, StandardCharsets.UTF_8));
                }
                if (StringUtils.isNotBlank(value = getProperty(PN_ZOOM, "13"))) {
                    url.append("&z=").append(URLEncoder.encode(value, StandardCharsets.UTF_8));
                }
                if (StringUtils.isNotBlank(value = getProperty(PN_TYPE, "0"))) {
                    url.append("&t=").append(URLEncoder.encode(value, StandardCharsets.UTF_8));
                }
                url.append("&f=0");
            }
            mapviewUrl = url.toString();
        }
        return mapviewUrl;
    }

    public String getApiKey() {
        if (apiKey == null) {
            apiKey = "";
            Page currentPage = getCurrentPage();
            if (currentPage != null) {
                apiKey = currentPage.getSettingsProperty(SP_API_KEY, null, "");
            }
        }
        return apiKey;
    }

    public static String getMapviewUrl(Page currentPage, String location) {
        try {
            if (StringUtils.isNotBlank(location)) {
                return currentPage.getSettingsProperty(SP_MAPVIEW_URL, null, DEFAULT_MAPVIEW_URL) + "?q=" + URLEncoder.encode(location, "UTF-8");
            }
        } catch (UnsupportedEncodingException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return "";
    }
}
