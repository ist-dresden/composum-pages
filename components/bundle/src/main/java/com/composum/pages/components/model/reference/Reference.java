package com.composum.pages.components.model.reference;

import com.composum.pages.commons.model.Element;
import com.composum.pages.commons.model.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;

import static com.composum.pages.commons.PagesConstants.PN_CONTENT_REFERENCE;

public class Reference extends Element {

    private transient Boolean valid;
    private transient String contentReference;
    private transient String includePath;

    public boolean isValid() {
        if (valid == null) {
            String path = getIncludePath();
            valid = StringUtils.isNotBlank(path) && !getPath().startsWith(path); // avoid recursive include;
        }
        return valid;
    }

    @Nonnull
    public String getIncludePath() {
        if (includePath == null) {
            includePath = "";
            String path = getContentReference();
            if (StringUtils.isNotBlank(path)) {
                Resource resource = getResource().getResourceResolver().getResource(path);
                if (resource != null) {
                    if (!Page.isPage(resource) && getPageManager().getContainingPage(context, resource) != null) {
                        includePath = resource.getPath();
                    }
                }
            }
        }
        return includePath;
    }

    @Nonnull
    public String getContentReference() {
        if (contentReference == null) {
            contentReference = retrieveContentReference();
        }
        return contentReference;
    }

    @Nonnull
    public String retrieveContentReference() {
        return getProperty(PN_CONTENT_REFERENCE, "");
    }
}
