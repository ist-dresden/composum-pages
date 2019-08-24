package com.composum.pages.commons.model;

import com.composum.sling.core.BeanContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * a text component model base class
 */
public class Text extends Element {

    private static final Logger LOG = LoggerFactory.getLogger(Text.class);

    public static final String PROP_TEXT = "text";

    private transient String text;

    private transient Boolean valid;

    public Text() {
    }

    public Text(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public boolean isValid() {
        if (valid == null) {
            valid = StringUtils.isNotBlank(getTitle()) || StringUtils.isNotBlank(getText());
        }
        return valid;
    }

    public String getText() {
        if (text == null) {
            text = getProperty(PROP_TEXT, "");
        }
        return text;
    }
}
