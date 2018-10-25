/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.service.search;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import javax.annotation.Nonnull;

/**
 * Service for fulltext general resource search.
 *
 * @author Hans-Peter Stoerr
 */
@Component(
        service = SearchPlugin.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Pages Default Resource Search Plugin"
        },
        immediate = true
)
public class ResourceSearchPlugin extends AbstractSearchPlugin {

    @Override
    public int rating(@Nonnull String selectors) {
        return 1;
    }
}
