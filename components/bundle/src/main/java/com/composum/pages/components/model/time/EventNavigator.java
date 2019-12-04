package com.composum.pages.components.model.time;

import com.composum.sling.platform.staging.query.Query;
import com.composum.sling.platform.staging.query.QueryConditionDsl.QueryCondition;

import javax.annotation.Nonnull;

import static com.composum.pages.components.model.time.TimeRelated.PN_DATE;
import static com.composum.pages.components.model.time.TimeRelated.PN_DATE_END;

public class EventNavigator extends TimeNavigator<Event> {

    @Override
    @Nonnull
    protected Class<Event> getItemType() {
        return Event.class;
    }

    @Override
    @Nonnull
    public String getResourceType() {
        return "composum/pages/components/time/event/page";
    }

    @Override
    @Nonnull
    protected QueryCondition dateCondition(@Nonnull final QueryCondition condition) {
        DateRange range = getDateRange();
        condition.and()
                .property(PN_DATE).lt().val(range.getTo())
                .and().startGroup().startGroup()
                .isNotNull(PN_DATE_END).and().property(PN_DATE_END).gt().val(range.getFrom())
                .endGroup().or()
                .property(PN_DATE).geq().val(range.getFrom())
                .endGroup();
        return condition;
    }

    @Override
    protected void completeQuery(@Nonnull final Query query) {
        query.orderBy(PN_DATE).ascending();
    }
}
