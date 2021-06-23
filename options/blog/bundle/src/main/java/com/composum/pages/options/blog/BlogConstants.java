package com.composum.pages.options.blog;

import com.composum.pages.options.blog.model.BlogArticle;
import com.composum.sling.core.util.ResourceUtil;

import java.util.Comparator;

public interface BlogConstants {

    // resource types
    String RT_BLOG = "composum/pages/options/blog/components/page/blog";
    String RT_ARTICLE = "composum/pages/options/blog/components/page/article";

    // request parameters
    String RP_TERM = "term";
    String RP_AUTHOR = "author";
    String RP_DATE = "date";
    String RP_PERIOD = "period";

    // property names
    String PN_TITLE = ResourceUtil.JCR_TITLE;
    String PN_SUBTITLE = "subtitle";
    String PN_INTRO = ResourceUtil.JCR_DESCRIPTION;
    String PN_META_AUTHOR = "meta/author";
    String PN_META_DATE = "meta/date";

    String PN_PATTERN = "pattern";
    String PN_PERIOD = "period";
    String PN_AUTHOR = "author";
    String PN_DATE = "date";
    String PN_SEARCH_ROOT = "searchRoot";
    String PN_MAX_COUNT = "maxCount";

    // settings
    int ABOUT_DATE_DAYS = 7;

    enum DatePeriod {lastMonth, lastYear, about, before, after}

    /**
     * the comparator to order articles by date
     */
    Comparator<BlogArticle> DATE_COMPARATOR = Comparator.comparing(BlogArticle::getDate);
}
