package com.composum.pages.components.model.search;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.Element;
import com.composum.sling.core.util.LinkUtil;
import org.apache.sling.api.request.RequestParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Models the possible configurations for a search field.
 */
public class SearchField extends Element {

    private static final Logger LOG = LoggerFactory.getLogger(SearchField.class);

    /** Property name for {@link #getButtonText()}. */
    public static final String PROP_BUTTON_TEXT = "buttonText";
    /** Property name for {@link #getButtonSymbol()}. */
    public static final String PROP_BUTTON_SYMBOL = "buttonSymbol";
    public static final String DEFAULT_BUTTON_SYMBOL = "search";
    /** Property name for {@link #getHint()}. */
    public static final String PROP_HINT = "hint";
    /** Property name for {@link #getPlaceholderText()}. */
    public static final String PROP_PLACEHOLDER_TEXT = "placeholderText";
    /** Property name for {@link #getSearchResultPath()}. */
    public static final String PROP_SEARCH_RESULT_PATH = "searchResultPath";
    /** Property name for {@link #getSearchResultAnchor()}. */
    public static final String PROP_SEARCH_RESULT_ANCHOR = "searchResultAnchor";
    /** @see #getButtonText() */
    private transient String buttonText;
    /** @see #getButtonSymbol() */
    private transient String buttonSymbol;
    /** @see #getPlaceholderText() */
    private transient String placeholderText;
    /** @see #getSearchTerm() */
    private transient String searchTerm;
    /** @see #getHint() */
    private transient String hint;
    /** @see #getSearchResultPath() */
    private transient String searchResultPath;
    /** @see #getSearchResultAnchor() */
    private transient String searchResultAnchor;

    /** Text for the search button. */
    public String getButtonText() {
        if (buttonText == null) {
            buttonText = getInherited(PROP_BUTTON_TEXT, "");
        }
        return buttonText;
    }

    /** Symbol for the search button - string:CSS. */
    public String getButtonSymbol() {
        if (buttonSymbol == null) {
            buttonSymbol = getInherited(PROP_BUTTON_SYMBOL, DEFAULT_BUTTON_SYMBOL);
        }
        return buttonSymbol;
    }

    /** The title (mouseover) for the search. */
    public String getHint() {
        if (hint == null) {
            hint = getInherited(PROP_HINT, "");
        }
        return hint;
    }

    /** Text for the placeholder that is shown in the search field before any text is put in. Optional. */
    public String getPlaceholderText() {
        if (placeholderText == null) {
            placeholderText = getInherited(PROP_PLACEHOLDER_TEXT, "");
        }
        return placeholderText;
    }

    /** The fulltext search expression, from the request parameter 'search.term'. */
    public String getSearchTerm() {
        if (searchTerm == null) {
            RequestParameter parameter = getContext().getRequest().getRequestParameter(SearchResult.PARAMETER_TERM);
            if (parameter != null) {
                try {
                    searchTerm = parameter.getString(PagesConstants.ENCODING);
                } catch (UnsupportedEncodingException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
            if (searchTerm == null) {
                searchTerm = "";
            }
        }
        return searchTerm;
    }

    /** Page in which the search result is shown. path:Page, default current page. */
    public String getSearchResultPath() {
        if (searchResultPath == null) {
            searchResultPath = getInherited(PROP_SEARCH_RESULT_PATH, getContainingPage().getPath());
        }
        return searchResultPath;
    }

    /** Anchor in the search result page to jump to for displaying the search result. string:Anchor, optional. */
    public String getSearchResultAnchor() {
        if (searchResultAnchor == null) {
            searchResultAnchor = getInherited(PROP_SEARCH_RESULT_ANCHOR, "");
        }
        return searchResultAnchor;
    }

    /**
     * Constructs the URL for the search result page from {@link #getSearchResultPath()} and {@link
     * #getSearchResultAnchor()}.
     */
    public String getSearchResultLink() throws URISyntaxException {
        StringBuilder buf = new StringBuilder();
        String value;
        if (isNotBlank(value = getSearchResultPath())) {
            buf.append(LinkUtil.getUrl(context.getRequest(), value));
        }
        if (isNotBlank(value = getSearchResultAnchor())) {
            buf.append("#").append(value);
        }
        return buf.toString();
    }
}
