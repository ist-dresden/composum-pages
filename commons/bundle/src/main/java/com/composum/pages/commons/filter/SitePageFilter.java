/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.filter;

import com.composum.sling.core.filter.ResourceFilter;
import org.apache.sling.api.resource.Resource;

/**
 * a page filter to restrict page resources to pages of a defined (current) site
 */
public class SitePageFilter extends ResourceFilter.AbstractResourceFilter {

    protected final String sitePath;
    protected final ResourceFilter pageFilter;

    public SitePageFilter(String sitePath, ResourceFilter pageFilter) {
        this.sitePath = sitePath.endsWith("/") ? sitePath : sitePath + "/";
        this.pageFilter = pageFilter;
    }

    @Override
    public boolean accept(Resource resource) {
        return resource.getPath().startsWith(sitePath) && pageFilter.accept(resource);
    }

    @Override
    public boolean isRestriction() {
        return false;
    }

    @Override
    public void toString(StringBuilder builder) {
        builder.append(SitePageFilter.class.getSimpleName()).append("(").append(sitePath).append(")");
    }
}
