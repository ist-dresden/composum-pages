package com.composum.pages.commons.model;

import com.composum.pages.commons.util.LinkUtil;
import com.composum.sling.core.BeanContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public class AssetRelated extends Element {

    private static final Logger LOG = LoggerFactory.getLogger(AssetRelated.class);

    public static final String PROP_ASSET_REF = "assetRef";

    private transient String src;

    private transient String assetRef;
    private transient String assetUrl;

    protected transient Boolean valid;
    private transient File assetFile;

    private transient String copyright;
    private transient String copyrightUrl;
    private transient String license;
    private transient String licenseUrl;

    public AssetRelated() {
    }

    public AssetRelated(BeanContext context, Resource resource) {
        super(context, resource);
    }

    protected String getRefPropName() {
        return PROP_ASSET_REF;
    }

    public boolean isValid() {
        if (valid == null) {
            valid = StringUtils.isNotBlank(getAssetUrl());
        }
        return valid;
    }

    public String getAssetRef() {
        if (assetRef == null) {
            assetRef = getProperty(getRefPropName(), "");
        }
        return assetRef;
    }

    public String getAssetUrl() {
        if (assetUrl == null) {
            assetUrl = getAssetRef();
            if (StringUtils.isNotBlank(assetUrl)) {
                assetUrl = LinkUtil.getUrl(context.getRequest(), assetUrl);
            }
        }
        return assetUrl;
    }

    protected String getPlaceholder() {
        return getProperty(PROP_PLACEHOLDER, "");
    }

    public String getMimeType() {
        File file = getAssetFile();
        return file != null ? file.getMimeType() : "";
    }

    public File getAssetFile() {
        if (assetFile == null) {
            String imageRef = getAssetRef();
            if (StringUtils.isNotBlank(imageRef)) {
                assetFile = new File(getContext(), imageRef, null);
            }
        }
        return assetFile;
    }

    public InputStream openInputStream() {
        File file = getAssetFile();
        if (file != null && file.isValid()) {
            return file.getFileHandle().getStream();
        }
        return null;
    }

    public boolean isWithMetaInfo() {
        return StringUtils.isNotBlank(getCopyright())
                || StringUtils.isNotBlank(getLicense())
                || StringUtils.isNotBlank(getLicenseUrl());
    }

    public String getCopyright() {
        if (copyright == null) {
            copyright = getProperty(FileResource.PROP_COPYRIGHT, "");
            if (StringUtils.isBlank(copyright)) {
                File file = getAssetFile();
                if (file != null && file.isShowCopyright()) {
                    copyright = file.getCopyright();
                }
            }
        }
        return copyright;
    }

    public String getCopyrightUrl() {
        if (copyrightUrl == null) {
            String uri = getProperty(FileResource.PROP_COPYRIGHT_URL, "");
            if (StringUtils.isBlank(uri)) {
                File file = getAssetFile();
                if (file != null && file.isShowCopyright()) {
                    uri = file.getCopyrightUrl();
                }
            }
            copyrightUrl = StringUtils.isNotBlank(uri) ? LinkUtil.getUrl(getContext().getRequest(), uri) : "";
        }
        return copyrightUrl;
    }

    public String getLicense() {
        if (license == null) {
            license = getProperty(FileResource.PROP_LICENSE, "");
            if (StringUtils.isBlank(license)) {
                if (StringUtils.isBlank(license)) {
                    File file = getAssetFile();
                    if (file != null && file.isShowCopyright()) {
                        license = file.getLicense();
                    }
                }
                if (StringUtils.isBlank(license)) {
                    license = getLicenseUrl();
                }
            }
        }
        return license;
    }

    public String getLicenseUrl() {
        if (licenseUrl == null) {
            String uri = getProperty(FileResource.PROP_LICENSE_URL, "");
            if (StringUtils.isBlank(uri)) {
                File file = getAssetFile();
                if (file != null && file.isShowCopyright()) {
                    uri = file.getLicenseUrl();
                }
            }
            licenseUrl = StringUtils.isNotBlank(uri) ? LinkUtil.getUrl(getContext().getRequest(), uri) : "";
        }
        return licenseUrl;
    }
}
