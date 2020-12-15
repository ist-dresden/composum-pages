/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.components.model.text;

import com.composum.sling.core.BeanContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

public class Popup extends TextImage {

    public static final String PN_LINK_TEXT = "linkText";
    public static final String PN_PLACEMENT = "placement";
    public static final String PN_BUTTON = "isButton";

    private transient String linkText;
    private transient String placement;
    private transient String popoverType;

    public Popup(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public Popup() {
    }

    public boolean isValid() {
        return StringUtils.isNotBlank(getLinkText()) && StringUtils.isNotBlank(getText());
    }

    public String getLinkText() {
        if (linkText == null) {
            linkText = getProperty(PN_LINK_TEXT, "");
        }
        return linkText;
    }

    public String getPlacement() {
        if (placement == null) {
            placement = getProperty(PN_PLACEMENT, "top");
        }
        return placement;
    }

    public String getPopoverType() {
        if (popoverType == null) {
            popoverType = getProperty(PN_BUTTON, Boolean.FALSE) ? "button" : "link";
        }
        return popoverType;
    }
}
