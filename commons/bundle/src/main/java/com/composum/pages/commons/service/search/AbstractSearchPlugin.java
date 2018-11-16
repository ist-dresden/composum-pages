package com.composum.pages.commons.service.search;

import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.pages.commons.util.LinkUtil;
import com.composum.sling.platform.staging.query.Query;
import com.composum.sling.platform.staging.query.QueryBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.RepositoryException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.composum.pages.commons.service.search.SearchService.PARAMETER_SEARCHTERM;
import static com.composum.pages.commons.service.search.SearchTermParseException.Kind.Empty;
import static com.composum.pages.commons.service.search.SearchTermParseException.Kind.NoPositivePhrases;
import static com.composum.sling.core.util.ResourceUtil.CONTENT_NODE;
import static com.composum.sling.core.util.ResourceUtil.PROP_TITLE;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.jackrabbit.JcrConstants.JCR_SCORE;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * base implementation for fulltext resource search.
 *
 * @author Hans-Peter Stoerr
 */
public abstract class AbstractSearchPlugin implements SearchPlugin {

    private static final Logger LOG = getLogger(AbstractSearchPlugin.class);

    protected ExcerptGenerator excerptGenerator = new ExcerptGeneratorImpl();

    protected SearchService searchService;

    @Override
    public void setService(@Nullable SearchService service) {
        searchService = service;
    }

    protected ResourceFilter getTargetFilter() {
        return null;
    }

    protected void buildQuery(Query query, String root, String searchExpression) {
        query.path(root).orderBy(JCR_SCORE).descending();
        if (StringUtils.isNotBlank(searchExpression)) {
            String namePattern = searchExpression.replace('*', '%');
            if (!namePattern.startsWith("%")) {
                namePattern = "%" + namePattern;
            }
            if (!namePattern.endsWith("%")) {
                namePattern = namePattern + "%";
            }
            query.condition(query.conditionBuilder()
                    .name().like().val(namePattern)
                    .or()
                    .contains(searchExpression));
        }
    }

    @Nonnull
    @Override
    public List<SearchService.Result> search(@Nonnull final BeanContext context, @Nonnull final String root,
                                             @Nonnull final String searchExpression, @Nullable final ResourceFilter filter,
                                             final int offset, @Nullable final Integer limit)
            throws RepositoryException, SearchTermParseException {
        if (isBlank(searchExpression)) throw new SearchTermParseException(Empty,
                searchExpression, searchExpression);
        final Set<String> positiveTerms = new SearchtermParser(searchExpression).getPositiveSearchterms();
        if (positiveTerms.isEmpty()) throw new SearchTermParseException(NoPositivePhrases,
                searchExpression, searchExpression);

        SearchService.LimitedQuery limitedQuery = new SearchService.LimitedQuery() {
            @Override
            public Pair<Boolean, List<SearchService.Result>> execQuery(int matchLimit) throws RepositoryException {
                Query query = context.getResolver().adaptTo(QueryBuilder.class).createQuery();
                buildQuery(query, root, searchExpression);
                query.limit(matchLimit);

                final int neededResults = null != limit ? offset + limit : Integer.MAX_VALUE;
                int rowcount = 0;

                ResourceFilter targetFilter = getTargetFilter();
                List<SearchService.Result> results = new ArrayList<>();
                Map<String, SubmatchResultImpl> targetToResultMap = new HashMap<>();
                for (Resource match : query.execute()) {
                    rowcount++;
                    Resource target = findTarget(match, targetFilter);
                    if (filter == null || filter.accept(target)) {
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
                }
                return Pair.of(rowcount < matchLimit, results);
            }
        };
        return searchService.executeQueryWithRaisingLimits(limitedQuery, offset, limit);
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

        @Nonnull
        @Override
        public Resource getTarget() {
            return target;
        }

        @Nonnull
        @Override
        public Resource getTargetContent() {
            Resource content = target.getChild(JcrConstants.JCR_CONTENT);
            return content != null ? content : target;
        }

        @Nonnull
        @Override
        public String getTargetUrl() {
            return createTargetUrl(target.getPath(), context.getRequest(), positiveTerms);
        }

        @Nonnull
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

        @Nonnull
        @Override
        public String getExcerpt() throws SearchTermParseException {
            if (null == excerpt) {
                excerpt = excerptGenerator.excerpt(matches, searchExpression);
            }
            return excerpt;
        }

        @Nonnull
        @Override
        public List<Resource> getMatches() {
            return matches;
        }

        @Nonnull
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

    protected Resource findTarget(Resource resource, ResourceFilter
            targetResourceFilter) {
        Resource target = resource;
        if (targetResourceFilter != null) {
            while (null != target && !targetResourceFilter.accept(target)) target = target.getParent();
        }
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
        ResourceHandle targetHandle = ResourceHandle.use(target);
        String title = targetHandle.getProperty("title");
        title = null == title ? targetHandle.getProperty(PROP_TITLE) : title;
        if (null == title) {
            ResourceHandle contentnode = ResourceHandle.use(targetHandle.getChild(CONTENT_NODE));
            title = contentnode.getProperty("title");
            title = null == title ? contentnode.getProperty(PROP_TITLE) : title;
        }
        if (null == title) {
            LOG.info("Cannot determine a search title for ", target);
            title = targetHandle.getResourceTitle(); // resource name as fallback
        }
        return title;
    }
}
