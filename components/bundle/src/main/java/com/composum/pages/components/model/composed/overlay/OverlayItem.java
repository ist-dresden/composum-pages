package com.composum.pages.components.model.composed.overlay;

import com.composum.pages.commons.model.Container;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import java.util.Calendar;

public class OverlayItem extends Container {

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

    public static boolean isEnabled(Resource item) {
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
        }
        return enabled;
    }

    public boolean isEnabled() {
        if (enabled == null) {
            enabled = isEnabled(resource);
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
