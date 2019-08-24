package com.composum.pages.commons.service;

import com.composum.pages.commons.PagesConfiguration;
import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.filter.TemplateFilter;
import com.composum.pages.commons.model.Model;
import com.composum.pages.commons.model.Site;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.platform.staging.StagingReleaseManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.tenant.Tenant;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.RepositoryException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.composum.pages.commons.PagesConstants.NODE_TYPE_SITE;

@Component(
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Site Manager"
        }
)
public class PagesSiteManager extends PagesContentManager<Site> implements SiteManager {

    public static final String SITE_RESOURCE_TYPE = "composum/pages/stage/edit/site";

    public static final Map<String, Object> SITE_PROPERTIES;
    public static final Map<String, Object> SITE_CONTENT_PROPERTIES;
    public static final Map<String, Object> SITE_ASSETS_PROPERTIES;

    static {
        SITE_PROPERTIES = new HashMap<>();
        SITE_PROPERTIES.put(JcrConstants.JCR_PRIMARYTYPE, NODE_TYPE_SITE);
        SITE_CONTENT_PROPERTIES = new HashMap<>();
        SITE_CONTENT_PROPERTIES.put(JcrConstants.JCR_PRIMARYTYPE, PagesConstants.NODE_TYPE_SITE_CONFIGURATION);
        SITE_CONTENT_PROPERTIES.put(ResourceUtil.PROP_RESOURCE_TYPE, SITE_RESOURCE_TYPE);
        SITE_ASSETS_PROPERTIES = new HashMap<>();
        SITE_ASSETS_PROPERTIES.put(JcrConstants.JCR_PRIMARYTYPE, "sling:Folder");
    }

    @Reference
    protected PagesConfiguration pagesConfig;

    @Reference(cardinality = ReferenceCardinality.OPTIONAL)
    protected volatile PagesTenantSupport tenantSupport;

    @Reference
    protected ResourceManager resourceManager;

    @Reference
    protected PageManager pageManager;

    @Reference
    protected StagingReleaseManager releaseManager;

    @Override
    public Site createBean(BeanContext context, Resource resource) {
        return new Site(this, context, resource);
    }

    @Override
    public Site getContainingSite(Model element) {
        return getContainingSite(element.getContext(), element.getResource());
    }

    @Override
    public Site getContainingSite(BeanContext context, Resource resource) {
        if (resource != null) {
            Resource siteResource = getContainingSiteResource(resource);
            if (siteResource != null) {
                return new Site(this, context, siteResource);
            }
        }
        return null;
    }

    @Override
    public Resource getContainingSiteResource(Resource resource) {
        if (resource != null) {
            Resource checkResource = resource;
            while (checkResource != null) {
                if (Site.isSite(checkResource)) {
                    return checkResource;
                }
                checkResource = checkResource.getParent();
            }
        }
        if (resource != null) {
            try { // for resources from the release tree at com.composum.sling.platform.staging.StagingConstants.RELEASE_ROOT_PATH :
                Resource releaseRoot = releaseManager.findReleaseRoot(resource);
                if (Site.isSite(releaseRoot)) {
                    return releaseRoot;
                }
            } catch (RuntimeException e) {
                // give up
            }
        }
        return null;
    }

    /**
     * @return 'true' if tenants are supported on the platform
     */
    @Override
    public boolean isTenantSupport() {
        return tenantSupport != null;
    }

    /**
     * @return the tenant support implementation if available
     */
    @Override
    @Nullable
    public PagesTenantSupport getTenantSupport() {
        return tenantSupport;
    }

    /**
     * @return the list of id/tenant pairs of the joined tenants in the context of the current request
     */
    @Override
    @Nonnull
    public Map<String, Tenant> getTenants(@Nonnull BeanContext context) {
        return tenantSupport != null ? tenantSupport.getTenants(context) : Collections.emptyMap();
    }

    @Nonnull
    protected Resource getContentRoot(ResourceResolver resolver) {
        return Objects.requireNonNull(resolver.getResource("/content"));
    }

    @Override
    @Nonnull
    public Resource getSitesRoot(@Nonnull final BeanContext context, @Nullable final String tenantId) {
        ResourceResolver resolver = context.getResolver();
        String sitesRootPath = null;
        if (StringUtils.isNotBlank(tenantId) && tenantSupport != null) {
            sitesRootPath = tenantSupport.getContentRoot(context, tenantId);
        }
        if (StringUtils.isBlank(sitesRootPath)) {
            sitesRootPath = pagesConfig.getConfig().defaultSitesRoot();
        }
        Resource sitesRoot = resolver.getResource(sitesRootPath);
        if (LOG.isDebugEnabled()) {
            LOG.debug("getSitesRoot({},{}): {}", context.getResolver().getUserID(), tenantId,
                    sitesRoot != null ? sitesRoot.getPath() : "NULL");
        }
        if (sitesRoot == null) {
            sitesRoot = getContentRoot(resolver);
        }
        return sitesRoot;
    }

    @Override
    @Nonnull
    public Collection<Site> getSites(@Nonnull final BeanContext context) {
        Set<Site> sites = new HashSet<>();
        if (tenantSupport == null) {
            sites.addAll(getSites(context, getSitesRoot(context, null), ResourceFilter.ALL));
            if (sites.size() < 1) {
                sites.addAll(getSites(context, getContentRoot(context.getResolver()), ResourceFilter.ALL));
            }
        } else {
            for (String tenantId : tenantSupport.getTenants(context).keySet()) {
                sites.addAll(getSites(context, getSitesRoot(context, tenantId), ResourceFilter.ALL));
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("getSites({}): [{}]", context.getResolver().getUserID(), StringUtils.join(sites.toArray(), ", "));
        }
        return sites;
    }

    @Override
    public Collection<Site> getSiteTemplates(@Nonnull BeanContext context, String tenant) {
        UniqueSiteList result = new UniqueSiteList();
        ResourceResolver resolver = context.getResolver();
        for (String root : resolver.getSearchPath()) {
            Resource searchRoot = resolver.getResource(StringUtils.isNotBlank(tenant) ? root + tenant : root);
            Collection<Site> templates = getSites(context, searchRoot, TemplateFilter.INSTANCE);
            result.addAll(templates);
        }
        result.sort();
        return result;
    }

    protected Collection<Site> getSites(@Nonnull BeanContext context, @Nullable Resource searchRoot, @Nonnull ResourceFilter filter) {
        return getModels(context, NODE_TYPE_SITE, searchRoot, filter);
    }

    @Override
    public Site createSite(@Nonnull BeanContext context, String tenant, @Nonnull String siteName,
                           @Nullable String homepageType, boolean commit)
            throws RepositoryException, PersistenceException {
        return createSite(context, getSitesRoot(context, tenant), siteName, homepageType, commit);
    }

    @Override
    public Site createSite(@Nonnull BeanContext context, @Nonnull Resource siteBase, @Nonnull String siteName,
                           @Nullable String homepageType, boolean commit)
            throws RepositoryException, PersistenceException {

        Resource siteResource;

        ResourceResolver resolver = context.getResolver();
        if (LOG.isInfoEnabled()) {
            LOG.info("createSite({},{})", siteBase.getPath(), siteName);
        }
        checkExistence(resolver, siteBase, siteName);

        siteResource = resolver.create(siteBase, siteName, SITE_PROPERTIES);
        resolver.create(siteResource, JcrConstants.JCR_CONTENT, SITE_CONTENT_PROPERTIES);
        resolver.create(siteResource, "assets", SITE_ASSETS_PROPERTIES);

        if (StringUtils.isNotBlank(homepageType)) {
            pageManager.createPage(context, siteResource, homepageType, "home", null, null, commit);
        }

        if (commit) {
            resolver.commit();
        }
        return instanceCreated(context, siteResource);
    }

    @Override
    public Site createSite(@Nonnull BeanContext context, @Nullable String tenant, @Nonnull String siteName,
                           @Nullable String siteTitle, @Nullable String description,
                           @Nullable Resource siteTemplate, boolean commit)
            throws RepositoryException, PersistenceException {
        return createSite(context, getSitesRoot(context, tenant),
                siteName, siteTitle, description, siteTemplate, commit);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public Site createSite(@Nonnull BeanContext context, @Nonnull Resource siteBase, @Nonnull String siteName,
                           @Nullable String siteTitle, @Nullable String description,
                           @Nullable Resource siteTemplate, boolean commit)
            throws RepositoryException, PersistenceException {

        Resource siteResource;
        String siteParentPath = siteBase.getPath();
        final String sitePath = siteParentPath + "/" + siteName;

        final ResourceResolver resolver = context.getResolver();
        if (LOG.isInfoEnabled()) {
            LOG.info("createSite({},{})", sitePath, siteTemplate != null ? siteTemplate.getPath() : "<null>");
        }
        checkExistence(resolver, siteBase, siteName);

        if (siteTemplate != null) {
            siteResource = resourceManager.createFromTemplate(new ResourceManager.TemplateContext() {

                @Override
                public ResourceResolver getResolver() {
                    return resolver;
                }

                @Override
                public String applyTemplatePlaceholders(@Nonnull final Resource target, @Nonnull final String value) {
                    String result = value.replaceAll("\\$\\{path}", target.getPath());
                    Resource pageResource = pageManager.getContainingPageResource(target);
                    if (pageResource != null) {
                        result = result.replaceAll("\\$\\{page}", pageResource.getPath());
                    }
                    result = result.replaceAll("\\$\\{site}", sitePath);
                    if (!value.equals(result)) {
                        result = result.replaceAll("/[^/]+/\\.\\./", "/");
                    }
                    return result;
                }

            }, siteBase, siteName, siteTemplate, true);
        } else {
            Site site = createSite(context, siteBase, siteName, null, commit);
            siteResource = site.getResource();
        }

        Resource content = Objects.requireNonNull(siteResource.getChild(JcrConstants.JCR_CONTENT));
        ModifiableValueMap values = Objects.requireNonNull(content.adaptTo(ModifiableValueMap.class));
        if (StringUtils.isNotBlank(siteTitle)) {
            values.put(ResourceUtil.PROP_TITLE, siteTitle);
        }
        if (StringUtils.isNotBlank(description)) {
            values.put(ResourceUtil.PROP_DESCRIPTION, description);
        }

        if (commit) {
            resolver.commit();
        }
        return instanceCreated(context, siteResource);
    }

    @Override
    public boolean deleteSite(@Nonnull BeanContext context, @Nonnull String sitePath, boolean commit)
            throws PersistenceException {
        return deleteSite(context, context.getResolver().getResource(sitePath), commit);
    }

    @Override
    public boolean deleteSite(@Nonnull BeanContext context, @Nullable Resource siteResource, boolean commit)
            throws PersistenceException {

        if (siteResource != null) {
            ResourceResolver resolver = context.getResolver();
            if (LOG.isInfoEnabled()) {
                LOG.info("deleteSite({})", siteResource.getPath());
            }

            resolver.delete(siteResource);

            if (commit) {
                resolver.commit();
            }
            return true;
        }
        return false;
    }
}
