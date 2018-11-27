/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.components.model.time;

import com.composum.pages.commons.model.Page;
import com.composum.pages.components.model.map.GoogleMap;
import org.apache.sling.api.resource.Resource;

public class Event extends TimeRelated {

    @Override
    protected Resource determineResource(Resource initialResource) {
        Page containingPage = getPageManager().getContainingPage(context, initialResource);
        return containingPage != null ? containingPage.getResource() : initialResource;
    }

    public String getLocation() {
        return getProperty("map/location", null, "");
    }

    public String getLocationUrl() {
        return GoogleMap.getMapsUrl(getCurrentPage(), getLocation());
    }
}
