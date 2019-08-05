package com.composum.pages.components.model.text;

import com.composum.pages.commons.model.Container;
import com.composum.pages.commons.model.Element;
import com.composum.pages.commons.model.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

public class Text extends Element {

    public static final String PROP_TEXT = "text";
    public static final String PROP_ALIGNMENT = "textAlignment";

    public static final Pattern IGNORE_IN_TITLE_LEVEL = Pattern.compile("^.*/(column)$");

    private transient String titleLevel;
    private transient String text;

    public boolean isValid() {
        return StringUtils.isNotBlank(getTitle()) || StringUtils.isNotBlank(getText());
    }

    public String getTitleLevel() {
        if (titleLevel == null) {
            titleLevel = getProperty("titleLevel", String.class);
            if (titleLevel == null) {
                titleLevel = Integer.toString(Text.getTitleLevel(getResource()));
            }
        }
        return titleLevel;
    }

    public static int getTitleLevel(@Nonnull Resource element) {
        int titleLevel = 1;
        ResourceResolver resolver = element.getResourceResolver();
        while (element != null && titleLevel < 5 && !Page.isPage(element)) {
            if (Container.isContainer(resolver, element, null)
                    && !IGNORE_IN_TITLE_LEVEL.matcher(element.getResourceType()).matches()) {
                titleLevel++;
            }
            element = element.getParent();
        }
        return Math.max(titleLevel, 3);
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
