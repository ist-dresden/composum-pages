/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.components.model.image;

import com.composum.sling.core.BeanContext;
import com.composum.pages.commons.util.LinkUtil;
import org.apache.sling.api.resource.Resource;

public class SimpleImage extends AbstractImage {

    private transient String imagePath;

    public SimpleImage(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public SimpleImage() {
    }

    public String getImageUrl() {
        String uri = getImagePath();
        return LinkUtil.getUrl(context.getRequest(), uri);
    }

    public String getImageUri() {
        return getImagePath();
    }

    public String getImagePath() {
        if (imagePath == null) {
            imagePath = getProperty("imagePath", "");
        }
        return imagePath;
    }
}
