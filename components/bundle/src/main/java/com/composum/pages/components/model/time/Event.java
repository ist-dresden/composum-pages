/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.components.model.time;

import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.util.LinkUtil;
import com.composum.pages.components.model.map.GoogleMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nullable;

import static com.composum.pages.commons.model.Link.PN_LINK;
import static com.composum.pages.commons.model.Link.PN_LINK_TARGET;
import static com.composum.pages.commons.model.Link.PN_LINK_TITLE;

public class Event extends TimeRelated {

    public static final String TYPE = "event";
    public static final String PAGE_TYPE = "composum/pages/components/time/" + TYPE + "/page";
    public static final String TEASER_TYPE = "composum/pages/components/time/" + TYPE + "/teaser";

    private transient String link;
    private transient String linkTitle;
    private transient String linkUrl;
    private transient String target;

    @Override
    @Nullable
    protected Resource determineResource(@Nullable Resource initialResource) {
        Page containingPage = getPageManager().getContainingPage(this.context, initialResource);
        return containingPage != null ? containingPage.getResource() : initialResource;
    }

    public String getLocation() {
        return getProperty("map/location", null, "");
    }

    public String getLocationUrl() {
        return GoogleMap.getMapsUrl(getCurrentPage(), getLocation());
    }

    public boolean isHasLink() {
        return StringUtils.isNotBlank(getLink());
    }

    public String getLink() {
        if (link == null) {
            link = getProperty(PN_LINK, "");
        }
        return link;
    }

    public String getLinkTitle() {
        if (linkTitle == null) {
            linkTitle = getProperty(PN_LINK_TITLE, getTitle());
        }
        return linkTitle;
    }

    public String getLinkUrl() {
        if (linkUrl == null) {
            linkUrl = getLink();
            if (StringUtils.isNotBlank(linkUrl)) {
                linkUrl = LinkUtil.getUrl(context.getRequest(), linkUrl);
            }
        }
        return linkUrl;
    }

    public String getLinkTarget() {
        if (target == null) {
            target = getProperty(PN_LINK_TARGET, "");
        }
        return target;
    }
}
