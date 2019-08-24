/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.model;

import com.composum.sling.clientlibs.handle.FileHandle;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;

public class File extends ContentDriven<FileResource> {

    enum Type {asset, document, file, image, video}

    /**
     * check the 'nt:file' or 'asset' type for a resource
     */
    public static boolean isFile(Resource resource) {
        return ResourceUtil.isResourceType(resource, JcrConstants.NT_FILE)
                || ResourceUtil.isResourceType(resource, "cpa:Asset");
    }

    public File() {
    }

    public File(BeanContext context, String path, String resourceType) {
        initialize(context, path, resourceType);
    }

    public File(BeanContext context, Resource resource) {
        initialize(context, resource);
    }

    @Override
    protected FileResource createContentModel(BeanContext context, Resource contentResource) {
        return new FileResource(context, contentResource);
    }

    public Type getFileType() {
        return getContent().getFileType();
    }

    public String getFileDate() {
        return getContent().getDateString();
    }

    public FileHandle getFileHandle() {
        return new FileHandle(getResource());
    }

    public String getMimeType() {
        return getContent().getMimeType();
    }

    public String getMimeTypeCss() {
        return getContent().getMimeTypeCss();
    }

    public boolean isShowCopyright() {
        return getContent().isShowCopyright();
    }

    public String getCopyright() {
        return getContent().getCopyright();
    }

    public String getCopyrightUrl() {
        return getContent().getCopyrightUrl();
    }

    public String getLicense() {
        return getContent().getLicense();
    }

    public String getLicenseUrl() {
        return getContent().getLicenseUrl();
    }
}
