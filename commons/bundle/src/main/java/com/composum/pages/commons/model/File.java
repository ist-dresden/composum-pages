package com.composum.pages.commons.model;

import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;

public class File extends AbstractModel {

    /**
     * check the 'nt:file' type for a resource
     */
    public static boolean isFile(Resource resource) {
        return ResourceUtil.isResourceType(resource, JcrConstants.NT_FILE);
    }

    public File() {
    }

    public File(BeanContext context, String path, String resourceType) {
        initialize(context, path, resourceType);
    }

    public File(BeanContext context, Resource resource) {
        initialize(context, resource);
    }
}
