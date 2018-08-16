package com.composum.pages.commons.taglib;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

public class DefineFrameObjectsTag extends DefineObjectsTag {

    @Override
    protected Resource determineResource(SlingHttpServletRequest request) {
        Resource resource = null;
        String path = request.getRequestPathInfo().getSuffix();
        if (StringUtils.isNotBlank(path)) {
            resource = request.getResourceResolver().resolve(path);
        }
        if (resource == null) {
            resource = request.getResource();
        }
        return resource;
    }
}
