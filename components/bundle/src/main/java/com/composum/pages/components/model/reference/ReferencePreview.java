package com.composum.pages.components.model.reference;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;

public class ReferencePreview extends Reference {

    @Nonnull
    public String retrieveContentReference() {
        SlingHttpServletRequest request = context.getRequest();
        if (request != null) {
            String suffix = request.getRequestPathInfo().getSuffix();
            if (StringUtils.isNotBlank(suffix)) {
                Resource resource = context.getResolver().getResource(suffix);
                if (resource != null) {
                    return resource.getPath();
                }
            }
        }
        return super.retrieveContentReference();
    }
}
