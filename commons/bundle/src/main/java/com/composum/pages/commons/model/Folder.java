package com.composum.pages.commons.model;

import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;

public class Folder extends AbstractModel {

    /**
     * check the folder type for a resource
     */
    public static boolean isFolder(Resource resource) {
        return (ResourceUtil.isResourceType(resource, JcrConstants.NT_FOLDER)
                || ResourceUtil.isResourceType(resource, ResourceUtil.TYPE_SLING_FOLDER)
                || ResourceUtil.isResourceType(resource, ResourceUtil.TYPE_SLING_ORDERED_FOLDER))
                && !(Page.isPage(resource) || Site.isSite(resource));
    }

    public Folder() {
    }

    public Folder(BeanContext context, String path, String resourceType) {
        initialize(context, path, resourceType);
    }

    public Folder(BeanContext context, Resource resource) {
        initialize(context, resource);
    }
}
