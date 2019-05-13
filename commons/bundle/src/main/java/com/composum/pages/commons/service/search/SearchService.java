/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.service.search;

import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.RepositoryException;
import java.util.List;

/**
 * Service for fulltext search.
 *
 * @author Hans-Peter Stoerr
 */
public interface SearchService {

    /**
     * Parameter that is appended to the generated links that contains the positive search terms and phrases of the
     * search expression. For several terms it occurs several times.
     */
    String PARAMETER_SEARCHTERM = "search.term";

    /**
     * Represents a result of the search consisting of a target page and one or more matching subresources. For use by
     * the search result renderer.
     */
    interface Result {

        /** The page which contains matches. */
        @Nonnull
        Resource getTarget();

        /** The content child of the page which contains matches. */
        @Nonnull
        Resource getTargetContent();

        /** A link that shows the target, including search terms with {@link #PARAMETER_SEARCHTERM} */
        @Nonnull
        String getTargetUrl();

        /** The title of the search result. */
        @Nonnull
        String getTitle();

        /** The score of the search result. */
        Float getScore();

        /**
         * One or more descendants of {@link #getTarget()} that potentially match the search expression. Mostly useful
         * for generating excerpts; can contain false positives in some search algorithms.
         */
        @Nonnull
        List<Resource> getMatches();

        /** The fulltext search expression for which this result was found. */
        @Nonnull
        String getSearchExpression();

        /**
         * A basic excerpt from the matches that demonstrates the occurrences of the terms from {@link
         * #getSearchExpression()} in this result. Might be empty if not applicable (e.g. if the search terms were found
         * in meta information). If there are several matches, we just give one excerpt. You might want to provide your
         * own implementation for that to accommodate for specific requirements.
         *
         * @return a text with the occurrences of the words marked with HTML tag em .
         */
        @Nonnull
        String getExcerpt() throws SearchTermParseException;
    }

    String SELECTOR_PAGE = "page";

    /**
     * Fulltext search for resources. The resources are grouped if they are subresources of one target page, as
     * determined by the parameter targetResourceFilter.
     * <p>
     * Limitations: if the searchExpression consists of several search terms (implicitly combined with AND) this finds
     * only resources where a single property matches the whole search condition, i.e., all those terms. If several
     * resources of a page contain different subsets of those terms, the page is not found.
     *
     * @param context          The context we use for the search.
     * @param selectors        a selector string to determine the right search strategy, e.g. 'page'
     * @param root             Optional parameter for the node below which we search.
     * @param searchExpression Mandatory parameter for the fulltext search expression to search for. For the syntax
     *                         see
     *                         {@link com.composum.sling.platform.staging.query.QueryConditionDsl.QueryConditionBuilder#contains(String)}
     *                         . It is advisable to avoid using AND and OR.
     * @param searchFilter     an optional filter to drop resources to ignore.
     * @return possibly empty list of results
     * @see com.composum.sling.core.mapping.jcr.ResourceFilterMapping
     */
    @Nonnull
    List<Result> search(@Nonnull BeanContext context, @Nonnull String selectors,
                        @Nonnull String root, @Nonnull String searchExpression, @Nullable ResourceFilter searchFilter,
                        int offset, @Nullable Integer limit)
            throws RepositoryException, SearchTermParseException;


    interface LimitedQuery {

        /**
         * Executes the query with the given limit; returns a pair of a boolean that is true when we are sure that all
         * results have been found in spite of the limit, and the results themselves.
         */
        Pair<Boolean, List<Result>> execQuery(int matchLimit);
    }

    /**
     * Execute the query with raising limit until the required number of results is met. We don't know in advance how
     * large we have to set the limit in the query to get all neccesary results, since each page can have an a priori
     * unknown number of matches. Thus, the query is executed with an estimated limit, and is reexecuted with tripled
     * limit if the number of results is not sufficient and there are more limits.
     *
     * @return up to limit elements of the result list with the offset first elements skipped.
     */
    @Nonnull
    List<Result> executeQueryWithRaisingLimits(PagesSearchService.LimitedQuery limitedQuery, int offset, Integer limit);
}
