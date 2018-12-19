/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.service;

import com.composum.sling.platform.staging.query.QueryConditionDsl;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;

import static com.composum.pages.commons.PagesConstants.PN_CATEGORY;
import static com.composum.pages.commons.service.search.SearchUtil.andNameAndTextCondition;
import static com.composum.pages.commons.service.search.SearchUtil.nameAndTextCondition;

public interface ComponentManager {

    class ComponentScope {

        public final Collection<String> category;
        public final String searchTerm;

        public ComponentScope(Collection<String> categories, String searchTerm) {
            this.category = categories;
            this.searchTerm = searchTerm;
        }

        public ComponentScope(String categories, String searchTerm) {
            this(Arrays.asList(StringUtils.isNotBlank(categories)
                    ? StringUtils.split(categories, ",")
                    : new String[0]), searchTerm);
        }

        @Nullable
        public QueryConditionDsl.QueryCondition queryCondition(
                @Nonnull QueryConditionDsl.QueryConditionBuilder conditionBuilder) {
            return category.size() > 0
                    ? andNameAndTextCondition(conditionBuilder.in(PN_CATEGORY, category), searchTerm)
                    : nameAndTextCondition(conditionBuilder, searchTerm);
        }
    }

    Collection<String> getComponentCategories(ResourceResolver resolver);
}
