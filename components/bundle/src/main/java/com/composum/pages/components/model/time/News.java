/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.components.model.time;

import com.composum.pages.commons.model.Page;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nullable;

public class News extends TimeRelated {

    public static final String TYPE = "news";
    public static final String PAGE_TYPE = "composum/pages/components/time/" + TYPE + "/page";
    public static final String TEASER_TYPE = "composum/pages/components/time/" + TYPE + "/teaser";

    @Override
    @Nullable
    protected Resource determineResource(@Nullable Resource initialResource) {
        Page containingPage = getPageManager().getContainingPage(this.context, initialResource);
        return containingPage != null ? containingPage.getResource() : initialResource;
    }
}
