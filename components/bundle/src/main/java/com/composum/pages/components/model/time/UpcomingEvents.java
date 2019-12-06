package com.composum.pages.components.model.time;

import com.composum.sling.platform.staging.query.QueryConditionDsl.QueryCondition;

import javax.annotation.Nonnull;

import static com.composum.pages.components.model.time.TimeRelated.PN_DATE;
import static com.composum.pages.components.model.time.TimeRelated.PN_DATE_END;

public class UpcomingEvents extends EventNavigator {

    @Override
    protected DateRange fromRequest(boolean useDefault) {
        return null; // ignore each range rule from request
    }

    @Override
    @Nonnull
    protected QueryCondition dateCondition(@Nonnull final QueryCondition condition) {
        DateRange range = getDateRange();
        condition.and().property(PN_DATE).geq().val(range.getFrom()).or().startGroup()
                .isNotNull(PN_DATE_END).and().property(PN_DATE_END).gt().val(range.getFrom())
                .endGroup();
        return condition;
    }
}
