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

    public static final String SP_LINK_URL = SP_API_ + "linkUrl";
    public static final String DEFAULT_LINK_URL = "https://wego.here.com/directions/mix/";

    public static final String SP_MAPVIEW_URL = SP_API_ + "mapviewUrl";
    public static final String DEFAULT_MAPVIEW_URL = "https://image.maps.ls.hereapi.com/mia/1.6/mapview";

    private transient String country;
    private transient String city;
    private transient String zip;
    private transient String street;
    private transient String number;
    private transient String[] parameters;

    private transient String apiKey;
    private transient String linkUrl;
    private transient String mapviewUrl;

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

    public String getLinkUrl() {
        if (linkUrl == null) {
            StringBuilder url = new StringBuilder();
            Page currentPage = getCurrentPage();
            String apiKey = getApiKey();
            if (StringUtils.isNotBlank(apiKey) && currentPage != null) {
                url.append(currentPage.getSettingsProperty(SP_LINK_URL, null, DEFAULT_LINK_URL));
                addLocationToLink(url);
            }
            linkUrl = url.toString();
        }
        return linkUrl;
    }

    public String getMapviewUrl() {
        if (mapviewUrl == null) {
            StringBuilder url = new StringBuilder();
            Page currentPage = getCurrentPage();
            String apiKey = getApiKey();
            if (StringUtils.isNotBlank(apiKey) && currentPage != null) {
                url.append(currentPage.getSettingsProperty(SP_MAPVIEW_URL, null, DEFAULT_MAPVIEW_URL));
                url.append("?apiKey=").append(apiKey);
                addLocationToMapview(url);
                addImageOptionsToMapview(url);
            }
            mapviewUrl = url.toString();
        }
        return mapviewUrl;
    }

    protected void addLocationToLink(StringBuilder url) {
        addUrlSegment(url, PN_COUNTRY, null);
        addUrlSegment(url, PN_ZIP, null);
        addUrlSegment(url, PN_CITY, null);
        addUrlSegment(url, PN_STREET, null);
        addUrlSegment(url, PN_NUMBER, null);
        addUrlParameter(url,"map", PN_ZOOM, null);
    }

    protected void addLocationToMapview(StringBuilder url) {
        addUrlParameter(url, "co", PN_COUNTRY, null);
        addUrlParameter(url, "zi", PN_ZIP, null);
        addUrlParameter(url, "ci", PN_CITY, null);
        addUrlParameter(url, "s", PN_STREET, null);
        addUrlParameter(url, "n", PN_NUMBER, null);
    }

    protected void addImageOptionsToMapview(StringBuilder url) {
        addUrlParameter(url, "w", PN_WIDTH, "1200");
        addUrlParameter(url, "h", PN_HEIGHT, "600");
        addUrlParameter(url, "z", PN_ZOOM, "13");
        addUrlParameter(url, "t", PN_TYPE, "0");
        url.append("&f=0");
    }

    protected void addUrlSegment(StringBuilder url, String property, String defaultValue) {
        String value = defaultValue != null ? getProperty(property, defaultValue) : getProperty(property, String.class);
        if (value != null) {
            url.append("-").append(value);
        }
    }

    protected void addUrlParameter(StringBuilder url, String name, String property, String defaultValue) {
        String value = defaultValue != null ? getProperty(property, defaultValue) : getProperty(property, String.class);
        if (value != null) {
            url.append(url.indexOf("?") > 0 ? '&' : '?').append(name).append('=').append(value);
        }
    }
}
