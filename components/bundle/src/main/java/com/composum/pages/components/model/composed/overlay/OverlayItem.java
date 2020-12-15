package com.composum.pages.components.model.composed.overlay;

import com.composum.pages.commons.model.Container;
import com.composum.pages.commons.util.RequestUtil;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

public class OverlayItem extends Container {

    private static final Logger LOG = LoggerFactory.getLogger(OverlayItem.class);

    public static final String PN_DISABLED = "disabled";
    public static final String PN_HIDE_CONTENT = "hideContent";
    public static final String PN_START_DATE = "startDate";
    public static final String PN_END_DATE = "endDate";
    public static final String PN_ALIGN_VERTICAL = "alignVertical";
    public static final String PN_ALIGN_HORIZONTAL = "alignHorizontal";
    public static final String PN_CSS_STYLE = "cssStyle";

    private transient Boolean enabled;
    private transient Boolean hideContent;
    private transient String alignment;
    private transient String cssStyle;

    public static boolean isEnabled(BeanContext context, Resource item) {
        ValueMap values = item.getValueMap();
        boolean enabled = !values.get(PN_DISABLED, Boolean.FALSE);
        if (enabled) {
            Calendar now = Calendar.getInstance();
            Calendar start = values.get(PN_START_DATE, Calendar.class);
            if (start != null) {
                if (start.after(now)) {
                    enabled = false;
                }
            }
            if (enabled) {
                Calendar end = values.get(PN_END_DATE, Calendar.class);
                if (end != null) {
                    if (!now.before(end)) {
                        enabled = false;
                    }
                }
            }
            if (LOG.isInfoEnabled()) {
                LOG.info("item: {} ({},{})", enabled, RequestUtil.isPreviewMode(context), item.getPath());
            }
            if (!enabled && RequestUtil.isPreviewMode(context)) {
                // show conditional content in preview mode
                enabled = true;
            }
        }
        return enabled;
    }

    public boolean isEnabled() {
        if (enabled == null) {
            enabled = isEnabled(context, resource);
        }
        return enabled;
    }

    public boolean isHideContent() {
        if (hideContent == null) {
            hideContent = isEnabled() && getProperty(PN_HIDE_CONTENT, Boolean.FALSE);
        }
        return hideContent;
    }

    public String getAlignment() {
        if (alignment == null) {
            if (isHideContent()) {
                alignment = "hide-content";
            } else {
                String horizontal = getProperty(PN_ALIGN_HORIZONTAL, "full");
                String vertical = getProperty(PN_ALIGN_VERTICAL, "full");
                alignment = "horizontal-" + horizontal + " vertical-" + vertical;
            }
        }
        return alignment;
    }

    public String getCssStyle() {
        if (cssStyle == null) {
            cssStyle = getProperty(PN_CSS_STYLE, "");
        }
        return cssStyle;
    }
}
