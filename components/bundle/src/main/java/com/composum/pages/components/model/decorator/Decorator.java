package com.composum.pages.components.model.decorator;

import com.composum.pages.commons.model.Container;
import org.apache.commons.lang3.StringUtils;

public class Decorator extends Container {

    public static final String PROP_LEVEL = "level";
    public static final String PROP_ICON = "icon";

    public static final String DEFAULT_LEVEL = "default";
    public static final String DEFAULT_TYPE = "composum/pages/components/element/text";

    private transient String level;
    private transient String icon;

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
