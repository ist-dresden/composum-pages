package com.composum.pages.components.model.textimage;

import com.composum.pages.commons.util.RichTextUtil;
import com.composum.pages.components.model.ImageRelatedElement;
import com.composum.pages.components.model.text.Text;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;

import static com.composum.pages.components.model.text.Text.PROP_TEXT;

public class TextImage extends ImageRelatedElement {

    public static final String PROP_IMAGE_POS = "imagePosition";
    public static final String PROP_FLOATING_TEXT = "floatingText";
    public static final String PROP_ALIGNMENT = "textAlignment";

    private transient Integer titleLevel;
    private transient String text;

    public int getTitleLevel() {
        if (titleLevel == null) {
            titleLevel = getProperty("titleLevel", Integer.class);
            if (titleLevel == null) {
                titleLevel = Text.getTitleLevel(getResource());
            }
        }
        return titleLevel;
    }

    public boolean isTextValid() {
        return StringUtils.isNotBlank(getTitle()) || StringUtils.isNotBlank(getText());
    }

    public String getText() {
        if (text == null) {
            text = RichTextUtil.prepareRichText(context.getRequest(), getProperty(PROP_TEXT, ""));
        }
        return text;
    }

    public boolean isFloatingText() {
        return getProperty(PROP_FLOATING_TEXT, Boolean.FALSE);
    }

    @Nonnull
    public String getImagePosition() {
        return getProperty(PROP_IMAGE_POS, "top");
    }

    public boolean isImageBottom() {
        return "bottom".equalsIgnoreCase(getImagePosition());
    }

    @Nonnull
    public String getAlignment() {
        return getProperty(PROP_ALIGNMENT, "left");
    }
}
