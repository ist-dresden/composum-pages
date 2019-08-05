/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.components.model.teaser;

import com.composum.pages.components.model.ImageRelatedElement;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import static com.composum.pages.components.model.text.Text.PROP_TEXT;

public class Teaser extends ImageRelatedElement {

    public static final String PROP_VARIATION = "variation";
    public static final String DEFAULT_VARIATION = "default";

    public static final String PROP_LINK = "link";
    public static final String PROP_LINK_TITLE = "linkTitle";

    public static final String PROP_SUBTITLE = "subtitle";

    public static final String NODE_IMAGE = "image";
    public static final String PROP_IMAGE_REF = NODE_IMAGE + "/imageRef";

    public static final String NODE_VIDEO = "video";
    public static final String PROP_VIDEO_REF = NODE_VIDEO + "/videoRef";

    public static final String PROP_ICON = "icon";

    public static final String NODE_LINKS = "links";
    public static final String SELECTOR_LINK_SET = NODE_LINKS;

    public static final String SELECTOR_TEXTBLOCK = "textblock";
    public static final String SELECTOR_PLACEHOLDER = "placeholder";

    private transient String variation;

    private transient String subtitle;
    private transient String text;

    private transient String icon;

    public String getVariation() {
        if (variation == null) {
            variation = getProperty(PROP_VARIATION, DEFAULT_VARIATION);
            if (isHasLinkSet()) {
                variation += "-" + SELECTOR_LINK_SET;
            }
        }
        return variation;
    }

    public String getShape() {
        return isUseVideo() ? "video" : isUseImage() ? "image" : isUseIcon() ? "symbol" : "text";
    }

    public boolean isUseVideo() {
        return StringUtils.isNotBlank(getProperty(PROP_VIDEO_REF, ""));
    }

    public boolean isUseImage() {
        return !isUseVideo() && StringUtils.isNotBlank(getProperty(PROP_IMAGE_REF, ""));
    }

    public boolean isUseIcon() {
        return !isUseImage() && StringUtils.isNotBlank(getIcon());
    }

    public String getIcon() {
        if (icon == null) {
            icon = getProperty(PROP_ICON, "");
        }
        return icon;
    }

    public String getSubtitle() {
        if (subtitle == null) {
            subtitle = getProperty(PROP_SUBTITLE, "");
        }
        return subtitle;
    }

    public String getText() {
        if (text == null) {
            text = getProperty(PROP_TEXT, "");
        }
        return text;
    }

    public boolean isTextValid() {
        return StringUtils.isNotBlank(getTitle())
                || StringUtils.isNotBlank(getSubtitle())
                || StringUtils.isNotBlank(getText());
    }

    public boolean isHasLinkSet() {
        Resource links = getResource().getChild(NODE_LINKS);
        return links != null && links.hasChildren();
    }

    public String getTextSelector() {
        return isTextValid() ? SELECTOR_TEXTBLOCK : SELECTOR_PLACEHOLDER;
    }
}
