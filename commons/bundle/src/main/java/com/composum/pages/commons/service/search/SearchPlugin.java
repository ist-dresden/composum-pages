/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.service.search;

import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.RepositoryException;
import java.util.List;

/**
 * the service interface to implement search strategy services for various request selectors
 */
public interface SearchPlugin {

    int rating(@Nonnull String selectors);

    @Nonnull
    List<SearchService.Result> search(@Nonnull final BeanContext context, @Nonnull final String root,
                                      @Nonnull final String searchExpression, @Nullable ResourceFilter filter,
                                      final int offset, @Nullable final Integer limit)
            throws RepositoryException, SearchTermParseException;

    void setService(@Nullable SearchService service);
}
