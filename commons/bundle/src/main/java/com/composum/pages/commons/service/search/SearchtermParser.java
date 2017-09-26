package com.composum.pages.commons.service.search;


import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.composum.pages.commons.service.search.SearchTermParseException.Kind.*;
import static java.util.Arrays.*;

/**
 * Parses a simple search language that contains of a space separated list of searchterms, negated searchterms (starting
 * with -) and phrases delimited with " or ' .
 */
public class SearchtermParser {

    /** \s+|(?:([^'"-][^'"\s]*)|-([^'"\s]+)|'([^']+)'|"([^"]+)"|-'([^']+)'|-"([^"]+)")(?=\s|$) */
    protected final Pattern termTypePattern = Pattern.compile(
            "\\s+|(?:([^\"-][^\"\\s]*)|-([^\"\\s]+)|\"([^\"]+)\"|-\"([^\"]+)\")(?=\\s|$)"
    );

    /**
     * A list of terms or phrases that have to occur in the searched items. The words and phrases have no delimiters.
     */
    protected final Set<String> positiveSearchterms = new LinkedHashSet<>();

    /**
     * A list of terms or phrases that most not occur in the searched items. The words and phrases have no delimiters.
     */
    protected final Set<String> negativeSearchterms = new LinkedHashSet<>();

    /** Instantiates a new Searchterm parser. */
    public SearchtermParser(String searchExpression) throws SearchTermParseException {
        if (StringUtils.isBlank(searchExpression))
            throw new SearchTermParseException(Empty, searchExpression, searchExpression);
        parse(searchExpression);
        if (positiveSearchterms.isEmpty())
            throw new SearchTermParseException(NoPositivePhrases, searchExpression, searchExpression);
    }

    protected void parse(String searchExpression) throws SearchTermParseException {
        String restExpression = searchExpression;
        while (StringUtils.isNotEmpty(restExpression)) {
            Matcher m = termTypePattern.matcher(restExpression);
            if (!m.find() || 0 != m.start())
                throw new SearchTermParseException(Unparseable, searchExpression, restExpression);
            if (null != m.group(1)) positiveSearchterms.add(m.group(1));
            if (null != m.group(2)) negativeSearchterms.add(m.group(2));
            if (null != m.group(3)) positiveSearchterms.add(m.group(3));
            if (null != m.group(4)) negativeSearchterms.add(m.group(4));
            restExpression = restExpression.substring(m.end());
        }
        // if the user uses the full query language, these operators would be wrongly identified as search terms here.
        positiveSearchterms.removeAll(asList("AND", "And", "and", "OR", "Or", "or"));
    }

    /** All words and phrases (without quotes) that have to occur in the result. */
    public Set<String> getPositiveSearchterms() {
        return positiveSearchterms;
    }

    /** All words and phrases (without quotes) that should not occur in the result. */
    public Set<String> getNegativeSearchterms() {
        return negativeSearchterms;
    }
}
