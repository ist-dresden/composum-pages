package com.composum.pages.commons.service.search;

import org.apache.sling.api.resource.Resource;

import java.util.List;
import java.util.Set;

/**
 * Generates an excerpt for the search service.
 */
public interface ExcerptGenerator {

    /**
     * Generates an excerpt of the text of a resource wrt. JCR query fulltext search expression to display the context
     * where the search terms occur.
     *
     * @return an excerpt (HTML) or the empty string
     */
    String excerpt(Resource resource, String searchExpression) throws SearchTermParseException;

    /**
     * Generates an excerpt of the text of a set of associated resources wrt. JCR query fulltext search expression to
     * display the context where the search terms occur.
     *
     * @return an excerpt (HTML) or the empty string
     */
    String excerpt(List<Resource> resources, String searchExpression) throws SearchTermParseException;
}
