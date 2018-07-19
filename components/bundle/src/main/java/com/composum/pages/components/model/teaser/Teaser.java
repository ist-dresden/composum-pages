package com.composum.pages.components.model.teaser;

import com.composum.pages.commons.util.RichTextUtil;
import com.composum.pages.components.model.ImageRelatedElement;
import com.composum.sling.core.util.LinkUtil;
import org.apache.commons.lang3.StringUtils;

import static com.composum.pages.components.model.text.Text.PROP_TEXT;

public class Teaser extends ImageRelatedElement {

    public static final String PROP_VARIATION = "variation";
    public static final String DEFAULT_VARIATION = "default";

    public static final String PROP_LINK = "link";
    public static final String PROP_SUBTITLE = "subtitle";

    public static final String SELECTOR_TEXTBLOCK = "textblock";
    public static final String SELECTOR_PLACEHOLDER = "placeholder";

    private transient String variation;

    private transient String link;
    private transient String linkUrl;

    private transient String subtitle;
    private transient String text;

    public String getVariation() {
        if (variation == null) {
            variation = getProperty(PROP_VARIATION, DEFAULT_VARIATION);
        }
        return variation;
    }

    public boolean getHasLink() {
        return StringUtils.isNotBlank(getLink());
    }

    public String getLink() {
        if (link == null) {
            link = getProperty(PROP_LINK, "");
        }
        return link;
    }

    public String getLinkUrl() {
        if (linkUrl == null) {
            linkUrl = getLink();
            if (StringUtils.isNotBlank(linkUrl)) {
                linkUrl = LinkUtil.getUrl(context.getRequest(), linkUrl);
            }
        }
        return linkUrl;
    }

    public String getSubtitle() {
        if (subtitle == null) {
            subtitle = getProperty(PROP_SUBTITLE, "");
        }
        return subtitle;
    }

    public String getText() {
        if (text == null) {
            text = RichTextUtil.prepareRichText(context.getRequest(), getProperty(PROP_TEXT, ""));
        }
        return text;
    }

    public boolean isTextValid() {
        return StringUtils.isNotBlank(getTitle())
                || StringUtils.isNotBlank(getSubtitle())
                || StringUtils.isNotBlank(getText());
    }

    public String getTextSelector() {
        return isTextValid() ? SELECTOR_TEXTBLOCK : SELECTOR_PLACEHOLDER;
    }
}
