package com.composum.pages.commons.model;

import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

public class File extends AbstractModel {

    public File() {
    }

    public File(BeanContext context, String path, String resourceType) {
        initialize(context, path, resourceType);
    }

    public File(BeanContext context, Resource resource) {
        initialize(context, resource);
    }
}
