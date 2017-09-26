package com.composum.pages.components.model.text;

import com.composum.pages.commons.model.Element;
import org.apache.commons.lang3.StringUtils;

public class Text extends Element {

    private transient String title;
    private transient String text;

    public boolean isValid() {
        return StringUtils.isNotBlank(getTitle()) || StringUtils.isNotBlank(getText());
    }

    public String getTitle() {
        if (title == null) {
            title = getProperty("title", "");
            if (StringUtils.isBlank(title)) {
                title = getProperty("jcr:title", getLocale(), title);
            }
        }
        return title;
    }

    public String getText() {
        if (text == null) {
            text = getProperty("text", "");
        }
        return text;
    }
}
