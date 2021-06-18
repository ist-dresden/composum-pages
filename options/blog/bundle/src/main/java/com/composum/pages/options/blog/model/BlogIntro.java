package com.composum.pages.options.blog.model;

import com.composum.pages.commons.model.Element;
import com.composum.pages.commons.model.Page;
import org.apache.commons.lang3.StringUtils;

public class BlogIntro extends Element {

    public static final String PN_SUBTITLE = "subtitle";

    private transient Boolean _blank;
    private transient Boolean _blogRoot;

    private transient String subtitle;

    public boolean isBlogRoot() {
        if (_blogRoot == null) {
            Page currentPage = getCurrentPage();
            _blogRoot = currentPage != null && BlogRoot.isBlogRoot(currentPage.getResource());
        }
        return _blogRoot;
    }

    public boolean isBlank() {
        if (_blank == null) {
            _blank = StringUtils.isBlank(getTitle())
                    && StringUtils.isBlank(getSubtitle())
                    && StringUtils.isBlank(getDescription());
        }
        return _blank;
    }

    public String getSubtitle() {
        if (subtitle == null) {
            subtitle = getProperty(PN_SUBTITLE, "");
        }
        return subtitle;
    }
}
