/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.options.assets.model;

import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

import java.util.ArrayList;
import java.util.List;

public class AdaptivePicture extends AdaptiveSrcset {

    private transient List<String[]> srcSet;

    static {
    }

    public AdaptivePicture(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public AdaptivePicture() {
    }

    public List<String[]> getSrcSet() {
        if (srcSet == null) {
            srcSet = new ArrayList<>();
            srcSet.add(new String[]{"(min-width: 1024px)", getImageUri("wide", "medium"), getImageUri("wide", "large")});
            srcSet.add(new String[]{"(min-width: 640px)", getImageUri("square", "medium"), getImageUri("square", "large")});
            srcSet.add(new String[]{"(min-width: 480px)", getImageUri("normal", "small"), getImageUri("normal", "medium")});
        }
        return srcSet;
    }
}
