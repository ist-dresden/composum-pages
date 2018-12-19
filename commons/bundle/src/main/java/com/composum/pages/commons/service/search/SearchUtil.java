/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.service.search;

import com.composum.sling.platform.staging.query.QueryConditionDsl;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SearchUtil {

    /**
     * @return the search expression transformed for a node name query
     */
    @Nonnull
    public static String namePattern(@Nonnull String searchExpression) {
        String namePattern = searchExpression.replace('*', '%');
        if (!namePattern.startsWith("%")) {
            namePattern = "%" + namePattern;
        }
        if (!namePattern.endsWith("%")) {
            namePattern = namePattern + "%";
        }
        return namePattern;
    }

    /**
     * @return the query condition for searching nodes containing a text fragment including the node name
     */
    @Nullable
    public static QueryConditionDsl.QueryCondition nameAndTextCondition(
            @Nonnull QueryConditionDsl.QueryConditionBuilder conditionBuilder, @Nullable String searchExpression) {
        return StringUtils.isNotBlank(searchExpression)
                ? conditionBuilder.name().like().val(namePattern(searchExpression)).or().contains(searchExpression)
                : null;
    }

    /**
     * @return the query condition extended with a text search condition including the node name
     */
    @Nonnull
    public static QueryConditionDsl.QueryCondition andNameAndTextCondition(
            @Nonnull QueryConditionDsl.QueryCondition condition, @Nullable String searchExpression) {
        return StringUtils.isNotBlank(searchExpression)
                ? condition.and().startGroup().name().like().val(namePattern(searchExpression)).or().contains(searchExpression).endGroup()
                : condition;
    }
}
