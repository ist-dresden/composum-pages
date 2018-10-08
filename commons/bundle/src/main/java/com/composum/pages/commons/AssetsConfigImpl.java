package com.composum.pages.commons;

import com.composum.pages.commons.util.RequestUtil;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.mapping.jcr.ResourceFilterMapping;
import org.apache.sling.api.SlingHttpServletRequest;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import static com.composum.pages.commons.PagesConfigImpl.REPLICATION_ROOT_FILTER;

/**
 * The configuration service for all servlets in the pages bundle.
 */
@Component(
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Assets Configuration"
        }
)
@Designate(ocd = AssetsConfigImpl.PagesAssetsConfiguration.class)
public class AssetsConfigImpl implements AssetsConfiguration {

    @ObjectClassDefinition(
            name = "Pages Asset Configuration"
    )
    public @interface PagesAssetsConfiguration {

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

    protected PagesAssetsConfiguration config;

    public PagesAssetsConfiguration getConfig() {
        return config;
    }

    @Override
    public ResourceFilter getAssetNodeFilter() {
        return assetNodeFilter;
    }

    @Override
    public ResourceFilter getImageNodeFilter() {
        return imageNodeFilter;
    }

    @Override
    public ResourceFilter getVideoNodeFilter() {
        return videoNodeFilter;
    }

    @Override
    public ResourceFilter getAnyAssetFilter() {
        return anyAssetFilter;
    }

    @Override
    public ResourceFilter getRequestNodeFilter(SlingHttpServletRequest request, String paramName, String defaultFilter) {
        String filter = RequestUtil.getParameter(request, paramName, defaultFilter);
        switch (filter) {
            case "asset":
                return getAssetNodeFilter();
            case "image":
                return getImageNodeFilter();
            case "video":
                return getVideoNodeFilter();
            default:
                return getAnyAssetFilter();
        }
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
    protected void activate(PagesAssetsConfiguration config) {
        this.config = config;
        ResourceFilter treeIntermediateFilter = new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.or,
                pagesConfiguration.getTreeIntermediateFilter(),
                pagesConfiguration.getSiteNodeFilter());
        assetNodeFilter = buildTreeFilter(new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.and,
                        REPLICATION_ROOT_FILTER,
                        ResourceFilterMapping.fromString(config.assetNodeFilterRule())),
                treeIntermediateFilter);
        imageNodeFilter = buildTreeFilter(new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.and,
                        REPLICATION_ROOT_FILTER,
                        ResourceFilterMapping.fromString(config.imageNodeFilterRule())),
                treeIntermediateFilter);
        videoNodeFilter = buildTreeFilter(new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.and,
                        REPLICATION_ROOT_FILTER,
                        ResourceFilterMapping.fromString(config.videoNodeFilterRule())),
                treeIntermediateFilter);
        anyAssetFilter = buildTreeFilter(new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.and,
                        REPLICATION_ROOT_FILTER,
                        new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.or,
                                ResourceFilterMapping.fromString(config.assetNodeFilterRule()),
                                ResourceFilterMapping.fromString(config.imageNodeFilterRule()),
                                ResourceFilterMapping.fromString(config.videoNodeFilterRule()))),
                treeIntermediateFilter);
    }

    @Deactivate
    protected void deactivate(ComponentContext context) {
        this.config = null;
    }
}
