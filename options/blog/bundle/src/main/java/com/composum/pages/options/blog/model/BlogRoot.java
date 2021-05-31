package com.composum.pages.options.blog.model;

import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.service.PageManager;
import com.composum.pages.commons.service.SiteManager;
import com.composum.pages.options.blog.BlogConstants;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;

public class BlogRoot extends Page {

    public static boolean isBlogRoot(@Nonnull final Resource resource) {
        Resource content;
        return isPage(resource) && ((content = resource.getChild(ResourceUtil.JCR_CONTENT)) != null
                && content.isResourceType(BlogConstants.RT_BLOG));
    }

    @Nonnull
    public static BlogRoot findBlogRoot(@Nonnull final BeanContext context, @Nonnull final Resource resource) {
        PageManager pageManager = context.getService(PageManager.class);
        Page page = pageManager.getContainingPage(context, resource);
        while (page != null && !page.isHome() && !isBlogRoot(page.getResource())) {
            page = page.getParentPage();
        }
        if (page != null) {
            return pageManager.createBean(context, page.getResource(), BlogRoot.class);
        }
        Site site = context.getService(SiteManager.class).getContainingSite(context, resource);
        return pageManager.createBean(context,
                site != null ? site.getHomepage(context.getLocale()).getResource() : resource,
                BlogRoot.class);
    }
}
