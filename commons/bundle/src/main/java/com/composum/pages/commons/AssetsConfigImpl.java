/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons;

import com.composum.pages.commons.util.RequestUtil;
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

    public static final String FILTER_ALL = "all";
    public static final String FILTER_ASSET = "asset";
    public static final String FILTER_IMAGE = "image";
    public static final String FILTER_VIDEO = "video";

    public static final String ASSETS_MODULE_CONFIG_CLASS = "com.composum.assets.commons.AssetsConfiguration";

    @ObjectClassDefinition(
            name = "Composum Pages Asset Configuration"
    )
    public @interface Configuration {

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
                description = "the filter configuration to set the scope to video files"
        )
        String treeIntermediateFilterRule() default "and{or{Folder(),PrimaryType(+'^cpp:(Site)$')},Path(-'^/(etc|conf|apps|libs|sightly|htl|var)')}";
    }

    @Reference
    protected PagesConfiguration pagesConfiguration;

    private ResourceFilter assetNodeFilter;
    private ResourceFilter imageNodeFilter;
    private ResourceFilter videoNodeFilter;
    private ResourceFilter anyAssetFilter;

    private Map<String, ConfigurableFilter> availableFilters;

    protected Configuration config;

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
    public ResourceFilter getNodeFilter(@Nonnull String key) {
        ConfigurableFilter filter = availableFilters.get(key);
        return filter != null ? filter.filter
                : FILTER_ASSET.equals(key) ? imageNodeFilter : anyAssetFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getRequestNodeFilter(@Nonnull SlingHttpServletRequest request,
                                               @Nonnull String paramName, @Nonnull String defaultFilter) {
        String filter = RequestUtil.getParameter(request, paramName, defaultFilter);
        return getNodeFilter(filter);
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
    public ResourceFilter getAnyAssetFilter() {
        return anyAssetFilter;
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
        this.availableFilters = new LinkedHashMap<>();
        ResourceFilter replicationRootFilter = pagesConfiguration.getReplicationRootFilter();
        Object assetsModuleConfig = null;
        ServiceReference serviceReference = bundleContext.getServiceReference(ASSETS_MODULE_CONFIG_CLASS);
        if (serviceReference != null) {
            assetsModuleConfig = bundleContext.getService(serviceReference);
        }
        ResourceFilter treeIntermediateFilter = new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.or,
                pagesConfiguration.getTreeIntermediateFilter(),
                pagesConfiguration.getSiteNodeFilter());
        imageNodeFilter = buildTreeFilter(new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.and,
                        replicationRootFilter,
                        ResourceFilterMapping.fromString(config.imageNodeFilterRule())),
                treeIntermediateFilter);
        videoNodeFilter = buildTreeFilter(new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.and,
                        replicationRootFilter,
                        ResourceFilterMapping.fromString(config.videoNodeFilterRule())),
                treeIntermediateFilter);
        if (assetsModuleConfig != null) {
            assetNodeFilter = buildTreeFilter(new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.and,
                            replicationRootFilter,
                            ResourceFilterMapping.fromString(config.assetNodeFilterRule())),
                    treeIntermediateFilter);
            anyAssetFilter = buildTreeFilter(new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.and,
                            replicationRootFilter,
                            new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.or,
                                    ResourceFilterMapping.fromString(config.assetNodeFilterRule()),
                                    ResourceFilterMapping.fromString(config.imageNodeFilterRule()),
                                    ResourceFilterMapping.fromString(config.videoNodeFilterRule()))),
                    treeIntermediateFilter);
        } else {
            assetNodeFilter = null;
            anyAssetFilter = buildTreeFilter(new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.and,
                            replicationRootFilter,
                            new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.or,
                                    ResourceFilterMapping.fromString(config.imageNodeFilterRule()),
                                    ResourceFilterMapping.fromString(config.videoNodeFilterRule()))),
                    treeIntermediateFilter);
        }
        availableFilters.put(FILTER_ALL, new ConfigurableFilter(anyAssetFilter,
                FILTER_ALL, "All", "show all available asset object types"));
        if (assetNodeFilter != null) {
            availableFilters.put(FILTER_ASSET, new ConfigurableFilter(assetNodeFilter,
                    FILTER_ASSET, "Asset", "restrict to 'Composum Assets' objects"));
        }
        availableFilters.put(FILTER_IMAGE, new ConfigurableFilter(imageNodeFilter,
                FILTER_IMAGE, "Image", "restrict to image file objects"));
        availableFilters.put(FILTER_VIDEO, new ConfigurableFilter(videoNodeFilter,
                FILTER_VIDEO, "Video", "restrict to video file objects"));
    }

    @Deactivate
    protected void deactivate(ComponentContext context) {
        this.availableFilters = null;
        this.config = null;
    }
}
