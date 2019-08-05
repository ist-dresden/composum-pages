package com.composum.pages.commons.model;

import com.composum.pages.commons.model.File.Type;
import com.composum.pages.commons.util.LinkUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.MimeTypeUtil;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FileResource extends ContentModel<File> {

    public static final Map<String, Type> TYPE_MAP;

    static {
        TYPE_MAP = new HashMap<>();
        TYPE_MAP.put("image", Type.image);
        TYPE_MAP.put("video", Type.video);
        TYPE_MAP.put("application/pdf", Type.document);
        TYPE_MAP.put("cpa:Asset", Type.asset);
    }

    public static final String PROP_SHOW_COPYRIGHT = "showCopyright";
    public static final String PROP_COPYRIGHT = "copyright";
    public static final String PROP_COPYRIGHT_URL = "copyrightUrl";
    public static final String PROP_LICENSE = "license";
    public static final String PROP_LICENSE_URL = "licenseUrl";

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private transient Type fileType;
    private transient String mimeType;
    private transient Calendar date;

    private transient Boolean showCopyright;
    private transient String copyright;
    private transient String copyrightUrl;
    private transient String license;
    private transient String licenseUrl;

    public FileResource() {
    }

    public FileResource(BeanContext context, Resource resource) {
        initialize(context, resource);
    }

    @Nonnull
    public Resource getFileResource(){
        return Objects.requireNonNull(getResource().getParent());
    }

    public String getFileName(){
        return getFileResource().getName();
    }

    public String getFilePath(){
        return getFileResource().getPath();
    }

    public Type getFileType() {
        if (fileType == null) {
            Resource resource = getResource();
            String primaryType = ResourceUtil.getPrimaryType(resource);
            if (StringUtils.isNotBlank(primaryType)) {
                fileType = TYPE_MAP.get(primaryType);
            }
            if (fileType == null) {
                String mimeType = getMimeType();
                fileType = TYPE_MAP.get(getMimeType());
                if (fileType == null) {
                    String category = StringUtils.substringBefore(mimeType, "/");
                    fileType = TYPE_MAP.get(category);
                }
            }
            if (fileType == null) {
                fileType = Type.file;
            }
        }
        return fileType;
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
            mimeType = MimeTypeUtil.getMimeType(resource, "");
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
