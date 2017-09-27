package com.composum.pages.components.model.search;

import com.composum.pages.commons.model.Element;

import java.net.URI;
import java.net.URISyntaxException;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Models the possible configurations for a search field.
 */
public class SearchField extends Element {

    /** Property name for {@link #getButtonText()}. */
    public static final String PROP_BUTTON_TEXT = "buttonText";
    /** Property name for {@link #getButtonSymbol()}. */
    public static final String PROP_BUTTON_SYMBOL = "buttonSymbol";
    /** Property name for {@link #getButtonImage()}. */
    public static final String PROP_BUTTON_IMAGE = "buttonImage";
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
    /** @see #getButtonImage() */
    private transient String buttonImage;
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
        if (isBlank(getButtonImage()) && isBlank(getButtonSymbol())) return buttonText;
        return "";
    }

    /** Symbol for the search button - string:CSS. */
    public String getButtonSymbol() {
        if (buttonSymbol == null) {
            if (isNotBlank(getButtonImage())) buttonSymbol = "";
            else buttonSymbol = getInherited(PROP_BUTTON_SYMBOL, "");
        }
        return buttonSymbol;
    }

    /** Image for the search button, overrides {@link #buttonSymbol} if present. */
    public String getButtonImage() {
        if (buttonImage == null) {
            buttonImage = getInherited(PROP_BUTTON_IMAGE, "");
        }
        return buttonImage;
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
    public URI getSearchResultLink() throws URISyntaxException {
        StringBuilder buf = new StringBuilder();
        if (isNotBlank(getSearchResultPath())) buf.append(getSearchResultPath()).append(".html");
        if (isNotBlank(getSearchResultAnchor())) buf.append("#").append(getSearchResultAnchor());
        return new URI(buf.toString());

    }

}
