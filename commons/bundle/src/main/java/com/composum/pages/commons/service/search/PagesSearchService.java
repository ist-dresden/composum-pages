package com.composum.pages.commons.service.search;

import com.composum.pages.commons.service.SearchService;
import com.composum.pages.commons.service.search.SearchTermParseException.Kind;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.filter.StringFilter;
import com.composum.sling.core.util.LinkUtil;
import com.composum.sling.platform.staging.query.Query;
import com.composum.sling.platform.staging.query.QueryBuilder;
import com.composum.sling.platform.staging.query.QueryConditionDsl;
import com.composum.sling.platform.staging.query.QueryValueMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import javax.jcr.RepositoryException;
import java.net.URISyntaxException;
import java.util.*;

import static com.composum.pages.commons.PagesConstants.NODE_TYPE_PAGE;
import static com.composum.pages.commons.PagesConstants.NODE_TYPE_PAGE_CONTENT;
import static com.composum.sling.core.util.ResourceUtil.CONTENT_NODE;
import static com.composum.sling.core.util.ResourceUtil.PROP_TITLE;
import static com.composum.sling.platform.staging.query.Query.COLUMN_PATH;
import static com.composum.sling.platform.staging.query.Query.COLUMN_SCORE;
import static com.composum.sling.platform.staging.query.Query.JoinCondition.Descendant;
import static com.composum.sling.platform.staging.query.Query.JoinType.LeftOuter;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.jackrabbit.JcrConstants.JCR_SCORE;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Service for fulltext search.
 *
 * @author Hans-Peter Stoerr
 */
@Component(
        service = SearchService.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Pages Search Service"
        },
        immediate = true
)
@Designate(ocd = PagesSearchService.PagesSearchServiceConfiguration.class)
public class PagesSearchService implements SearchService {

    private static final Logger LOG = getLogger(PagesSearchService.class);
    /** Default target resource filter matching {@link com.composum.pages.commons.model.Page}s . */
    protected final ResourceFilter PAGE_FILTER =
            new ResourceFilter.PrimaryTypeFilter(new StringFilter.WhiteList("^" + NODE_TYPE_PAGE + "$"));
    protected PagesSearchServiceConfiguration config;
    protected ExcerptGenerator excerptGenerator = new ExcerptGeneratorImpl();

    @Activate
    @Modified
    public void activate(PagesSearchServiceConfiguration config) {
        this.config = config;
    }

    protected interface LimitedQuery {
        /**
         * Executes the query with the given limit; returns a pair of a boolean that is true when we are sure that all
         * results have been found in spite of the limit, and the results themselves.
         */
        Pair<Boolean, List<Result>> execQuery(int matchLimit) throws RepositoryException;

    }

    @Nonnull
    @Override
    public List<Result> searchPages(@Nonnull final BeanContext context, final String root,
                                    @Nonnull final String searchExpression, final int offset, final Integer limit)
            throws RepositoryException, SearchTermParseException {
        if (isBlank(searchExpression)) throw new SearchTermParseException(Kind.Empty,
                searchExpression, searchExpression);
        final Set<String> positiveTerms = new SearchtermParser(searchExpression).getPositiveSearchterms();
        if (positiveTerms.isEmpty()) throw new SearchTermParseException(Kind.NoPositivePhrases,
                searchExpression, searchExpression);

        LimitedQuery limitedQuery = new LimitedQuery() {
            @Override
            public Pair<Boolean, List<Result>> execQuery(int matchLimit) throws RepositoryException {
                Query q = context.getResolver().adaptTo(QueryBuilder.class).createQuery();
                q.path(root).type(NODE_TYPE_PAGE_CONTENT).orderBy(JCR_SCORE).descending();
                QueryConditionDsl.QueryCondition matchJoin = q.joinConditionBuilder()
                        .contains(StringUtils.join(positiveTerms, " OR "));
                q.condition(q.conditionBuilder().contains(searchExpression));
                q.join(LeftOuter, Descendant, matchJoin);
                q.limit(matchLimit);

                SearchPageFilter filter = new SearchPageFilter(context);
                final int neededResults = null != limit ? offset + limit : Integer.MAX_VALUE;

                List<Result> results = new ArrayList<>();
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
                                new ArrayList<Resource>(Arrays.asList(row.getResource())),
                                searchExpression, positiveTerms);
                        targetToResultMap.put(path, result);
                        if (filter.accept(result.getTarget())) {
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
        return executeQueryWithRaisingLimits(limitedQuery, offset, limit);
    }

    /**
     * Execute the query with raising limit until the required number of results is met. We don't know in advance how
     * large we have to set the limit in the query to get all neccesary results, since each page can have an a priori
     * unknown number of matches. Thus, the query is executed with an estimated limit, and is reexecuted with tripled
     * limit if the number of results is not sufficient and there are more limits.
     *
     * @return up to limit elements of the result list with the offset first elements skipped.
     */
    protected List<Result> executeQueryWithRaisingLimits(LimitedQuery limitedQuery, int offset, Integer limit)
            throws RepositoryException {
        Pair<Boolean, List<Result>> result;
        int neededResults = Integer.MAX_VALUE;
        int currentLimit = Integer.MAX_VALUE;
        if (null != limit) {
            neededResults = offset + limit;
            currentLimit = neededResults * config.overshoot() + 5;
        }
        int lastResultCount = -1;
        do {
            result = limitedQuery.execQuery(currentLimit);
            if (currentLimit > config.maximumMatchCount()) currentLimit = config.maximumMatchCount();
            if (result.getLeft() && result.getRight().size() >= neededResults) break;
            if (result.getRight().size() <= lastResultCount) break; // panic switch; shouldn't happen
            if (currentLimit >= config.maximumMatchCount()) break;
            lastResultCount = result.getRight().size();
            currentLimit = currentLimit * 3;
            LOG.info("Reexecuting search with limit {} for {}", currentLimit, neededResults); // should rarely happen
        } while (true);
        if (result.getRight().size() <= offset) return Collections.emptyList();
        else return result.getRight().subList(offset, Math.min(result.getRight().size(), neededResults));
    }

    @Override
    @Nonnull
    public List<Result> search(final @Nonnull BeanContext context, final String root,
                               final @Nonnull String searchExpression,
                               final ResourceFilter targetResourceFilter, final int offset, final Integer limit)
            throws RepositoryException, SearchTermParseException {
        if (isBlank(searchExpression)) throw new SearchTermParseException(Kind.Empty,
                searchExpression, searchExpression);
        final Set<String> positiveTerms = new SearchtermParser(searchExpression).getPositiveSearchterms();
        if (positiveTerms.isEmpty()) throw new SearchTermParseException(Kind.NoPositivePhrases,
                searchExpression, searchExpression);

        LimitedQuery limitedQuery = new LimitedQuery() {
            @Override
            public Pair<Boolean, List<Result>> execQuery(int matchLimit) throws RepositoryException {
                Query q = context.getResolver().adaptTo(QueryBuilder.class).createQuery();
                q.path(root).orderBy(JCR_SCORE).descending();
                q.condition(q.conditionBuilder().contains(searchExpression));
                q.limit(matchLimit);

                final int neededResults = null != limit ? offset + limit : Integer.MAX_VALUE;
                int rowcount = 0;

                List<Result> results = new ArrayList<>();
                Map<String, SubmatchResultImpl> targetToResultMap = new HashMap<>();
                for (Resource match : q.execute()) {
                    rowcount++;
                    Resource target = findTarget(match, null != targetResourceFilter ? targetResourceFilter : PAGE_FILTER);

                    SubmatchResultImpl result = targetToResultMap.get(target.getPath());
                    if (null == result) {
                        result = new SubmatchResultImpl(context, target, null, new ArrayList<Resource>(),
                                searchExpression, positiveTerms);
                        targetToResultMap.put(target.getPath(), result);
                        if (results.size() >= neededResults) return Pair.of(true, results);
                        results.add(result);
                    }
                    result.getMatches().add(match);
                }
                return Pair.of(rowcount < matchLimit, results);
            }
        };
        return executeQueryWithRaisingLimits(limitedQuery, offset, limit);
    }

    protected Resource findTarget(Resource resource, ResourceFilter
            targetResourceFilter) {
        Resource target = resource;
        while (null != target && !targetResourceFilter.accept(target)) target = target.getParent();
        return null != target ? target : resource;
    }

    protected String createTargetUrl(String path, SlingHttpServletRequest request, Collection<String> positiveTerms) {
        String basicUrl = LinkUtil.getUrl(request, path);
        try {
            URIBuilder builder = new URIBuilder(basicUrl);
            for (String term : positiveTerms) {
                builder.addParameter(PARAMETER_SEARCHTERM, term);
            }
            return builder.build().toString();
        } catch (URISyntaxException e) {
            LOG.error("Bug: " + basicUrl + " : " + e, e);
            throw new ContextedRuntimeException(e).addContextValue("url", basicUrl)
                    .addContextValue("target", path);
        }
    }

    protected String determineTitle(Resource target) {
        String title = null;
        ResourceHandle targetHandle = ResourceHandle.use(target);
        title = null == title ? targetHandle.getProperty("title") : title;
        title = null == title ? targetHandle.getProperty(PROP_TITLE) : title;
        if (null == title) {
            ResourceHandle contentnode = ResourceHandle.use(targetHandle.getChild(CONTENT_NODE));
            title = null == title ? contentnode.getProperty("title") : title;
            title = null == title ? contentnode.getProperty(PROP_TITLE) : title;
        }
        if (null == title) {
            LOG.info("Cannot determine a search title for ", target);
            title = targetHandle.getResourceTitle(); // resource name as fallback
        }
        return title;
    }

    @ObjectClassDefinition(name = "Composum Pages Search Service Configuration",
            description = "Configurations for the Composum Pages Search Service")
    public @interface PagesSearchServiceConfiguration {

        @AttributeDefinition(name = "Overshoot", description = "Internal tuning property")
        int overshoot() default 3;

        @AttributeDefinition(name = "Maximum number of search matches", description = "Limits the number of search " +
                "matches e.g. if there is an extremely common search term entered.")
        int maximumMatchCount() default 100;

        @AttributeDefinition()
        String webconsole_configurationFactory_nameHint() default
                "{name} (maximumMatchCount: {maximumMatchCount})";
    }

    protected class SubmatchResultImpl implements SearchService.Result {

        private final BeanContext context;
        private final Resource target;
        private final List<Resource> matches;
        private final String searchExpression;
        private final Set<String> positiveTerms;
        private final Float score;
        private String title;
        private String excerpt;

        public SubmatchResultImpl(BeanContext context, Resource target, Float score, List<Resource> matches, String
                searchExpression, Set<String> positiveTerms) {
            this.context = context;
            this.target = target;
            this.score = score;
            this.matches = matches;
            this.searchExpression = searchExpression;
            this.positiveTerms = positiveTerms;
        }

        @Override
        public Resource getTarget() {
            return target;
        }

        @Override
        public Resource getTargetContent() {
            Resource content = target.getChild(JcrConstants.JCR_CONTENT);
            return content != null ? content : target;
        }

        @Override
        public String getTargetUrl() {
            return createTargetUrl(target.getPath(), context.getRequest(), positiveTerms);
        }

        @Override
        public String getTitle() {
            if (null == title) {
                title = determineTitle(target);
            }
            return title;
        }

        @Override
        public Float getScore() {
            return score;
        }

        @Override
        public String getExcerpt() throws SearchTermParseException {
            if (null == excerpt) {
                excerpt = excerptGenerator.excerpt(matches, searchExpression);
            }
            return excerpt;
        }

        @Override
        public List<Resource> getMatches() {
            return matches;
        }

        @Override
        public String getSearchExpression() {
            return searchExpression;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("target", target)
                    .append("matches", matches)
                    .toString();
        }
    }
}
