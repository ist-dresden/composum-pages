package com.composum.pages.components.model.time;

import com.composum.pages.commons.model.Element;
import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.service.PageManager;
import com.composum.pages.commons.util.RequestUtil;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.platform.staging.query.Query;
import com.composum.sling.platform.staging.query.QueryBuilder;
import com.composum.sling.platform.staging.query.QueryConditionDsl.QueryCondition;
import com.composum.sling.platform.staging.query.QueryConditionDsl.QueryConditionBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import static com.composum.pages.commons.PagesConstants.NODE_TYPE_PAGE_CONTENT;
import static com.composum.pages.commons.PagesConstants.PN_CATEGORY;
import static com.composum.pages.commons.PagesConstants.PN_TITLE;
import static com.composum.pages.components.model.time.TimeRelated.PN_DATE;
import static com.composum.pages.components.model.time.TimeRelated.PN_DATE_END;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

/**
 * the model for a date range related resource searching component
 */
public class NavigatorModel extends Element {

    public static final String PNM_RANGE = "range";
    public static final String PNM_TERM = "term";

    public static final String PN_YEAR = "year";
    public static final String PN_MONTH = "month";

    public static final String PN_SHOW_NAVIGATION = "showNavigation";

    public static final String PN_SEARCH_ROOT = "searchRoot";
    public static final String PN_MAX_RESULTS = "maxResults";

    public static final String PN_ITEM_TYPE = "itemType";

    public class Item {

        protected final String teaserType;
        protected final TimeRelated model;

        public Item(String teaserType, TimeRelated model) {
            this.model = model;
            this.teaserType = teaserType;
        }

        public String getTeaserType() {
            return teaserType;
        }

        public TimeRelated getModel() {
            return model;
        }
    }

    protected transient Integer year, month;
    protected transient DateRange dateRange;

    private transient String label;
    private transient Boolean showNavigation;

    private transient List<String> category;
    private transient String searchTerm;

    private transient String searchRoot;
    private transient Integer maxResults;

    private transient String itemType;
    private transient List<Item> items;

    @Nonnull
    public String getItemType() {
        if (itemType == null) {
            itemType = getProperty(PN_ITEM_TYPE, null, "");
        }
        return itemType;
    }

    /**
     * @return the list of matching resources
     */
    public List<Item> getItems() {
        if (items == null) {
            PageManager manager = getPageManager();
            items = new ArrayList<>();
            Query query = getQuery();
            for (Resource element : query.execute()) {
                String teaserType = element.isResourceType(Event.PAGE_TYPE) ? Event.TEASER_TYPE : News.TEASER_TYPE;
                items.add(new Item(teaserType, createBean(manager, element)));
            }
        }
        return items;
    }

    public boolean isShowNavigation() {
        if (showNavigation == null) {
            showNavigation = getProperty(PN_SHOW_NAVIGATION, Boolean.FALSE);
        }
        return showNavigation;
    }

    public String getBackwardRange() {
        return getDateRange().move(-1).getRule();
    }

    public String getForwardRange() {
        return getDateRange().move(1).getRule();
    }

    public String getLabel() {
        if (label == null) {
            label = getProperty(PN_TITLE, getDateRange().toString());
        }
        return label;
    }

    /**
     * @return the current date related scope; default: the current month
     */
    @Nonnull
    public DateRange getDateRange() {
        if (dateRange == null) {
            dateRange = fromRequest(false);
            if (dateRange == null) {
                Locale locale = getLocale();
                TimeZone timeZone = TimeZone.getDefault();
                dateRange = new DateRange(timeZone, locale, MONTH, getYear(), getMonth());
            }
        }
        return dateRange;
    }

    protected DateRange fromRequest(boolean useDefault) {
        Locale locale = getLocale();
        TimeZone timeZone = TimeZone.getDefault();
        String rule = RequestUtil.getParameter(getContext().getRequest(), PNM_RANGE, "");
        return useDefault || StringUtils.isNotBlank(rule) ? DateRange.valueOf(rule, timeZone, locale) : null;
    }

    public int getYear() {
        if (year == null) {
            year = getProperty(PN_YEAR, Calendar.getInstance().get(YEAR));
            if (year < 10) {
                Calendar date = Calendar.getInstance();
                date.add(YEAR, year);
                year = date.get(YEAR);
            }
        }
        return year;
    }

    public int getMonth() {
        if (month == null) {
            month = getProperty(PN_MONTH, Calendar.getInstance().get(Calendar.MONTH));
            if (month < 1) {
                Calendar date = Calendar.getInstance();
                date.set(YEAR, getYear());
                date.add(Calendar.MONTH, month);
                month = date.get(Calendar.MONTH);
            }
            month++;
        }
        return month;
    }

    /**
     * @return the current category scope (optional; maybe empty)
     */
    @Nonnull
    public List<String> getCategory() {
        if (category == null) {
            String[] parameter = getContext().getRequest().getParameterValues(PN_CATEGORY);
            if (parameter != null) {
                category = Arrays.asList(parameter);
            } else {
                category = super.getCategory();
            }
        }
        return category;
    }

    /**
     * @return an additional text pattern for filtering (optional; maybe empty)
     */
    @Nonnull
    public String getSearchTerm() {
        if (searchTerm == null) {
            searchTerm = RequestUtil.getParameter(getContext().getRequest(), PNM_TERM, getProperty(PNM_TERM, ""));
        }
        return searchTerm;
    }

    /**
     * @return the repository root for searching; default: the sites root path
     */
    public String getSearchRoot() {
        if (searchRoot == null) {
            searchRoot = getProperty(PN_SEARCH_ROOT, "");
            if (StringUtils.isBlank(searchRoot)) {
                Site site = getSiteManager().getContainingSite(this);
                if (site != null) {
                    searchRoot = site.getPath();
                } else {
                    searchRoot = "/content";
                }
            }
        }
        return searchRoot;
    }

    /**
     * @return the result limit; 0: no limit (the deafult)
     */
    public int getMaxResults() {
        if (maxResults == null) {
            maxResults = getProperty(PN_MAX_RESULTS, 0);
        }
        return maxResults;
    }

    /**
     * @return a query like the following XPATH query string (event example):
     * <code><pre>
     * /jcr:root/{root}//element(*,cpp:PageContent)[@sling:resourceType='.../time/event/page'
     *     and @date < xs:dateTime('{to}') and (@dateEnd and @dateEnd > xs:dateTime('{from}') or @date >= xs:dateTime('{from}'))]
     *     order by @date ascending
     * </pre></code>
     */
    @Nonnull
    public Query getQuery() {
        DateRange range = getDateRange();
        Query query = Objects.requireNonNull(context.getResolver().adaptTo(QueryBuilder.class)).createQuery();
        QueryCondition condition = itemCondition(query.conditionBuilder());
        List<String> category = getCategory();
        if (category.size() > 0) {
            condition.and().in(PN_CATEGORY, category);
        }
        String term = getSearchTerm();
        if (StringUtils.isNotBlank(term)) {
            condition.and().contains(term);
        }
        query.path(getSearchRoot()).type(NODE_TYPE_PAGE_CONTENT).condition(condition);
        int max = getMaxResults();
        if (max > 0) {
            query.limit(max);
        }
        completeQuery(query);
        return query;
    }

    /**
     * the hook to build the type and date range condition of the query
     */
    @Nonnull
    protected TimeRelated createBean(PageManager manager, Resource resource) {
        Class<? extends TimeRelated> type = resource.isResourceType(Event.PAGE_TYPE) ? Event.class : News.class;
        return manager.createBean(context, resource, type);
    }

    /**
     * the hook to generate the conditions for searching the appropriate content elements
     */
    @Nonnull
    protected QueryCondition itemCondition(@Nonnull QueryConditionBuilder conditionBuilder) {
        DateRange range = getDateRange();
        switch (getItemType()) {
            case Event.TYPE:
                return eventCondition(conditionBuilder, range);
            case News.TYPE:
                return newsCondition(conditionBuilder, range);
            default:
                return newsCondition(eventCondition(conditionBuilder.startGroup(), range)
                        .endGroup().or().startGroup(), range).endGroup();
        }
    }

    /**
     * @return the updated condition to find the appropriate event items
     */
    protected QueryCondition eventCondition(@Nonnull final QueryConditionBuilder conditionBuilder,
                                            @Nonnull final DateRange range) {
        return conditionBuilder
                .property(ResourceUtil.PROP_RESOURCE_TYPE).eq().val(Event.PAGE_TYPE).and()
                .property(PN_DATE).lt().val(range.getTo())
                .and().startGroup().startGroup()
                .isNotNull(PN_DATE_END).and().property(PN_DATE_END).gt().val(range.getFrom())
                .endGroup().or().property(PN_DATE).geq().val(range.getFrom()).endGroup();
    }

    /**
     * @return the updated condition to find the appropriate news items
     */
    protected QueryCondition newsCondition(@Nonnull final QueryConditionBuilder conditionBuilder,
                                           @Nonnull final DateRange range) {
        return conditionBuilder
                .property(ResourceUtil.PROP_RESOURCE_TYPE).eq().val(News.PAGE_TYPE).and()
                .property(PN_DATE).lt().val(range.getTo()).and()
                .property(PN_DATE).geq().val(range.getFrom());
    }

    /**
     * the hook to make the generated query final (add sorting, ...)
     */
    protected void completeQuery(@Nonnull final Query query) {
        switch (getItemType()) {
            case Event.TYPE:
                query.orderBy(PN_DATE).ascending();
                break;
            default:
                query.orderBy(PN_DATE).descending();
                break;
        }
    }
}
