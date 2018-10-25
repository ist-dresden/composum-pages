/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.model;

import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.MimeTypeUtil;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;

import java.util.HashMap;
import java.util.Map;

public class File extends AbstractModel {

    enum Type {asset, document, file, image, video}

    public static final Map<String, Type> TYPE_MAP;

    static {
        TYPE_MAP = new HashMap<>();
        TYPE_MAP.put("image", Type.image);
        TYPE_MAP.put("video", Type.video);
        TYPE_MAP.put("application/pdf", Type.document);
        TYPE_MAP.put("cpa:Asset", Type.asset);
    }

    /**
     * check the 'nt:file' or 'asset' type for a resource
     */
    public static boolean isFile(Resource resource) {
        return ResourceUtil.isResourceType(resource, JcrConstants.NT_FILE)
                || ResourceUtil.isResourceType(resource, "cpa:Asset");
    }

    private transient Type fileType;
    private transient String mimeType;

    public File() {
    }

    public File(BeanContext context, String path, String resourceType) {
        initialize(context, path, resourceType);
    }

    public File(BeanContext context, Resource resource) {
        initialize(context, resource);
    }

    public String getFileName() {
        return (JcrConstants.JCR_CONTENT.equals(getName())) ? getResource().getParent().getName() : getName();
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

    public String getMimeType() {
        if (mimeType == null) {
            mimeType = MimeTypeUtil.getMimeType(resource, "");
        }
        return mimeType;
    }

    public String getMimeTypeCss() {
        return getMimeType().replace('/', ' ').replace('+', ' ');
    }
}
