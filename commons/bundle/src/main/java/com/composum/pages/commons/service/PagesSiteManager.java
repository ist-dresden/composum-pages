package com.composum.pages.commons.service;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.Model;
import com.composum.pages.commons.model.Site;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.platform.staging.query.Query;
import com.composum.sling.platform.staging.query.QueryBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.composum.pages.commons.PagesConstants.NODE_TYPE_SITE;
import static org.apache.jackrabbit.JcrConstants.JCR_NAME;

@Component(immediate = true)
@Service
public class PagesSiteManager extends ResourceManager<Site> implements SiteManager {

    public static final String SITE_RESOURCE_TYPE = "composum/pages/stage/edit/site";

    public static final Map<String, Object> SITE_ROOT_PROPERTIES;
    public static final Map<String, Object> SITE_PROPERTIES;
    public static final Map<String, Object> SITE_CONTENT_PROPERTIES;
    public static final Map<String, Object> SITE_ASSETS_PROPERTIES;

    static {
        SITE_ROOT_PROPERTIES = new HashMap<>();
        SITE_ROOT_PROPERTIES.put(JcrConstants.JCR_PRIMARYTYPE, "sling:Folder");
        SITE_PROPERTIES = new HashMap<>();
        SITE_PROPERTIES.put(JcrConstants.JCR_PRIMARYTYPE, NODE_TYPE_SITE);
        SITE_CONTENT_PROPERTIES = new HashMap<>();
        SITE_CONTENT_PROPERTIES.put(JcrConstants.JCR_PRIMARYTYPE, PagesConstants.NODE_TYPE_SITE_CONFIGURATION);
        SITE_CONTENT_PROPERTIES.put(ResourceUtil.PROP_RESOURCE_TYPE, SITE_RESOURCE_TYPE);
        SITE_ASSETS_PROPERTIES = new HashMap<>();
        SITE_ASSETS_PROPERTIES.put(JcrConstants.JCR_PRIMARYTYPE, "sling:Folder");
    }

    @Reference
    protected PageManager pageManager;

    @Override
    public Site createBean(BeanContext context, Resource resource) {
        return new Site(this, context, resource);
    }

    public Site getContainingSite(Model element) {
        return getContainingSite(element.getContext(), element.getResource());
    }

    public Site getContainingSite(BeanContext context, Resource resource) {
        if (resource != null) {
            Resource siteResource = getContainingSiteResource(resource);
            if (siteResource != null) {
                return new Site(this, context, siteResource);
            }
        }
        return null;
    }

    public Resource getContainingSiteResource(Resource resource) {
        if (resource != null) {
            if (Site.isSite(resource)) {
                return resource;
            } else {
                return getContainingSiteResource(resource.getParent());
            }
        }
        return null;
    }

    public Resource getSiteBase(BeanContext context, String tenant)
            throws PersistenceException {
        ResourceResolver resolver = context.getResolver();
        Resource tenantsContent = resolver.getResource("/content");
        Resource siteBase = StringUtils.isNotBlank(tenant)
                ? resolver.getResource(tenantsContent, tenant)
                : tenantsContent;
        if (siteBase == null) {
            siteBase = resolver.create(tenantsContent, tenant, SITE_ROOT_PROPERTIES);
        }
        return siteBase;
    }

    public List<Site> getSites(BeanContext context, Resource siteBase) {
        List<Site> result = new ArrayList<>();
        try {
            ResourceResolver resolver = context.getResolver();
            String queryRoot = siteBase != null ? siteBase.getPath() : "/";
            Query query = resolver.adaptTo(QueryBuilder.class).createQuery();
            query.path(queryRoot).type(NODE_TYPE_SITE).orderBy(JCR_NAME);
            Iterable<Resource> found = query.execute();
            for (Resource siteRes : found) {
                result.add(createBean(context, siteRes));
            }
        } catch (RepositoryException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return result;
    }

    public List<Site> getSites(BeanContext context, String tenant) {
        try {
            Resource siteBase = getSiteBase(context, tenant);
            return getSites(context, siteBase);
        } catch (PersistenceException ex) {
            LOG.error(ex.getMessage(), ex);
            return Collections.emptyList();
        }
    }

    public List<Site> getSiteTemplates(BeanContext context, String tenant) {
        UniqueSiteList result = new UniqueSiteList();
        ResourceResolver resolver = context.getResolver();
        for (String root : resolver.getSearchPath()) {
            Resource siteBase = resolver.getResource(StringUtils.isNotBlank(tenant) ? root + tenant : root);
            List<Site> templates = getSites(context, siteBase);
            result.addAll(templates);
        }
        result.sort();
        return result;
    }

    public Site createSite(BeanContext context, String tenant, String siteName, String homepageType)
            throws RepositoryException, PersistenceException {
        return createSite(context, getSiteBase(context, tenant), siteName, homepageType);
    }

    public Site createSite(BeanContext context, Resource siteBase, String siteName, String homepageType)
            throws RepositoryException, PersistenceException {

        Resource siteResource;
        try (ResourceResolver resolver = context.getResolver()) {
            checkExistence(resolver, siteBase, siteName);

            siteResource = resolver.create(siteBase, siteName, SITE_PROPERTIES);
            resolver.create(siteResource, JcrConstants.JCR_CONTENT, SITE_CONTENT_PROPERTIES);
            resolver.create(siteResource, "assets", SITE_ASSETS_PROPERTIES);

            if (StringUtils.isNotBlank(homepageType)) {
                pageManager.createPage(context, siteResource, "home", homepageType);
            }
            resolver.commit();
        }

        return instanceCreated(context, siteResource);
    }

    public Site createSite(BeanContext context, Resource siteBase, String siteName, Resource siteTemplate)
            throws RepositoryException, PersistenceException {

        Resource siteResource;
        try (ResourceResolver resolver = context.getResolver()) {
            checkExistence(resolver, siteBase, siteName);

            siteResource = resolver.copy(siteTemplate.getPath(), siteBase.getPath() + "/" + siteName);
            resolver.commit();
        }

        return instanceCreated(context, siteResource);
    }
}
