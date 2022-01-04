package com.composum.pages.options.blog.model;

import com.composum.pages.commons.model.Element;
import com.composum.pages.commons.service.PageManager;
import com.composum.pages.commons.util.DateConverter;
import com.composum.pages.options.blog.BlogConstants;
import com.composum.pages.options.blog.BlogConstants.DatePeriod;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.CoreConstants;
import com.composum.sling.core.util.RequestUtil;
import com.composum.sling.platform.staging.query.Query;
import com.composum.sling.platform.staging.query.QueryBuilder;
import com.composum.sling.platform.staging.query.QueryConditionDsl.QueryCondition;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import static com.composum.pages.commons.PagesConstants.NODE_TYPE_PAGE_CONTENT;
import static com.composum.pages.options.blog.BlogConstants.ABOUT_DATE_DAYS;
import static com.composum.pages.options.blog.BlogConstants.DatePeriod.about;
import static com.composum.pages.options.blog.BlogConstants.DatePeriod.lastMonth;
import static com.composum.pages.options.blog.BlogConstants.DatePeriod.lastYear;
import static com.composum.pages.options.blog.BlogConstants.PN_META_AUTHOR;
import static com.composum.pages.options.blog.BlogConstants.PN_META_DATE;
import static com.composum.pages.options.blog.BlogConstants.RP_AUTHOR;
import static com.composum.pages.options.blog.BlogConstants.RP_DATE;
import static com.composum.pages.options.blog.BlogConstants.RP_PERIOD;
import static com.composum.pages.options.blog.BlogConstants.RP_TERM;
import static com.composum.pages.options.blog.model.BlogRoot.findBlogRoot;

public class NewestArticles extends Element {

    private static final Logger LOG = LoggerFactory.getLogger(NewestArticles.class);

    public static final String QP_RES_TYPE = CoreConstants.PROP_RESOURCE_TYPE;
    public static final String QP_AUTHOR = PN_META_AUTHOR;
    public static final String QP_DATE = PN_META_DATE;

    protected String searchRoot;
    protected int maxCount;

    protected String pattern;
    protected String author;
    protected Calendar date;
    protected DatePeriod period;

    private transient Set<BlogArticle> articles;

    @Override
    public void initialize(@Nonnull final BeanContext context, @Nonnull final Resource resource) {
        super.initialize(context, resource);
        SlingHttpServletRequest request = getContext().getRequest();
        String param;
        searchRoot = getProperty(BlogConstants.PN_SEARCH_ROOT, String.class);
        maxCount = getProperty(BlogConstants.PN_MAX_COUNT, 10);
        pattern = RequestUtil.getParameter(request, RP_TERM,
                getProperty(BlogConstants.PN_PATTERN, String.class));
        author = RequestUtil.getParameter(request, RP_AUTHOR,
                getProperty(BlogConstants.PN_AUTHOR, String.class));
        param = request.getParameter(RP_DATE);
        if (StringUtils.isNotBlank(param)) {
            date = DateConverter.convert(param);
        }
        if (date == null) {
            date = getProperty(BlogConstants.PN_DATE, Calendar.class);
        }
        try {
            period = DatePeriod.valueOf(RequestUtil.getParameter(request, RP_PERIOD,
                    getProperty(BlogConstants.PN_PERIOD, about.name())));
        } catch (IllegalArgumentException ex) {
            period = about;
        }
        if (period == lastMonth || period == lastYear) {
            date = Calendar.getInstance(getLocale());
        }
        if (date != null) {
            date.set(Calendar.HOUR_OF_DAY, 12);
            date.set(Calendar.MINUTE, 0);
            date.set(Calendar.SECOND, 0);
            date.set(Calendar.MILLISECOND, 0);
        }
    }

    public String getPattern() {
        return pattern;
    }

    public String getAuthor() {
        return author;
    }

    public Calendar getDate() {
        return date;
    }

    public String getDateString() {
        return date != null ? new SimpleDateFormat("yyyy-MM-dd").format(date.getTime()) : null;
    }

    public DatePeriod getPeriod() {
        return period;
    }

    @Nonnull
    public Collection<BlogArticle> getArticles() {
        if (articles == null) {
            articles = findArticles();
        }
        return articles;
    }

    @Nonnull
    protected Set<BlogArticle> findArticles() {
        Set<BlogArticle> result = new TreeSet<>();
        PageManager pageManager = context.getService(PageManager.class);
        Query query = buildQuery();
        query.limit(maxCount);
        LOG.debug("query: |{}|", query);
        int count = 0;
        for (Resource articleContent : query.execute()) {
            Resource articlePage = articleContent.getParent();
            if (articlePage != null) {
                result.add(pageManager.createBean(context, articlePage, BlogArticle.class));
                count++;
            }
        }
        return result;
    }

    @Nonnull
    protected Query buildQuery() {
        Query query = Objects.requireNonNull(context.getResolver().adaptTo(QueryBuilder.class)).createQuery();
        String rootPath = StringUtils.isNotBlank(searchRoot) ? searchRoot
                : findBlogRoot(context, getResource()).getPath();
        query.path(rootPath).element(JcrConstants.JCR_CONTENT, NODE_TYPE_PAGE_CONTENT).orderBy(QP_DATE).descending();
        QueryCondition condition = query.conditionBuilder().property(QP_RES_TYPE).eq().val(BlogConstants.RT_ARTICLE);
        if (StringUtils.isNotBlank(pattern)) {
            condition.and().contains(pattern);
        }
        if (StringUtils.isNotBlank(author)) {
            condition.and().property(QP_AUTHOR).like().val('%' + author + '%');
        }
        if (date != null) {
            switch (period) {
                default:
                case about:
                    Calendar after = (Calendar) date.clone();
                    Calendar before = (Calendar) date.clone();
                    after.add(Calendar.DAY_OF_MONTH, -ABOUT_DATE_DAYS);
                    before.add(Calendar.DAY_OF_MONTH, ABOUT_DATE_DAYS);
                    condition.and().property(QP_DATE).gt().val(after)
                            .and().property(QP_DATE).lt().val(before);
                    break;
                case before:
                    condition.and().property(QP_DATE).lt().val(date);
                    break;
                case after:
                    condition.and().property(QP_DATE).gt().val(date);
                    break;
                case lastMonth:
                    Calendar lastMonth = (Calendar) date.clone();
                    lastMonth.add(Calendar.MONTH, -1);
                    lastMonth.set(Calendar.DAY_OF_MONTH, 0);
                    lastMonth.add(Calendar.DAY_OF_MONTH, -1);
                    condition.and().property(QP_DATE).gt().val(lastMonth);
                    break;
                case lastYear:
                    Calendar lastYear = (Calendar) date.clone();
                    lastYear.add(Calendar.YEAR, -1);
                    lastYear.set(Calendar.DAY_OF_MONTH, 0);
                    lastYear.add(Calendar.DAY_OF_MONTH, -1);
                    condition.and().property(QP_DATE).gt().val(lastYear);
                    break;
            }
        }
        LOG.debug("query.condition: |{}|", condition);
        query.condition(condition);
        return query;
    }
}
