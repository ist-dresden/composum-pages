package com.composum.pages.components.model.text;

import com.composum.pages.commons.model.Container;
import com.composum.pages.commons.model.Element;
import com.composum.pages.commons.model.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

public class Text extends Element {

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

    public static int getTitleLevel(Resource element) {
        int titleLevel = 2;
        ResourceResolver resolver = element.getResourceResolver();
        while (titleLevel < 5 && !Page.isPage(element)) {
            if (Container.isContainer(resolver, element, null)) {
                titleLevel++;
            }
            element = element.getParent();
        }
        return titleLevel < 3 ? 3 : titleLevel;
    }

    public String getText() {
        if (text == null) {
            text = getProperty("text", "");
        }
        return text;
    }
}
