package com.composum.pages.components.model.container;

import com.composum.pages.commons.model.Container;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Section extends Container {

    public static final String PROP_ANCHOR = "anchor";
    public static final String PROP_LEVEL = "level";
    public static final String PROP_ICON = "icon";
    public static final String PROP_HIDE_TITLE = "hideTitle";

    public static final String DEFAULT_LEVEL = "";
    public static final String DEFAULT_TYPE = "composum/pages/components/element/text";

    private transient String anchor;
    private transient String waringLevel;
    private transient String icon;
    private transient Boolean hideTitle;

    public boolean isSetAnchor() {
        return StringUtils.isNotBlank(getAnchor());
    }

    public String getAnchor() {
        if (anchor == null) {
            anchor = getProperty(PROP_ANCHOR, "");
        }
        return anchor;
    }

    public String getWarningLevel() {
        if (waringLevel == null) {
            waringLevel = getProperty(PROP_LEVEL, DEFAULT_LEVEL);
        }
        return waringLevel;
    }

    public boolean isUsePanel() {
        return StringUtils.isNotBlank(getWarningLevel());
    }

    public String getPanelCss() {
        List<String> css = new ArrayList<>();
        if (isHasIcon()) {
            css.add("section-icon");
        }
        if (isHasTitle()) {
            css.add("section-title");
        }
        if (isUsePanel()) {
            css.add("panel");
            css.add("panel-" + getWarningLevel());
        } else {
            css.add("no-panel");
        }
        return StringUtils.join(css, " ");
    }

    public boolean isHasIcon() {
        return StringUtils.isNotBlank(getIcon());
    }

    public String getIcon() {
        if (icon == null) {
            icon = getProperty(PROP_ICON, "");
        }
        return icon;
    }

    public boolean isHasTitle() {
        return !isHideTitle() && StringUtils.isNotBlank(getTitle());
    }

    public boolean isHideTitle() {
        if (hideTitle == null) {
            hideTitle = getProperty(PROP_HIDE_TITLE, Boolean.FALSE);
        }
        return hideTitle;
    }

    @Override
    protected int getDefaultMinElements() {
        return 1;
    }

    @Override
    protected String getDefaultElementType() {
        return DEFAULT_TYPE;
    }
}
