package com.composum.pages.options.blog;

import com.composum.pages.options.blog.model.BlogArticle;
import com.composum.sling.core.util.ResourceUtil;

import java.util.Comparator;

public interface BlogConstants {

    String RT_BLOG = "composum/pages/options/blog/components/page/blog";
    String RT_ARTICLE = "composum/pages/options/blog/components/page/article";

    String PN_TITLE = ResourceUtil.JCR_TITLE;
    String PN_SUBTITLE = "subtitle";
    String PN_INTRO = ResourceUtil.JCR_DESCRIPTION;
    String PN_AUTHOR = "meta/author";
    String PN_DATE = "meta/date";

    String PN_SEARCH_ROOT = "searchRoot";
    String PN_MAX_COUNT = "maxCount";

    Comparator<BlogArticle> DATE_COMPARATOR = Comparator.comparing(BlogArticle::getDate);
}
