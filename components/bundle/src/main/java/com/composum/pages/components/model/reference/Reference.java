package com.composum.pages.components.model.reference;

import com.composum.pages.commons.model.Element;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;

import static com.composum.pages.commons.PagesConstants.PN_CONTENT_REFERENCE;

public class Reference extends Element {

    private transient Boolean valid;
    private transient String contentReference;

    public boolean isValid() {
        if (valid == null) {
            String path = getContentReference();
            valid = StringUtils.isNotBlank(path) && getContext().getResolver().getResource(path) != null;
        }
        return valid;
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
