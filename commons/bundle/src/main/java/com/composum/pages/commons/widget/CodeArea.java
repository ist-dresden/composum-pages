package com.composum.pages.commons.widget;

import com.composum.pages.commons.taglib.PropertyEditHandle;
import org.apache.commons.codec.binary.Base64;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;

public class CodeArea extends PropertyEditHandle<String> implements WidgetModel {

    public static final String LANGUAGE_ATTR = "language";
    public static final String DATA_LANGUAGE_ATTR = "data-" + LANGUAGE_ATTR;

    public CodeArea() {
        super(String.class);
    }

    public String getText() {
        return getValue();
    }

    public String getEncoded() {
        return Base64.encodeBase64String(getText().getBytes(StandardCharsets.UTF_8));
    }

    public String getHeight() {
        return widget.consumeDynamicAttribute("height", "");
    }

    @Override
    public String filterWidgetAttribute(@Nonnull String attributeKey, Object attributeValue) {
        if (LANGUAGE_ATTR.equals(attributeKey) || DATA_LANGUAGE_ATTR.equals(attributeKey)) {
            return DATA_LANGUAGE_ATTR;
        }
        return attributeKey;
    }
}
