package com.composum.pages.components.model.time;

import com.composum.sling.platform.staging.query.QueryConditionDsl;

import javax.annotation.Nonnull;

import static com.composum.pages.components.model.time.TimeRelated.PN_DATE;
import static com.composum.pages.components.model.time.TimeRelated.PN_DATE_END;

public class EventCalendar extends AbstractCalendar<Event> {

    @Nonnull
    @Override
    protected Class<Event> getItemType() {
        return Event.class;
    }

    @Override
    @Nonnull
    public String getResourceType() {
        return "composum/pages/components/time/event/page";
    }

    @Nonnull
    @Override
    protected QueryConditionDsl.QueryCondition dateCondition(@Nonnull QueryConditionDsl.QueryCondition condition) {
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
}
