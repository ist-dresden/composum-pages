package com.composum.pages.components.model.text;

import com.composum.pages.components.model.ImageRelatedElement;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;

import static com.composum.pages.components.model.text.Text.PROP_TEXT;

public class TextImage extends ImageRelatedElement {

    public static final String PROP_IMAGE_POS = "imagePosition";
    public static final String PROP_FLOATING_TEXT = "floatingText";
    public static final String PROP_ALIGNMENT = "textAlignment";

    private transient String text;
    private transient Boolean hideTitle;
    private transient String tileTitle;

    public boolean isTextValid() {
        return StringUtils.isNotBlank(getTitle()) || StringUtils.isNotBlank(getText());
    }

    public String getText() {
        if (text == null) {
            text = getProperty(PROP_TEXT, "");
        }
        return text;
    }

    public boolean isHideTitle() {
        if (hideTitle == null) {
            hideTitle = getProperty("hideTitle", Boolean.FALSE);
        }
        return hideTitle;
    }

    @Nonnull
    @Override
    public String getTileTitle() {
        if (tileTitle == null) {
            tileTitle = super.getTitle();
            if (StringUtils.isBlank(tileTitle)) {
                tileTitle = StringUtils.substring(getText().replaceAll("</?[^>]*>", ""), 0, 100);
            }
        }
        return tileTitle;
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
