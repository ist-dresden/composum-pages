package com.composum.pages.commons.service.search;

import org.apache.commons.lang3.StringUtils;

/** Is thrown when the search term could not be parsed. */
public class SearchTermParseException extends Exception {

    enum Kind {Empty, NoPositivePhrases, Unparseable}

    private final String searchExpression;

    private final String ununderstandableRest;

    SearchTermParseException(Kind kind, String searchExpression, String ununderstandableRest) {
        super("Could not parse search expression " + searchExpression + " : " + kind +
                " \"" + StringUtils.defaultString(ununderstandableRest) + "\"");
        this.searchExpression = searchExpression;
        this.ununderstandableRest = ununderstandableRest;
    }

    /** The searchexpression that could not be parsed. */
    public String getSearchExpression() {
        return searchExpression;
    }

    /** The rest when the parse error occured. This could not be understood. */
    public String getUnunderstandableRest() {
        return ununderstandableRest;
    }

}
