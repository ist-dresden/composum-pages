package com.composum.pages.components.model.text;

import com.composum.pages.commons.model.Element;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

public class Text extends Element {

    public static final String PROP_TEXT = "text";
    public static final String PROP_ALIGNMENT = "textAlignment";

    public static final Pattern IGNORE_IN_TITLE_LEVEL = Pattern.compile("^.*/(column)$");

    private transient Boolean hideTitle;
    private transient String text;

    public boolean isValid() {
        return StringUtils.isNotBlank(getTitle()) || StringUtils.isNotBlank(getText());
    }

    public boolean isHideTitle() {
        if (hideTitle == null) {
            hideTitle = getProperty("hideTitle", Boolean.FALSE);
        }
        return hideTitle;
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
