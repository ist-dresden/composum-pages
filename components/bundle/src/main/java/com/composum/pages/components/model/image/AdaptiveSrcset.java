/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.components.model.image;

import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

public class AdaptiveSrcset extends AdaptiveImage {

    public AdaptiveSrcset(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public AdaptiveSrcset() {
    }
}
