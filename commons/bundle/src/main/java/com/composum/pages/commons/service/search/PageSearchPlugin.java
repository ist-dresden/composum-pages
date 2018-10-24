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
                Constants.SERVICE_DESCRIPTION + "=Pages Default Page Search Plugin"
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

    @Override
    protected ResourceFilter getTargetFilter() {
        return TARGET_FILTER;
    }

    @Nonnull
    @Override
    public List<SearchService.Result> search(@Nonnull final BeanContext context, @Nonnull final String root,
                                             @Nonnull final String searchExpression, @Nullable final ResourceFilter filter,
                                             final int offset, @Nullable final Integer limit)
            throws RepositoryException, SearchTermParseException {
        if (isBlank(searchExpression)) throw new SearchTermParseException(Kind.Empty,
                searchExpression, searchExpression);
        final Set<String> positiveTerms = new SearchtermParser(searchExpression).getPositiveSearchterms();
        if (positiveTerms.isEmpty()) throw new SearchTermParseException(Kind.NoPositivePhrases,
                searchExpression, searchExpression);
        final ResourceFilter searchFilter = filter != null ? filter : new SearchPageFilter(context);

        SearchService.LimitedQuery limitedQuery = new SearchService.LimitedQuery() {
            @Override
            public Pair<Boolean, List<SearchService.Result>> execQuery(int matchLimit) throws RepositoryException {
                Query q = context.getResolver().adaptTo(QueryBuilder.class).createQuery();
                q.path(root).type(NODE_TYPE_PAGE_CONTENT).orderBy(JCR_SCORE).descending();
                QueryConditionDsl.QueryCondition matchJoin = q.joinConditionBuilder()
                        .contains(StringUtils.join(positiveTerms, " OR "));
                q.condition(q.conditionBuilder().contains(searchExpression));
                q.join(LeftOuter, Descendant, matchJoin);
                q.limit(matchLimit);
                final int neededResults = null != limit ? offset + limit : Integer.MAX_VALUE;

                List<SearchService.Result> results = new ArrayList<>();
                Map<String, SubmatchResultImpl> targetToResultMap = new HashMap<>();
                Iterable<QueryValueMap> rows = q.selectAndExecute(COLUMN_PATH, COLUMN_SCORE,
                        matchJoin.joinSelector(COLUMN_PATH));
                int rowcount = 0;

                for (QueryValueMap row : rows) {
                    rowcount++;
                    String path = row.get(COLUMN_PATH, String.class);
                    SubmatchResultImpl result = targetToResultMap.get(path);
                    if (null == result) {
                        result = new SubmatchResultImpl(context, row.getResource().getParent(),
                                row.get(COLUMN_SCORE, Float.class),
                                new ArrayList<>(Arrays.asList(row.getResource())),
                                searchExpression, positiveTerms);
                        targetToResultMap.put(path, result);
                        if (searchFilter.accept(result.getTarget())) {
                            if (results.size() >= neededResults) return Pair.of(true, results);
                            results.add(result);
                        }
                    }
                    Resource match = row.getJoinResource(matchJoin.getSelector());
                    if (null != match) result.getMatches().add(match);
                }
                return Pair.of(rowcount < matchLimit, results);
            }
        };
        return searchService.executeQueryWithRaisingLimits(limitedQuery, offset, limit);
    }
}
