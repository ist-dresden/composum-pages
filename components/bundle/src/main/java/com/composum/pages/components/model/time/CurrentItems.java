package com.composum.pages.components.model.time;

import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.platform.staging.query.QueryConditionDsl.QueryCondition;
import com.composum.sling.platform.staging.query.QueryConditionDsl.QueryConditionBuilder;

import javax.annotation.Nonnull;

import static com.composum.pages.components.model.time.TimeRelated.PN_DATE;
import static com.composum.pages.components.model.time.TimeRelated.PN_DATE_END;

public class CurrentItems extends NavigatorModel {

    @Override
    protected DateRange fromRequest(boolean useDefault) {
        return null; // ignore each range rule from request
    }

    @Override
    protected QueryCondition eventCondition(@Nonnull final QueryConditionBuilder conditionBuilder,
                                            @Nonnull final DateRange range) {
        return conditionBuilder
                .property(ResourceUtil.PROP_RESOURCE_TYPE).eq().val(Event.PAGE_TYPE).and()
                .property(PN_DATE).geq().val(range.getFrom()).or().startGroup()
                .isNotNull(PN_DATE_END).and().property(PN_DATE_END).gt().val(range.getFrom()).endGroup();
    }

    @Override
    protected QueryCondition newsCondition(@Nonnull final QueryConditionBuilder conditionBuilder,
                                           @Nonnull final DateRange range) {
        return conditionBuilder
                .property(ResourceUtil.PROP_RESOURCE_TYPE).eq().val(News.PAGE_TYPE).and()
                .property(PN_DATE).lt().val(range.getTo());
    }
}
