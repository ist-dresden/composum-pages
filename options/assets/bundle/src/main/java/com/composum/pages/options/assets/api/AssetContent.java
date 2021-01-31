/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.options.assets.api;

import com.composum.pages.commons.model.ContentModel;
import com.composum.pages.commons.model.FileContent;
import com.composum.pages.commons.model.FileResource;
import com.composum.pages.commons.util.LinkUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.MimeTypeUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AssetContent extends ContentModel<Asset> implements FileContent {

    private transient String mimeType;
    private transient Calendar date;

    private transient Boolean showCopyright;
    private transient String copyright;
    private transient String copyrightUrl;
    private transient String license;
    private transient String licenseUrl;

    public AssetContent() {
    }

    public AssetContent(BeanContext context, Resource resource) {
        initialize(context, resource);
    }

    @Nonnull
    public Resource getAssetResource() {
        return getParent().getResource();
    }

    /**
     * @return the files content resource (normally the 'jcr:content' child - of type 'nt:resource')
     */
    public Resource getContentResource() {
        return getParent().getImageAsset().getOriginal().getContentResource();
    }

    public String getFileName() {
        return getAssetResource().getName();
    }

    public String getFilePath() {
        return getAssetResource().getPath();
    }

    public String getDateString() {
        Calendar date = getDate();
        return date != null ? new SimpleDateFormat(DATE_FORMAT).format(date.getTime()) : "";
    }

    public Calendar getDate() {
        if (date == null) {
            date = getProperty(JcrConstants.JCR_LASTMODIFIED, null, Calendar.class);
            if (date == null) {
                date = getProperty(JcrConstants.JCR_CREATED, null, Calendar.class);
            }
        }
        return date;
    }

    public String getMimeType() {
        if (mimeType == null) {
            mimeType = MimeTypeUtil.getMimeType(getContentResource(), "");
        }
        return mimeType;
    }

    public String getMimeTypeCss() {
        return getMimeType().replace('/', ' ').replace('+', ' ');
    }

    public boolean isShowCopyright() {
        if (showCopyright == null) {
            showCopyright = getInherited(FileResource.PROP_SHOW_COPYRIGHT, null, Boolean.TRUE);
        }
        return showCopyright;
    }

    public String getCopyright() {
        if (copyright == null) {
            copyright = getInherited(FileResource.PROP_COPYRIGHT, null, "");
        }
        return copyright;
    }

    public String getCopyrightUrl() {
        if (copyrightUrl == null) {
            String uri = getInherited(FileResource.PROP_COPYRIGHT_URL, null, "");
            copyrightUrl = StringUtils.isNotBlank(uri) ? LinkUtil.getUrl(getContext().getRequest(), uri) : "";
        }
        return copyrightUrl;
    }

    public String getLicense() {
        if (license == null) {
            license = getInherited(FileResource.PROP_LICENSE, null, "");
            if (StringUtils.isBlank(license)) {
                license = getLicenseUrl();
            }
        }
        return license;
    }

    public String getLicenseUrl() {
        if (licenseUrl == null) {
            String uri = getInherited(FileResource.PROP_LICENSE_URL, null, "");
            licenseUrl = StringUtils.isNotBlank(uri) ? LinkUtil.getUrl(getContext().getRequest(), uri) : "";
        }
        return licenseUrl;
    }
}
