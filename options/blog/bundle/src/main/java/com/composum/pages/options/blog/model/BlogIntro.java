package com.composum.pages.options.blog.model;

import com.composum.pages.commons.model.Element;

public class BlogIntro extends Element {

    private transient Boolean blogRoot;

    public boolean isBlogRoot() {
        if (blogRoot == null) {
            blogRoot = BlogRoot.isBlogRoot(getCurrentPage().getResource());
        }
        return blogRoot;
    }
}
