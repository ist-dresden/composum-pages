/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons;

import com.composum.pages.commons.util.RequestUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.mapping.jcr.ResourceFilterMapping;
import org.apache.sling.api.SlingHttpServletRequest;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * The configuration service for the integration of Assets into Pages.
 */
@Component(
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Assets Configuration"
        }
)
@Designate(ocd = AssetsConfigImpl.Configuration.class)
public class AssetsConfigImpl implements AssetsConfiguration {

    public static final String ASSETS_MODULE_CONFIG_CLASS = "com.composum.assets.commons.AssetsConfiguration";

    @ObjectClassDefinition(
            name = "Composum Pages Asset Configuration"
    )
    public @interface Configuration {

        @AttributeDefinition(
                description = "the switch to enable the Assets Module extension"
        )
        boolean assetModuleEnabled() default false;

        @AttributeDefinition(
                description = "the filter configuration to set the scope to the Composum Assets objects"
        )
        String assetNodeFilterRule() default "PrimaryType(+'^cpa:(Asset)$')";

        @AttributeDefinition(
                description = "the filter configuration to set the scope to image files"
        )
        String imageNodeFilterRule() default "and{PrimaryType(+'^nt:(file)$'),MimeType(+'^image/')}";

        @AttributeDefinition(
                description = "the filter configuration to set the scope to video files"
        )
        String videoNodeFilterRule() default "and{PrimaryType(+'^nt:(file)$'),MimeType(+'^video/')}";

        @AttributeDefinition(
                description = "the filter configuration to set the scope to audio files"
        )
        String audioNodeFilterRule() default "and{PrimaryType(+'^nt:(file)$'),MimeType(+'^audio/')}";

        @AttributeDefinition(
                description = "the filter configuration to set the scope to document files"
        )
        String documentNodeFilterRule() default "and{PrimaryType(+'^nt:(file)$'),MimeType(+'^application/(pdf)')}";

        @AttributeDefinition(
                description = "the filter configuration to set the scope to binary files"
        )
        String binaryNodeFilterRule() default "and{PrimaryType(+'^nt:(file)$'),MimeType(+'^application/(zip)')}";

        @AttributeDefinition(
                description = "the filter configuration to set the scope all files"
        )
        String fileNodeFilterRule() default "PrimaryType(+'^nt:(file)$')";

        @AttributeDefinition(
                description = "the filter configuration for tree itermediate nodes (folders, sites, ...)"
        )
        String treeIntermediateFilterRule() default "or{Folder(),PrimaryType(+'^cpp:(Site)$')}";
    }

    @Reference
    protected PagesConfiguration pagesConfiguration;

    private Object assetsModuleConfig;

    private ResourceFilter assetNodeFilter;
    private ResourceFilter imageNodeFilter;
    private ResourceFilter videoNodeFilter;
    private ResourceFilter audioNodeFilter;
    private ResourceFilter documentNodeFilter;
    private ResourceFilter binaryNodeFilter;
    private ResourceFilter anyNodeFilter;

    private ResourceFilter assetFileFilter;
    private ResourceFilter imageFileFilter;
    private ResourceFilter videoFileFilter;
    private ResourceFilter audioFileFilter;
    private ResourceFilter documentFileFilter;
    private ResourceFilter binaryFileFilter;
    private ResourceFilter anyFileFilter;

    private Map<String, ConfigurableFilter> availableFilters;
    private Map<String, ResourceFilter> fileFilters;

    protected Configuration config;

    @Override
    public boolean isAssetsModuleSupport() {
        return config.assetModuleEnabled() && assetsModuleConfig != null;
    }

    // asset resource filters (file filters)

    @Nonnull
    @Override
    public Set<String> getFileFilterKeys() {
        return fileFilters.keySet();
    }

    @Nullable
    @Override
    public ResourceFilter getFileFilter(@Nonnull final BeanContext context, @Nonnull String key) {
        return fileFilters.get(key);
    }

    @Nullable
    @Override
    public ResourceFilter getAssetFileFilter() {
        return assetFileFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getImageFileFilter() {
        return imageFileFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getVideoFileFilter() {
        return videoFileFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getAudioFileFilter() {
        return audioFileFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getDocumentFileFilter() {
        return documentFileFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getBinaryFileFilter() {
        return binaryFileFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getAnyFileFilter() {
        return anyFileFilter;
    }

    // configured node filters

    @Nonnull
    @Override
    public Set<String> getNodeFilterKeys() {
        return availableFilters.keySet();
    }

    @Nonnull
    @Override
    public Collection<ConfigurableFilter> getNodeFilters() {
        return availableFilters.values();
    }

    @Nonnull
    @Override
    public ResourceFilter getNodeFilter(@Nonnull SlingHttpServletRequest request, @Nonnull String key) {
        ConfigurableFilter filter = availableFilters.get(key);
        return filter != null ? filter.filter
                : ASSET_FILTER_ASSET.equals(key) ? imageNodeFilter : anyNodeFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getRequestNodeFilter(@Nonnull SlingHttpServletRequest request,
                                               @Nonnull String paramName, @Nonnull String defaultFilter) {
        String filter = RequestUtil.getParameter(request, paramName, defaultFilter);
        return getNodeFilter(request, filter);
    }

    @Nullable
    @Override
    public ResourceFilter getAssetNodeFilter() {
        return assetNodeFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getImageNodeFilter() {
        return imageNodeFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getVideoNodeFilter() {
        return videoNodeFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getAudioNodeFilter() {
        return audioNodeFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getDocumentNodeFilter() {
        return documentNodeFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getBinaryNodeFilter() {
        return binaryNodeFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getAnyNodeFilter() {
        return anyNodeFilter;
    }

    /**
     * Creates a 'tree filter' as combination with the configured filter and the rules for the
     * 'intermediate' nodes (folders) to traverse up to the target nodes.
     *
     * @param configuredFilter the filter for the target nodes
     */
    protected ResourceFilter buildTreeFilter(ResourceFilter configuredFilter,
                                             ResourceFilter intermediateFilter) {
        return new ResourceFilter.FilterSet(
                ResourceFilter.FilterSet.Rule.tree, configuredFilter, intermediateFilter);
    }

    @Activate
    @Modified
    protected void activate(BundleContext bundleContext, Configuration config) {
        this.config = config;
        this.fileFilters = new LinkedHashMap<>();
        this.availableFilters = new LinkedHashMap<>();
        ResourceFilter replicationRootFilter = pagesConfiguration.getContentRootFilter();
        assetsModuleConfig = null;
        ServiceReference<?> serviceReference = bundleContext.getServiceReference(ASSETS_MODULE_CONFIG_CLASS);
        if (serviceReference != null) {
            assetsModuleConfig = bundleContext.getService(serviceReference);
        }
        anyFileFilter = ResourceFilterMapping.fromString(config.fileNodeFilterRule());
        imageFileFilter = ResourceFilterMapping.fromString(config.imageNodeFilterRule());
        videoFileFilter = ResourceFilterMapping.fromString(config.videoNodeFilterRule());
        audioFileFilter = ResourceFilterMapping.fromString(config.audioNodeFilterRule());
        documentFileFilter = ResourceFilterMapping.fromString(config.documentNodeFilterRule());
        binaryFileFilter = ResourceFilterMapping.fromString(config.binaryNodeFilterRule());
        ResourceFilter treeIntermediateFilter = new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.or,
                pagesConfiguration.getTreeIntermediateFilter(),
                pagesConfiguration.getSiteNodeFilter());
        imageNodeFilter = buildTreeFilter(new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.and,
                replicationRootFilter, imageFileFilter), treeIntermediateFilter);
        videoNodeFilter = buildTreeFilter(new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.and,
                replicationRootFilter, videoFileFilter), treeIntermediateFilter);
        audioNodeFilter = buildTreeFilter(new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.and,
                replicationRootFilter, audioFileFilter), treeIntermediateFilter);
        documentNodeFilter = buildTreeFilter(new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.and,
                replicationRootFilter, documentFileFilter), treeIntermediateFilter);
        binaryNodeFilter = buildTreeFilter(new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.and,
                replicationRootFilter, binaryFileFilter), treeIntermediateFilter);
        if (config.assetModuleEnabled() && assetsModuleConfig != null) {
            assetFileFilter = /*new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.or,*/
                    ResourceFilterMapping.fromString(config.assetNodeFilterRule())/*,
                    imageFileFilter)*/;//FIXME
            assetNodeFilter = buildTreeFilter(new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.and,
                    replicationRootFilter, assetFileFilter), treeIntermediateFilter);
            anyNodeFilter = buildTreeFilter(new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.and,
                    replicationRootFilter, assetFileFilter, anyFileFilter), treeIntermediateFilter);
        } else {
            assetFileFilter = null;
            assetNodeFilter = null;
            anyNodeFilter = buildTreeFilter(new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.and,
                    replicationRootFilter, anyFileFilter), treeIntermediateFilter);
        }
        fileFilters.put(ASSET_FILTER_ALL, anyFileFilter);
        availableFilters.put(ASSET_FILTER_ALL, new ConfigurableFilter(anyNodeFilter,
                ASSET_FILTER_ALL, "All", "show all available asset object types"));
        if (assetNodeFilter != null) {
            fileFilters.put(ASSET_FILTER_ASSET, assetFileFilter);
            availableFilters.put(ASSET_FILTER_ASSET, new ConfigurableFilter(assetNodeFilter,
                    ASSET_FILTER_ASSET, "Asset", "restrict to 'Composum Assets' objects"));
        }
        fileFilters.put(ASSET_FILTER_IMAGE, imageFileFilter);
        availableFilters.put(ASSET_FILTER_IMAGE, new ConfigurableFilter(imageNodeFilter,
                ASSET_FILTER_IMAGE, "Image", "restrict to image file objects"));
        fileFilters.put(ASSET_FILTER_VIDEO, videoFileFilter);
        availableFilters.put(ASSET_FILTER_VIDEO, new ConfigurableFilter(videoNodeFilter,
                ASSET_FILTER_VIDEO, "Video", "restrict to video file objects"));
        fileFilters.put(ASSET_FILTER_AUDIO, audioFileFilter);
        availableFilters.put(ASSET_FILTER_AUDIO, new ConfigurableFilter(audioNodeFilter,
                ASSET_FILTER_AUDIO, "Audio", "restrict to audio file objects"));
        fileFilters.put(ASSET_FILTER_DOCUMENT, documentFileFilter);
        availableFilters.put(ASSET_FILTER_DOCUMENT, new ConfigurableFilter(documentNodeFilter,
                ASSET_FILTER_DOCUMENT, "Document", "restrict to document file objects"));
        fileFilters.put(ASSET_FILTER_BINARY, binaryFileFilter);
        availableFilters.put(ASSET_FILTER_BINARY, new ConfigurableFilter(binaryNodeFilter,
                ASSET_FILTER_BINARY, "Binary", "restrict to binary file objects"));
    }

    @Deactivate
    protected void deactivate(ComponentContext context) {
        this.availableFilters = null;
        this.config = null;
    }
}
