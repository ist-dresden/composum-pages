package com.composum.pages.options.blog.model;

import com.composum.pages.commons.model.Page;
import com.composum.pages.options.blog.BlogConstants;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Calendar;

public class BlogArticle extends Page {

    public static boolean isBlogArticle(@Nonnull final Resource resource) {
        Resource content;
        return isPage(resource) && ((content = resource.getChild(ResourceUtil.JCR_CONTENT)) != null
                && content.isResourceType(BlogConstants.RT_ARTICLE));
    }

    protected String subtitle;
    protected String introText;
    protected String author;
    protected Calendar date;

    @Override
    public int compareTo(@Nonnull Page page) {
        return page instanceof BlogArticle
                ? BlogConstants.DATE_COMPARATOR.compare((BlogArticle) page, this)
                : super.compareTo(page);
    }

    @Override
    public void initialize(@Nonnull final BeanContext context, @Nonnull final Resource resource) {
        super.initialize(context, resource);
        subtitle = getProperty(BlogConstants.PN_SUBTITLE, String.class);
        introText = getProperty(BlogConstants.PN_INTRO, String.class);
        author = getProperty(BlogConstants.PN_META_AUTHOR, String.class);
        date = getProperty(BlogConstants.PN_META_DATE, Calendar.class);
    }

    @Nullable
    public String getSubtitel() {
        return subtitle;
    }

    @Nullable
    public String getIntroText() {
        return introText;
    }

    @Nullable
    public String getAuthor() {
        return author;
    }

    @Nonnull
    public Calendar getDate() {
        if (date == null) {
            date = Calendar.getInstance();
        }
        return date;
    }
}
