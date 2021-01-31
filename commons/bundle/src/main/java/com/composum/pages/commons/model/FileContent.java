package com.composum.pages.commons.model;

import org.apache.sling.api.resource.Resource;

import java.util.Calendar;

public interface FileContent {

    String PROP_SHOW_COPYRIGHT = "showCopyright";
    String PROP_COPYRIGHT = "copyright";
    String PROP_COPYRIGHT_URL = "copyrightUrl";
    String PROP_LICENSE = "license";
    String PROP_LICENSE_URL = "licenseUrl";

    String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * @return the files content resource (normally the 'jcr:content' child - of type 'nt:resource')
     */

    Resource getContentResource();

    String getFileName();

    String getFilePath();

    String getDateString();

    Calendar getDate();

    String getMimeType();

    String getMimeTypeCss();

    boolean isShowCopyright();

    String getCopyright();

    String getCopyrightUrl();

    String getLicense();

    String getLicenseUrl();
}
