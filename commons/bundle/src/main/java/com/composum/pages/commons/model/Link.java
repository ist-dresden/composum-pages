/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.model;

import com.composum.pages.commons.util.LinkUtil;
import org.apache.commons.lang3.StringUtils;

public class Link extends Element {

    public static final String PN_LINK = "link";
    public static final String PN_LINK_TITLE = "linkTitle";
    public static final String PN_LINK_TARGET = "linkTarget";

    private transient String link;
    private transient String linkTitle;
    private transient String linkUrl;
    private transient String target;

    public boolean isValid() {
        return isHasLink();
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

    @Override
    public String getTitle() {
        if (title == null) {
            title = super.getTitle();
            if (StringUtils.isBlank(title)) {
                title = LinkUtil.toText(getContext().getRequest(), getLink());
            }
        }
        return title;
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
