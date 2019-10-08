/*
 * copyright (c) 2015 IST GmbH Dresden, Germany
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.components.model.element;

import com.composum.pages.commons.model.Element;
import com.composum.pages.commons.util.LinkUtil;
import com.composum.sling.core.BeanContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

public class IFrame extends Element {

    public static final String TYPE_SIMPLE = "simple";
    public static final String TYPE_PANEL = "panel";

    public static final String PN_SRC = "src";
    public static final String PN_SERVICE_URI = "serviceUri";
    public static final String PN_COPYRIGHT = "copyright";
    public static final String PN_STYLE = "style";
    public static final String PN_MODE = "mode";
    public static final String PN_HEIGHT = "height";
    public static final String PN_EXPANDABLE = "expandable";

    private transient String src;
    private transient String serviceUri;
    private transient String copyright;
    private transient String mode;
    private transient String height;
    private transient Boolean expandable;
    private transient String style;

    public IFrame(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public IFrame() {
    }

    public boolean isValid() {
        return StringUtils.isNotBlank(getSrc());
    }

    public boolean isShowHeading() {
        return isExpandable() || StringUtils.isNotBlank(getTitle());
    }

    public String getRenderType() {
        return TYPE_PANEL.equals(getStyle()) || isExpandable() ? TYPE_PANEL : TYPE_SIMPLE;
    }

    public String getSrc() {
        if (src == null) {
            src = getProperty(PN_SRC, "");
            String service = getServiceUri();
            if (StringUtils.isNotBlank(service)) {
                src = service + src;
            }
            if (StringUtils.isNotBlank(src)) {
                src = LinkUtil.getUrl(context.getRequest(), src);
            }
        }
        return src;
    }

    public String getServiceUri() {
        if (serviceUri == null) {
            serviceUri = getProperty(PN_SERVICE_URI, "");
        }
        return serviceUri;
    }

    public boolean isHasCopyright() {
        return StringUtils.isNotBlank(getCopyright());
    }

    public String getCopyright() {
        if (copyright == null) {
            copyright = getProperty(PN_COPYRIGHT, "");
        }
        return copyright;
    }

    public String getStyle() {
        if (style == null) {
            style = getProperty(PN_STYLE, TYPE_SIMPLE);
        }
        return style;
    }

    public String getMode() {
        if (mode == null) {
            mode = getProperty(PN_MODE, "body");
        }
        return mode;
    }

    public String getHeight() {
        if (height == null) {
            height = getProperty(PN_HEIGHT, "");
        }
        return height;
    }

    public boolean isExpandable() {
        if (expandable == null) {
            expandable = getProperty(PN_EXPANDABLE, Boolean.FALSE);
        }
        return expandable;
    }
}
