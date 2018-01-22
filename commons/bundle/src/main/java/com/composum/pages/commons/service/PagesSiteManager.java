package com.composum.pages.commons.service;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.Model;
import com.composum.pages.commons.model.Site;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.platform.staging.query.Query;
import com.composum.sling.platform.staging.query.QueryBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.composum.pages.commons.PagesConstants.NODE_TYPE_SITE;
import static org.apache.jackrabbit.JcrConstants.JCR_NAME;

@org.osgi.service.component.annotations.Component(
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Site Manager"
        }
)
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
            if (Site.isSite(resource)) {
                return resource;
            } else {
                return getContainingSiteResource(resource.getParent());
            }
        }
        return null;
    }

    @Override
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

    @Override
    public Collection<Site> getSites(BeanContext context, String tenant) {
        try {
            return getSites(context, getSiteBase(context, tenant));
        } catch (PersistenceException ex) {
            LOG.error(ex.getMessage(), ex);
            return Collections.emptyList();
        }
    }

    @Override
    public Collection<Site> getSiteTemplates(BeanContext context, String tenant) {
        UniqueSiteList result = new UniqueSiteList();
        ResourceResolver resolver = context.getResolver();
        for (String root : resolver.getSearchPath()) {
            Resource searchRoot = resolver.getResource(StringUtils.isNotBlank(tenant) ? root + tenant : root);
            Collection<Site> templates = getSites(context, searchRoot);
            result.addAll(templates);
        }
        result.sort();
        return result;
    }

    @Override
    public Collection<Site> getSites(BeanContext context, Resource searchRoot) {
        Collection<Site> result = new ArrayList<>();
        try {
            ResourceResolver resolver = context.getResolver();
            String queryRoot = searchRoot != null ? searchRoot.getPath() : "/";
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

    @Override
    public Site createSite(BeanContext context, String tenant, String siteName,
                           String homepageType, boolean commit)
            throws RepositoryException, PersistenceException {
        return createSite(context, getSiteBase(context, tenant), siteName, homepageType, commit);
    }

    @Override
    public Site createSite(BeanContext context, Resource siteBase, String siteName,
                           String homepageType, boolean commit)
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
            pageManager.createPage(context, siteResource, "home", homepageType, commit);
        }

        if (commit) {
            resolver.commit();
        }
        return instanceCreated(context, siteResource);
    }

    @Override
    public Site createSite(BeanContext context, String tenant,
                           String siteName, String siteTitle, String description,
                           Resource siteTemplate, boolean commit)
            throws RepositoryException, PersistenceException {
        return createSite(context, getSiteBase(context, tenant),
                siteName, siteTitle, description, siteTemplate, commit);
    }

    @Override
    public Site createSite(BeanContext context, Resource siteBase,
                           String siteName, String siteTitle, String description,
                           Resource siteTemplate, boolean commit)
            throws RepositoryException, PersistenceException {

        Resource siteResource;
        String siteParentPath = siteBase.getPath();
        String sitePath = siteParentPath + "/" + siteName;

        ResourceResolver resolver = context.getResolver();
        if (LOG.isInfoEnabled()) {
            LOG.info("createSite({},{})", sitePath, siteTemplate != null ? siteTemplate.getPath() : "<null>");
        }
        checkExistence(resolver, siteBase, siteName);

        if (siteTemplate != null) {
            Session session = resolver.adaptTo(Session.class);
            Workspace workspace = session.getWorkspace();
            workspace.copy(siteTemplate.getPath(), sitePath);
            resolver.refresh();
            siteResource = resolver.getResource(sitePath);
        } else {
            Site site = createSite(context, siteBase, siteName, null, commit);
            siteResource = site.getResource();
        }

        ModifiableValueMap values = siteResource.adaptTo(ModifiableValueMap.class);
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
    public Site deleteSite(BeanContext context, String sitePath, boolean commit)
            throws PersistenceException {
        return deleteSite(context, context.getResolver().getResource(sitePath), commit);
    }

    @Override
    public Site deleteSite(BeanContext context, Resource siteResource, boolean commit)
            throws PersistenceException {

        ResourceResolver resolver = context.getResolver();
        if (LOG.isInfoEnabled()) {
            LOG.info("deleteSite({})", siteResource.getPath());
        }

        resolver.delete(siteResource);

        if (commit) {
            resolver.commit();
        }
        return instanceCreated(context, siteResource);
    }
}
