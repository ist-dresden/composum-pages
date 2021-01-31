package com.composum.pages.commons;

import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import org.apache.sling.api.SlingHttpServletRequest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;

/**
 * The configuration service for the Assets aspect of the Pages implementation.
 */
public interface AssetsConfiguration {

    String ASSET_FILTER_ALL = "all";
    String ASSET_FILTER_ASSET = "asset";
    String ASSET_FILTER_IMAGE = "image";
    String ASSET_FILTER_VIDEO = "video";
    String ASSET_FILTER_AUDIO = "audio";
    String ASSET_FILTER_DOCUMENT = "document";
    String ASSET_FILTER_BINARY = "binary";

    class ConfigurableFilter {

        protected final ResourceFilter filter;
        protected final String key;
        protected final String label;
        protected final String hint;

        public ConfigurableFilter(@Nonnull ResourceFilter filter, @Nonnull String key,
                                  @Nonnull String label, @Nonnull String hint) {
            this.filter = filter;
            this.key = key;
            this.label = label;
            this.hint = hint;
        }

        public ResourceFilter getFilter() {
            return filter;
        }

        public String getKey() {
            return key;
        }

        public String getLabel() {
            return label;
        }

        public String getHint() {
            return hint;
        }
    }

    boolean isAssetsModuleSupport();

    /**
     * @return the set of keys of the available filters for the (tree) views
     */
    @Nonnull
    Set<String> getNodeFilterKeys();

    /**
     * @return the set of available filter configurations for filter menu rendering
     */
    @Nonnull
    Collection<ConfigurableFilter> getNodeFilters();

    /**
     * @param key the filter key from the set of available filters
     * @return the filter instance or a default filter if the instance is not available
     */
    @Nullable
    ResourceFilter getNodeFilter(@Nonnull SlingHttpServletRequest request, @Nonnull String key);

    /**
     * @param request       the current rendering request
     * @param paramName     the name of the filter key URL parameter
     * @param defaultFilter the key of the default filter
     * @return the filter instance according to the parameter value or the default filter key
     */
    @Nonnull
    ResourceFilter getRequestNodeFilter(@Nonnull SlingHttpServletRequest request,
                                        @Nonnull String paramName, @Nonnull String defaultFilter);

    // direct access to the filter instances

    @Nullable /* accessible only if the Composum Assets Module is available */
    ResourceFilter getAssetNodeFilter();

    @Nonnull
    ResourceFilter getImageNodeFilter();

    @Nonnull
    ResourceFilter getVideoNodeFilter();

    @Nonnull
    ResourceFilter getAudioNodeFilter();

    @Nonnull
    ResourceFilter getDocumentNodeFilter();

    @Nonnull
    ResourceFilter getBinaryNodeFilter();

    @Nonnull
    ResourceFilter getAnyNodeFilter();

    @Nullable
    ResourceFilter getAssetFileFilter();

    @Nonnull
    ResourceFilter getImageFileFilter();

    @Nonnull
    ResourceFilter getVideoFileFilter();

    @Nonnull
    ResourceFilter getAudioFileFilter();

    @Nonnull
    ResourceFilter getDocumentFileFilter();

    @Nonnull
    ResourceFilter getBinaryFileFilter();

    @Nonnull
    ResourceFilter getAnyFileFilter();

    // configured node filters

    @Nonnull
    Set<String> getFileFilterKeys();

    @Nullable
    ResourceFilter getFileFilter(@Nonnull BeanContext context, @Nonnull String key);
}
