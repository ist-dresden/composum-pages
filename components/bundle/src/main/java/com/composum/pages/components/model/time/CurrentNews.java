package com.composum.pages.components.model.time;

import com.composum.sling.platform.staging.query.QueryConditionDsl.QueryCondition;

import javax.annotation.Nonnull;

import static com.composum.pages.components.model.time.TimeRelated.PN_DATE;

public class CurrentNews extends NewsNavigator {

    @Override
    protected DateRange fromRequest(boolean useDefault) {
        return null; // ignore each range rule from request
    }

    @Override
    @Nonnull
    protected QueryCondition dateCondition(@Nonnull final QueryCondition condition) {
        DateRange range = getDateRange();
        condition.and().property(PN_DATE).lt().val(range.getTo());
        return condition;
    }
}
