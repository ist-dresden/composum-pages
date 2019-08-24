/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.components.model.image;

import com.composum.pages.commons.model.Element;
import com.composum.sling.core.BeanContext;
import com.composum.pages.commons.util.LinkUtil;
import org.apache.sling.api.resource.Resource;

/**
 *
 */
public abstract class AbstractImage extends Element {

    protected transient String altText;

    public AbstractImage(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public AbstractImage() {
    }

    public abstract String getImageUri();

    public String getImageUrl() {
        String uri = getImageUri();
        return LinkUtil.getUrl(context.getRequest(), uri);
    }

    public String getAltText() {
        if (altText == null) {
            altText = getProperty("altText", "");
        }
        return altText;
    }
}
