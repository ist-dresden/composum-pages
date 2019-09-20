/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.service.search;

import com.composum.pages.commons.service.search.SearchTermParseException.Kind;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.filter.StringFilter;
import com.composum.sling.platform.staging.query.Query;
import com.composum.sling.platform.staging.query.QueryBuilder;
import com.composum.sling.platform.staging.query.QueryConditionDsl;
import com.composum.sling.platform.staging.query.QueryValueMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.sling.api.resource.Resource;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.composum.pages.commons.PagesConstants.NODE_TYPE_PAGE;
import static com.composum.pages.commons.PagesConstants.NODE_TYPE_PAGE_CONTENT;
import static com.composum.pages.commons.service.search.SearchService.SELECTOR_PAGE;
import static com.composum.pages.commons.service.search.SearchUtil.nameAndTextCondition;
import static com.composum.sling.platform.staging.query.Query.COLUMN_PATH;
import static com.composum.sling.platform.staging.query.Query.COLUMN_SCORE;
import static com.composum.sling.platform.staging.query.Query.JoinCondition.Descendant;
import static com.composum.sling.platform.staging.query.Query.JoinType.LeftOuter;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.jackrabbit.JcrConstants.JCR_SCORE;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Service for fulltext page search.
 *
 * @author Hans-Peter Stoerr
 */
@Component(
        service = SearchPlugin.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Default Page Search Plugin"
        },
        immediate = true
)
public class PageSearchPlugin extends AbstractSearchPlugin {

    private static final Logger LOG = getLogger(PageSearchPlugin.class);

    /** target resource filter matching {@link com.composum.pages.commons.model.Page}s . */
    public static final ResourceFilter TARGET_FILTER = new ResourceFilter.PrimaryTypeFilter(
            new StringFilter.WhiteList("^" + NODE_TYPE_PAGE + "$"));

    @Override
    public int rating(@Nonnull String selectors) {
        return selectors.startsWith(SELECTOR_PAGE) ? 9 : 0;
    }

    @Nonnull
    @Override
    protected ResourceFilter getTargetFilter() {
        return TARGET_FILTER;
    }

    @Nonnull
    @Override
    protected ResourceFilter getDefaultTargetAcceptFilter(BeanContext context) {
        return new SearchPageFilter(context);
    }

    @Override
    protected void buildQuery(Query query, String root, String searchExpression) {
        super.buildQuery(query, root, searchExpression);
        query.type(NODE_TYPE_PAGE_CONTENT);
    }

}
