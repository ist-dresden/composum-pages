package com.composum.pages.components.model.text;

import com.composum.pages.commons.model.Container;
import com.composum.pages.commons.model.Element;
import com.composum.pages.commons.model.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.annotation.Nonnull;

public class Text extends Element {

    public static final String PROP_TEXT = "text";
    public static final String PROP_ALIGNMENT = "textAlignment";

    private transient Integer titleLevel;
    private transient String text;

    public boolean isValid() {
        return StringUtils.isNotBlank(getTitle()) || StringUtils.isNotBlank(getText());
    }

    public int getTitleLevel() {
        if (titleLevel == null) {
            titleLevel = getProperty("titleLevel", Integer.class);
            if (titleLevel == null) {
                titleLevel = Text.getTitleLevel(getResource());
            }
        }
        return titleLevel;
    }

    public static int getTitleLevel(@Nonnull Resource element) {
        int titleLevel = 2;
        ResourceResolver resolver = element.getResourceResolver();
        while (element != null && titleLevel < 5 && !Page.isPage(element)) {
            if (Container.isContainer(resolver, element, null)) {
                titleLevel++;
            }
            element = element.getParent();
        }
        return titleLevel < 3 ? 3 : titleLevel;
    }

    @Nonnull
    public String getText() {
        if (text == null) {
            text = getProperty(PROP_TEXT, "");
        }
        return text;
    }

    @Nonnull
    public String getAlignment() {
        return getProperty(PROP_ALIGNMENT, "left");
    }
}
