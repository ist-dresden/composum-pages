/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.components.model.link;

import com.composum.pages.commons.model.Element;
import com.composum.sling.core.util.LinkUtil;
import org.apache.commons.lang3.StringUtils;

public class Link extends Element {

    public static final String PROP_LINK = "link";
    public static final String PROP_LINK_TITLE = "linkTitle";

    private transient String link;
    private transient String linkTitle;
    private transient String linkUrl;

    public boolean isValid() {
        return StringUtils.isNotBlank(getLinkUrl());
    }

    public String getLink() {
        if (link == null) {
            link = getProperty(PROP_LINK, "");
        }
        return link;
    }

    public String getLinkTitle() {
        if (linkTitle == null) {
            linkTitle = getProperty(PROP_LINK_TITLE, "");
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
}
