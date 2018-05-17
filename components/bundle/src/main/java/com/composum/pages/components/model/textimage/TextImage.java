package com.composum.pages.components.model.textimage;

import com.composum.pages.commons.util.RichTextUtil;
import com.composum.pages.components.model.ImageRelatedElement;
import com.composum.pages.components.model.text.Text;
import org.apache.commons.lang3.StringUtils;

public class TextImage extends ImageRelatedElement {

    public static final String PROP_TEXT = "text";
    public static final String PROP_IMAGE_POS = "imagePosition";
    public static final String PROP_FLOATING_TEXT = "floatingText";

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

    public String getImagePosition() {
        return getProperty(PROP_IMAGE_POS, "");
    }

    public boolean isImageBottom() {
        return "bottom".equalsIgnoreCase(getImagePosition());
    }
}
