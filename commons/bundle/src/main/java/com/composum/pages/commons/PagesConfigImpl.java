/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons;

import com.composum.pages.commons.PagesConstants.ReferenceType;
import com.composum.pages.commons.filter.SitePageFilter;
import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.service.SiteManager;
import com.composum.pages.commons.util.RequestUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.mapping.jcr.ResourceFilterMapping;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.composum.pages.commons.PagesConstants.META_ROOT_PATH;

/**
 * The configuration service for all servlets in the Pages module.
 */
@Component(
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Module Configuration"
        }
)
@Designate(ocd = PagesConfigImpl.Configuration.class)
public class PagesConfigImpl implements PagesConfiguration {

    public static final String PAGE_FILTER_ALL = "all";
    public static final String PAGE_FILTER_SITE = "site";

    @ObjectClassDefinition(
            name = "Composum Pages Module Configuration"
    )
    public @interface Configuration {

        @AttributeDefinition(
                description = "the default root name for sites not assigend to a tenant"
        )
        String defaultSitesRoot() default "/content/sites";

        @AttributeDefinition(
                description = "additional shared template root paths scanned in configured order"
        )
        String[] sharedTemplates() default {
                "/apps/shared",
                "/apps/composum"
        };

        @AttributeDefinition(
                description = "the filter configuration to set the scope to the internet sites"
        )
        String siteNodeFilterRule() default "PrimaryType(+'^cpp:(Site)$')";

        @AttributeDefinition(
                description = "the filter configuration to set the scope to the content pages"
        )
        String pageNodeFilterRule() default "PrimaryType(+'^cpp:(Page|Site)$')";

        @AttributeDefinition(
                description = "the filter configuration to set the scope to the content containers"
        )
        String containerNodeFilterRule() default "PrimaryType(+'^cpp:(Container|Page|Site)$')";

        @AttributeDefinition(
                description = "the filter configuration to set the scope to the content element"
        )
        String elementNodeFilterRule() default "PrimaryType(+'^cpp:(Element|Container|Page|Site)$')";

        @AttributeDefinition(
                description = "the filter configuration to set the scope to component development"
        )
        String develomentTreeFilterRule() default "Path(+'^/(etc|conf|apps|libs|sightly|htl|var)/+')";

        @AttributeDefinition(
                description = "the filter configuration to determine all intermediate nodes in the content structure"
        )
        String componentIntermediateFilterRule() default "PrimaryType(+'^cpp:(PageContent)$')";

        @AttributeDefinition(
                description = "the filter configuration to determine all intermediate nodes in the development scope"
        )
        String devIntermediateFilterRule() default "and{Folder(),Path(+'^/(etc|conf|apps|libs|sightly|htl|var)(/.+)?')}";

        @AttributeDefinition(
                description = "the filter configuration to determine all intermediate nodes in the tree view"
        )
        String treeIntermediateFilterRule() default "and{Folder(),Path(-'^/(etc|conf|apps|libs|sightly|htl|var)')}";

        @AttributeDefinition(
                description = "the filter configuration to detect ordered nodes (prevent from sorting in the tree)"
        )
        String orderableNodesFilterRule() default "or{Type(+[node:orderable]),PrimaryType(+'^.*([Oo]rdered|[Pp]age).*$')}";

        @AttributeDefinition(
                description = "the filter configuration to hide replication paths"
        )
        String replicationRootFilterRule() default "Path(-'^/(public|preview)')";

        @AttributeDefinition(
                description = "the filter configuration for page resources (reference type 'page')"
        )
        String pageFilterRule() default "PrimaryType(+'^cpp:Page$')";

        @AttributeDefinition(
                description = "the filter configuration for asset resources (reference type 'asset')"
        )
        String assetFilterRule() default "PrimaryType(+'^(cpp:Asset|nt:file)$')";
    }

    private ResourceFilter siteNodeFilter;
    private ResourceFilter pageNodeFilter;
    private ResourceFilter containerNodeFilter;
    private ResourceFilter elementNodeFilter;
    private ResourceFilter develomentTreeFilter;
    private ResourceFilter treeIntermediateFilter;
    private ResourceFilter orderableNodesFilter;
    private ResourceFilter replicationRootFilter;

    private Map<String, ResourceFilter> pageFilters;

    protected Configuration config;
    private transient BundleContext bundleContext;
    private transient SiteManager siteManager;

    @Nonnull
    @Override
    public Resource getPageMetaDataRoot(@Nonnull final ResourceResolver resolver) {
        return Objects.requireNonNull(resolver.getResource(META_ROOT_PATH));
    }

    @Nonnull
    @Override
    public ResourceFilter getRequestNodeFilter(@Nonnull SlingHttpServletRequest request,
                                               @Nonnull String paramName, @Nonnull String defaultFilter) {
        String filter = RequestUtil.getParameter(request, paramName, defaultFilter);
        switch (filter) {
            case "element":
                return getElementNodeFilter();
            case "container":
                return getContainerNodeFilter();
            case "page":
            default:
                return getPageNodeFilter();
        }
    }

    @Nonnull
    @Override
    public Configuration getConfig() {
        return config;
    }

    @Nonnull
    @Override
    public ResourceFilter getSiteNodeFilter() {
        return siteNodeFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getPageNodeFilter() {
        return pageNodeFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getContainerNodeFilter() {
        return containerNodeFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getElementNodeFilter() {
        return elementNodeFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getDevelopmentTreeFilter() {
        return develomentTreeFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getTreeIntermediateFilter() {
        return treeIntermediateFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getOrderableNodesFilter() {
        return orderableNodesFilter;
    }

    @Nonnull
    @Override
    public ResourceFilter getReplicationRootFilter() {
        return replicationRootFilter;
    }

    @Nullable
    @Override
    public ResourceFilter getPageFilter(@Nonnull BeanContext context, @Nonnull String key) {
        ResourceFilter filter = pageFilters.get(key);
        if (PAGE_FILTER_SITE.equals(key)) {
            Site site = getSiteManager().getContainingSite(context, context.getResource());
            if (site != null) {
                filter = new SitePageFilter(site.getPath(), filter);
            }
        }
        return filter;
    }

    protected ResourceFilter pageFilter;
    protected ResourceFilter assetFilter;

    @Nonnull
    @Override
    public ResourceFilter getReferenceFilter(@Nonnull ReferenceType type) {
        switch (type) {
            case asset:
                return assetFilter;
            case page:
            default:
                return pageFilter;
        }
    }

    protected SiteManager getSiteManager() {
        if (siteManager == null) {
            siteManager = (SiteManager) bundleContext.getService(
                    bundleContext.getServiceReference(SiteManager.class.getName()));
        }
        return siteManager;
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
        this.bundleContext = bundleContext;
        this.config = config;
        orderableNodesFilter = ResourceFilterMapping.fromString(config.orderableNodesFilterRule());
        replicationRootFilter = ResourceFilterMapping.fromString(config.replicationRootFilterRule());
        treeIntermediateFilter = new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.and,
                replicationRootFilter,
                ResourceFilterMapping.fromString(config.treeIntermediateFilterRule()));
        siteNodeFilter = buildTreeFilter(new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.and,
                        replicationRootFilter,
                        ResourceFilterMapping.fromString(config.siteNodeFilterRule())),
                treeIntermediateFilter);
        pageNodeFilter = buildTreeFilter(new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.and,
                        replicationRootFilter,
                        ResourceFilterMapping.fromString(config.pageNodeFilterRule())),
                treeIntermediateFilter);
        ResourceFilter componentIntermediateFilter = new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.or,
                ResourceFilterMapping.fromString(config.componentIntermediateFilterRule()),
                treeIntermediateFilter);
        containerNodeFilter = buildTreeFilter(new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.and,
                        replicationRootFilter,
                        ResourceFilterMapping.fromString(config.containerNodeFilterRule())),
                componentIntermediateFilter);
        elementNodeFilter = buildTreeFilter(new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.and,
                        replicationRootFilter,
                        ResourceFilterMapping.fromString(config.elementNodeFilterRule())),
                componentIntermediateFilter);
        ResourceFilter devIntermediateFilter = ResourceFilterMapping.fromString(config.devIntermediateFilterRule());
        develomentTreeFilter = buildTreeFilter(
                ResourceFilterMapping.fromString(config.develomentTreeFilterRule()), devIntermediateFilter);
        pageFilter = ResourceFilterMapping.fromString(config.pageFilterRule());
        assetFilter = ResourceFilterMapping.fromString(config.assetFilterRule());
        pageFilters = new HashMap<>();
        pageFilters.put(PAGE_FILTER_SITE, pageFilter);
        pageFilters.put(PAGE_FILTER_ALL, pageFilter);
    }

    @Deactivate
    protected void deactivate(ComponentContext context) {
        this.siteManager = null;
        this.config = null;
        this.bundleContext = null;
    }
}
