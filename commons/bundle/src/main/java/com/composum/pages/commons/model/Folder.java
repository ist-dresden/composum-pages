package com.composum.pages.commons.model;

import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

public class Folder extends AbstractModel {

    public Folder() {
    }

    public Folder(BeanContext context, String path, String resourceType) {
        initialize(context, path, resourceType);
    }

    public Folder(BeanContext context, Resource resource) {
        initialize(context, resource);
    }
}
