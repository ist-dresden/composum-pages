package com.composum.pages.components.model.time;

import com.composum.sling.platform.staging.query.Query;
import com.composum.sling.platform.staging.query.QueryConditionDsl.QueryCondition;

import javax.annotation.Nonnull;

import static com.composum.pages.components.model.time.TimeRelated.PN_DATE;

public class NewsNavigator extends TimeNavigator<News> {

    @Override
    @Nonnull
    protected Class<News> getItemType() {
        return News.class;
    }

    @Override
    @Nonnull
    public String getResourceType() {
        return "composum/pages/components/time/news/page";
    }

    @Override
    @Nonnull
    protected QueryCondition dateCondition(@Nonnull final QueryCondition condition) {
        DateRange range = getDateRange();
        condition.and()
                .property(PN_DATE).lt().val(range.getTo())
                .and()
                .property(PN_DATE).geq().val(range.getFrom());
        return condition;
    }

    @Override
    protected void completeQuery(@Nonnull final Query query) {
        query.orderBy(PN_DATE).ascending();
    }
}
