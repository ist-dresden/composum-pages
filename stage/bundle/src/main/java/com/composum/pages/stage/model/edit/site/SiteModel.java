package com.composum.pages.stage.model.edit.site;

import com.composum.pages.commons.AssetsConfiguration;
import com.composum.pages.commons.PagesConfiguration;
import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.ContentVersion;
import com.composum.pages.commons.model.Element;
import com.composum.pages.commons.model.Model;
import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.service.PagesVersionsService;
import com.composum.pages.commons.service.VersionsService;
import com.composum.pages.stage.model.edit.FrameModel;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.platform.staging.versions.PlatformVersionsService;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.composum.pages.commons.AssetsConfigImpl.ASSET_FILTER_ASSET;
import static com.composum.pages.commons.AssetsConfigImpl.ASSET_FILTER_DOCUMENT;
import static com.composum.pages.commons.AssetsConfigImpl.ASSET_FILTER_IMAGE;
import static com.composum.pages.commons.AssetsConfigImpl.ASSET_FILTER_VIDEO;

public class SiteModel extends FrameModel {

    private static final Logger LOG = LoggerFactory.getLogger(SiteModel.class);

    public static final String PARAM_TYPE = "type";
    public static final String PARAM_FILTER = "filter";

    public static final String SA_TYPE = "composum-pages-site-page-list-type";
    public static final String SA_FILTER = "composum-pages-site-page-list-filter";

    private transient Site site;
    private transient String contentTypeValue;
    private transient String filterValue;
    private transient List<String> activationStates;
    private transient List<ContentVersion> modifiedContent;
    private transient Collection<ContentVersion> releaseChanges;

    public Site getSite() {
        if (site == null) {
            Resource resource = getFrameResource(); // try to use the 'frame'
            if (Site.isSite(resource) || Site.isSiteConfiguration(resource)) {
                site = getSiteManager().createBean(getContext(), resource);
            } else { // try to use the delegate (reference from suffix)
                Model delegate = getDelegate();
                if (delegate instanceof Site) {
                    site = (Site) delegate;
                } else if (delegate instanceof Page) {
                    site = ((Page) delegate).getSite();
                } else if (delegate instanceof Element) {
                    Page page = delegate.getContainingPage();
                    if (page != null) {
                        site = page.getSite();
                    } else {
                        site = getSiteManager().getContainingSite(delegate);
                    }
                } else {
                    site = getSiteManager().getContainingSite(delegate);
                }
            }
        }
        return site;
    }

    /**
     * @return the list of pages changed (modified and activated) for the current release
     */
    public Collection<ContentVersion> getReleaseChanges() {
        if (releaseChanges == null) {
            Site site = getSite();
            VersionsService.ContentVersionFilter filter = null;
            ResourceFilter contentTypeFilter = getContentTypeFilter();
            if (contentTypeFilter != null) {
                filter = new VersionsService.ContentVersionByResourceFilter(contentTypeFilter);
            }
            PagesVersionsService.ActivationStateFilter stateFilter = null;
            PlatformVersionsService.ActivationState statusFilter = getStatusFilter();
            if (statusFilter != null) {
                switch (statusFilter) {
                    case initial:
                        stateFilter = new InitialActivationFilter();
                        break;
                    case activated:
                        stateFilter = new VersionsService.ActivationStateFilter(statusFilter,
                                PlatformVersionsService.ActivationState.modified);
                        break;
                    default:
                        stateFilter = new VersionsService.ActivationStateFilter(statusFilter);
                        break;
                }
            }
            if (stateFilter != null) {
                filter = filter != null ? filter.and(stateFilter) : stateFilter;
            }
            releaseChanges = site.getReleaseChanges(site.getCurrentRelease(), filter);
        }
        return releaseChanges;
    }

    /**
     * @return the list of pages changed after last activation
     */
    public List<ContentVersion> getModifiedContent() {
        if (modifiedContent == null) {
            Site site = getSite();
            try {
                VersionsService.ContentVersionFilter filter = null;
                ResourceFilter contentTypeFilter = getContentTypeFilter();
                if (contentTypeFilter != null) {
                    filter = new VersionsService.ContentVersionByResourceFilter(contentTypeFilter);
                }
                PlatformVersionsService.ActivationState statusFilter = getStatusFilter();
                if (statusFilter == PlatformVersionsService.ActivationState.activated) {
                    statusFilter = PlatformVersionsService.ActivationState.modified;
                }
                if (statusFilter != null) {
                    VersionsService.ContentVersionFilter stateFilter = new VersionsService.ActivationStateFilter(statusFilter);
                    filter = filter != null ? filter.and(stateFilter) : stateFilter;
                }
                modifiedContent = site.getVersionsService().findModifiedContent(getContext(), site.getCurrentRelease(), filter);
            } catch (RepositoryException e) {
                LOG.error("Retrieving modified content for " + getResource().getPath(), e);
                modifiedContent = new ArrayList<>();
            }
        }
        return modifiedContent;
    }

    public Map<String, String> getContentTypes() {
        Map<String, String> result = new LinkedHashMap<>();
        result.put(PagesConstants.ReferenceType.page.name(), "Pages");
        if (isAssetsSupport()) {
            result.put(ASSET_FILTER_ASSET, "Assets");
        }
        result.put(ASSET_FILTER_IMAGE, "Images");
        result.put(ASSET_FILTER_VIDEO, "Videos");
        result.put(ASSET_FILTER_DOCUMENT, "Documents");
        return result;
    }

    public String getContentTypeValue() {
        if (contentTypeValue == null) {
            contentTypeValue = getSessionValue(SA_TYPE, PARAM_TYPE);
        }
        return contentTypeValue;
    }

    public static class ContentFilterWrapper implements ResourceFilter {

        protected final ResourceFilter filter;

        public ContentFilterWrapper(ResourceFilter filter) {
            this.filter = filter;
        }

        @Override
        public boolean accept(@Nullable Resource resource) {
            if (resource != null) {
                if (filter.accept(resource)) {
                    return true;
                }
                if (JcrConstants.JCR_CONTENT.equals(resource.getName())) {
                    return filter.accept(resource.getParent());
                }
            }
            return false;
        }

        @Override
        public boolean isRestriction() {
            return filter.isRestriction();
        }

        @Override
        public void toString(@Nonnull StringBuilder builder) {
            filter.toString(builder);
        }
    }

    public ResourceFilter getContentTypeFilter() {
        String contentType = getContentTypeValue();
        if (StringUtils.isNotBlank(contentType) && !"all".equals(contentType)) {
            BeanContext context = getContext();
            ResourceFilter filter;
            if (PagesConstants.ReferenceType.page.name().equals(contentType)) {
                PagesConfiguration config = context.getService(PagesConfiguration.class);
                filter = new ResourceFilter.FilterSet(ResourceFilter.FilterSet.Rule.or,
                        config.getReferenceFilter(PagesConstants.ReferenceType.page), config.getSiteFilter());
            } else {
                filter = context.getService(AssetsConfiguration.class).getFileFilter(context, contentType);
            }
            if (filter != null) {
                return new ContentFilterWrapper(filter);
            }
        }
        return null;
    }

    protected class InitialActivationFilter extends VersionsService.ActivationStateFilter {

        public InitialActivationFilter() {
            super(PlatformVersionsService.ActivationState.activated,
                    PlatformVersionsService.ActivationState.modified);
        }

        @Override
        public boolean accept(ContentVersion version) {
            return super.accept(version) && version.getStatus().getPreviousVersionable() == null;
        }
    }

    public List<String> getActivationStates() {
        if (activationStates == null) {
            activationStates = new ArrayList<>();
            for (PlatformVersionsService.ActivationState value : PlatformVersionsService.ActivationState.values()) {
                activationStates.add(value.name());
            }
        }
        return activationStates;
    }

    public PlatformVersionsService.ActivationState getStatusFilter() {
        try {
            return PlatformVersionsService.ActivationState.valueOf(getFilterValue());
        } catch (Exception ignore) {
        }
        return null;
    }

    public String getFilterValue() {
        if (filterValue == null) {
            filterValue = getSessionValue(SA_FILTER, PARAM_FILTER);
        }
        return filterValue;
    }

    public String getSessionValue(String sessionKey, String paramName) {
        SlingHttpServletRequest request = getContext().getRequest();
        String value = request.getParameter(paramName);
        if (value != null) {
            HttpSession session = request.getSession(true);
            if (session != null) {
                session.setAttribute(sessionKey, value);
            }
        } else {
            HttpSession session = request.getSession();
            if (session != null) {
                value = (String) session.getAttribute(sessionKey);
            }
        }
        return value;
    }
}
