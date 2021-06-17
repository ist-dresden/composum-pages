package com.composum.pages.options.blog.model;

import com.composum.pages.commons.model.Element;
import com.composum.pages.commons.service.PageManager;
import com.composum.pages.options.blog.BlogConstants;
import com.composum.sling.core.BeanContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.jcr.query.Query;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import static com.composum.pages.options.blog.model.BlogRoot.findBlogRoot;

public class NewestArticles extends Element {

    private static final Logger LOG = LoggerFactory.getLogger(NewestArticles.class);

    private transient String searchRoot;
    private transient int maxCount;

    private transient Set<BlogArticle> articles;

    @Override
    public void initialize(@Nonnull final BeanContext context, @Nonnull final Resource resource) {
        super.initialize(context, resource);
        searchRoot = getProperty(BlogConstants.PN_SEARCH_ROOT, String.class);
        maxCount = getProperty(BlogConstants.PN_MAX_COUNT, 10);
    }

    @Nonnull
    public Collection<BlogArticle> getArticles() {
        if (articles == null) {
            articles = findArticles();
        }
        return articles;
    }

    @Nonnull
    @SuppressWarnings("deprecation")
    protected Set<BlogArticle> findArticles() {
        Set<BlogArticle> result = new TreeSet<>();
        PageManager pageManager = context.getService(PageManager.class);
        ResourceResolver resolver = context.getResolver();
        String queryString = buildContentQuery();
        LOG.info("query: |{}|", queryString);
        int count = 0;
        Iterator<Resource> articleContents = resolver.findResources(queryString, Query.XPATH);
        try {
            while (articleContents.hasNext() && maxCount < 1 || count < maxCount) {
                Resource articlePage = articleContents.next();
                if (articlePage != null) {
                    result.add(pageManager.createBean(context, articlePage, BlogArticle.class));
                    count++;
                }
            }
        } catch (Exception ex) {
            LOG.error(ex.toString());
        }
        return result;
    }

    @Nonnull
    protected String buildContentQuery() {
        StringBuilder builder = new StringBuilder("/jcr:root");
        builder.append(StringUtils.isNotBlank(searchRoot) ? searchRoot : findBlogRoot(context, getResource()).getPath());
        builder.append("//element(*,cpp:Page)[")
                .append("jcr:content/@sling:resourceType='").append(BlogConstants.RT_ARTICLE).append("'")
                .append("]");
        builder.append(" order by jcr:content/meta/@date descending");
        return builder.toString();
    }
}
