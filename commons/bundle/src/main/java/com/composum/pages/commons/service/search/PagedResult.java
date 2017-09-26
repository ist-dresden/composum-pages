package com.composum.pages.commons.service.search;

import javax.annotation.CheckForNull;
import java.util.List;

/**
 * Encapsulates a list of something that has to be displayed in pages, e.g. a search result with potentially lots of
 * hits.
 *
 * @param <T> the type of item to display
 * @author Hans-Peter Stoerr
 */
public class PagedResult<T> {

    /** @see #getResults() */
    private final List<T> results;
    /** @see #getPageSize() */
    private final int pageSize;
    /** @see #getPageCount() */
    private final Integer pageCount;
    /** @see #getLastPage() */
    private final boolean lastPage;
    /** @see #getPageNumber() */
    private final int pageNumber;

    /**
     * Instantiates a new Paged result.
     *
     * @param results    the results
     * @param pageNumber the page number
     * @param pageSize   the page size
     * @param pageCount  the full page count, if known
     * @param lastPage   whether this is the last page
     */
    public PagedResult(List<T> results, int pageNumber, int pageSize, Integer pageCount, boolean lastPage) {
        this.results = results;
        this.pageSize = pageSize;
        this.pageCount = pageCount;
        this.lastPage = lastPage;
        this.pageNumber = pageNumber;
    }

    /** The results contained in the current page. */
    public List<T> getResults() {
        return results;
    }

    /** The number of results in one page. */
    public int getPageSize() {
        return pageSize;
    }

    /** If known, the number of pages for presenting all results. */
    @CheckForNull
    public Integer getPageCount() {
        return pageCount;
    }

    /** If true, this is the last page of the results. */
    public boolean getLastPage() {
        return lastPage;
    }

    /** The number of the current page, starting with 1. */
    public int getPageNumber() {
        return pageNumber;
    }

}
