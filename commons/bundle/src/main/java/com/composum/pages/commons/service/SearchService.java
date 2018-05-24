package com.composum.pages.commons.service;

import com.composum.pages.commons.service.search.SearchTermParseException;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
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
    public static final String PARAMETER_SEARCHTERM = "search.term";

    /**
     * Fulltext search for cpp:Page matching a searchExpression.
     * <p>
     * Limitations: this search is specific for cpp:Page and needs the lucene configured for aggregation of all subnodes
     * into cpp:PageContent , and of all subnodes of a nt:frozenNode into the nt:frozenNode . This improves the search
     * efficiency and enables easily searching for nodes that contain the search words in different subnodes. (Import
     * of aggregates .json into /oak:index/lucene/aggregates ).
     *
     * @param context          The context we use for the search.
     * @param root             Optional parameter for the node below which we search.
     * @param searchExpression Mandatory parameter for the fulltext search expression to search for. For the syntax see
     *                         {@link com.composum.sling.platform.staging.query.QueryConditionDsl.QueryConditionBuilder#contains(String)}
     *                         . It is advisable to avoid using AND and OR.
     * @param offset
     * @param limit @return possibly empty list of results
     * @see "https://jackrabbit.apache.org/oak/docs/query/lucene.html#aggregation"
     */
    @Nonnull
    List<Result> searchPages(@Nonnull BeanContext context, String root, @Nonnull String searchExpression, int offset,
                             Integer limit)
            throws RepositoryException, SearchTermParseException;

    /**
     * Fulltext search for resources. The resources are grouped if they are subresources of one target page, as
     * determined by the parameter targetResourceFilter.
     * <p>
     * Limitations: if the searchExpression consists of several search terms (implicitly combined with AND) this finds
     * only resources where a single property matches the whole search condition, i.e., all those terms. If several
     * resources of a page contain different subsets of those terms, the page is not found.
     *
     * @param context              The context we use for the search.
     * @param root                 Optional parameter for the node below which we search.
     * @param searchExpression     Mandatory parameter for the fulltext search expression to search for. For the syntax
     *                             see
     *                             {@link com.composum.sling.platform.staging.query.QueryConditionDsl.QueryConditionBuilder#contains(String)}
     *                             . It is advisable to avoid using AND and OR.
     * @param targetResourceFilter Optional filter to find the {@link Result#getTarget()} resource for a path: the
     *                             search looks for the first parent (from bottom to top) that matches this filter. If
     *                             no parent matches the page, the target resource is the match itself. Default: a
     *                             filter matching Composum Pages.
     * @param offset
     *@param limit @return possibly empty list of results
     * @see com.composum.sling.core.mapping.jcr.ResourceFilterMapping
     */
    @Nonnull
    List<Result> search(@Nonnull BeanContext context, String root, @Nonnull String searchExpression,
                        ResourceFilter targetResourceFilter, int offset, Integer limit) throws RepositoryException, SearchTermParseException;

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

}
