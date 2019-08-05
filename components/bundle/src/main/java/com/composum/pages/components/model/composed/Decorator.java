package com.composum.pages.components.model.composed;

import com.composum.pages.commons.model.Container;
import com.composum.pages.components.model.text.Text;
import org.apache.commons.lang3.StringUtils;

public class Decorator extends Container {

    public static final String PROP_LEVEL = "level";
    public static final String PROP_ICON = "icon";

    public static final String DEFAULT_LEVEL = "default";
    public static final String DEFAULT_TYPE = "composum/pages/components/element/text";

    private transient String titleLevel;
    private transient String level;
    private transient String icon;

    public String getTitleLevel() {
        if (titleLevel == null) {
            String title = getTitle();
            if (StringUtils.isNotBlank(title)) {
                titleLevel = getProperty("titleLevel", String.class);
                if (titleLevel == null) {
                    titleLevel = Integer.toString(Text.getTitleLevel(getResource()));
                }
            } else {
                titleLevel = "";
            }
        }
        return titleLevel;
    }

    public String getLevel() {
        if (level == null) {
            level = getProperty(PROP_LEVEL, DEFAULT_LEVEL);
        }
        return level;
    }

    public boolean getHasIcon() {
        return StringUtils.isNotBlank(getIcon());
    }

    public String getIcon() {
        if (icon == null) {
            icon = getProperty(PROP_ICON, "");
        }
        return icon;
    }

    @Override
    protected int getDefaultMinElements() {
        return 1;
    }

    @Override
    protected int getDefaultMaxElements() {
        return 1;
    }

    @Override
    protected String getDefaultElementType() {
        return DEFAULT_TYPE;
    }
}
