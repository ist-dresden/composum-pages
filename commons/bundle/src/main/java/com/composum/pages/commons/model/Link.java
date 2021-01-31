/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.model;

import com.composum.pages.commons.util.LinkUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import java.util.regex.Matcher;

import static com.composum.pages.commons.PagesConstants.TILE_TITLE_URL;

public class Link extends Element {

    public static final String PN_LINK = "link";
    public static final String PN_LINK_TITLE = "linkTitle";
    public static final String PN_LINK_TARGET = "linkTarget";

    private transient Boolean hasLink;
    private transient String link;
    private transient String linkTitle;
    private transient String linkUrl;
    private transient String target;

    private transient String tileTitle;

    public Link(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public Link() {
    }

    public boolean isValid() {
        return isHasLink();
    }

    @Nonnull
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

    @Nonnull
    @Override
    public String getTileTitle() {
        if (tileTitle == null) {
            tileTitle = super.getTitle();
            if (StringUtils.isBlank(tileTitle)) {
                String url = getLinkUrl();
                if (StringUtils.isNotBlank(url)) {
                    Matcher matcher = TILE_TITLE_URL.matcher(url);
                    if (matcher.matches()) {
                        tileTitle = matcher.group(2);
                    }
                }
            }
        }
        return tileTitle;
    }

    public boolean isHasLink() {
        if (hasLink == null) {
            String link = getLink();
            hasLink = StringUtils.isNotBlank(link) && (LinkUtil.isExternalUrl(link) ||
                            !ResourceUtil.isNonExistingResource(getContext().getResolver().resolve(link)));
        }
        return hasLink;
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
