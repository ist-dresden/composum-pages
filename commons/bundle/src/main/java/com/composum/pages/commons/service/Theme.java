package com.composum.pages.commons.service;

import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;

/**
 * a Theme in the Pages context implements a strategy to modify the markup rendering
 * and to customize the page clientlibs without content changes; such a theme supports
 * the use of standard components with modified markup and a style suitable to this markup
 */
public interface Theme extends Comparable<Theme> {

    /**
     * @return the unique identifier of the theme (e.g. the path of the themes configuration root)
     */
    @Nonnull
    String getName();

    /**
     * @return the more readable title to display the theme
     */
    @Nonnull
    String getTitle();

    @Override
    default int compareTo(@Nonnull Theme other) {
        return getTitle().compareTo(other.getTitle());
    }

    /**
     * the themes resource type transformation for component rendering
     *
     * @param resource     the resource to render
     * @param resourceType the resource type to use by default
     * @return the resource type to use for rendering in the themes context
     */
    @Nonnull
    String getResourceType(@Nonnull Resource resource, @Nonnull String resourceType);

    /**
     * the themes template path transformation for a content page
     *
     * @param pageResource the resource of the content page
     * @param templatePath the path of the template to overlay
     * @return the template path to use
     */
    @Nonnull
    String getPageTemplate(@Nonnull Resource pageResource, @Nonnull String templatePath);

    /**
     * the themes clientlib category transformation for page rendering
     *
     * @param pageResource      the resource ot the content page to render
     * @param clientlibCategory the clientlib category to use by default
     * @return the clientlib category to use for page rendering in the themes context
     */
    @Nonnull
    String getClientlibCategory(@Nonnull Resource pageResource, @Nonnull String clientlibCategory);
}
