package com.composum.pages.stage.model.edit.site;

import com.composum.pages.commons.model.Element;
import com.composum.pages.commons.model.Model;
import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.PageVersion;
import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.service.PagesVersionsService;
import com.composum.pages.commons.service.VersionsService;
import com.composum.pages.stage.model.edit.FrameModel;
import com.composum.sling.platform.staging.versions.PlatformVersionsService;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SiteModel extends FrameModel {

    private static final Logger LOG = LoggerFactory.getLogger(SiteModel.class);

    public static final String PARAM_FILTER = "filter";

    public static final String SA_FILTER = "composum-pages-site-page-list-filter";

    private transient Site site;
    private transient String filterValue;
    private transient List<String> activationStates;
    private transient List<PageVersion> modifiedPages;
    private transient Collection<PageVersion> releaseChanges;

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
                }
            }
        }
        return site;
    }

    protected class InitialActivationFilter extends VersionsService.ActivationStateFilter {

        public InitialActivationFilter() {
            super(PlatformVersionsService.ActivationState.activated,
                    PlatformVersionsService.ActivationState.modified);
        }

        @Override
        public boolean accept(PageVersion version) {
            return super.accept(version) && version.getStatus().getPreviousVersionable() == null;
        }
    }

    /**
     * @return the list of pages changed (modified and activated) for the current release
     */
    public Collection<PageVersion> getReleaseChanges() {
        if (releaseChanges == null) {
            Site site = getSite();
            PagesVersionsService.ActivationStateFilter filter = null;
            PlatformVersionsService.ActivationState statusFilter = getStatusFilter();
            if (statusFilter != null) {
                switch (statusFilter) {
                    case initial:
                        filter = new InitialActivationFilter();
                        break;
                    case activated:
                        filter = new VersionsService.ActivationStateFilter(statusFilter,
                                PlatformVersionsService.ActivationState.modified);
                        break;
                    default:
                        filter = new VersionsService.ActivationStateFilter(statusFilter);
                        break;
                }
            }
            releaseChanges = site.getReleaseChanges(site.getCurrentRelease(), filter);
        }
        return releaseChanges;
    }

    /**
     * @return the list of pages changed after last activation
     */
    public List<PageVersion> getModifiedPages() {
        if (modifiedPages == null) {
            Site site = getSite();
            try {
                PlatformVersionsService.ActivationState statusFilter = getStatusFilter();
                if (statusFilter == PlatformVersionsService.ActivationState.activated) {
                    statusFilter = PlatformVersionsService.ActivationState.modified;
                }
                modifiedPages = site.getVersionsService().findModifiedPages(getContext(), site.getCurrentRelease(),
                        statusFilter != null ? new VersionsService.ActivationStateFilter(statusFilter) : null);
            } catch (RepositoryException e) {
                LOG.error("Retrieving modified pages for " + getResource().getPath(), e);
                modifiedPages = new ArrayList<>();
            }
        }
        return modifiedPages;
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
                session.setAttribute(sessionKey, "all".equals(value) ? "" : value);
            }
        } else {
            HttpSession session = request.getSession();
            if (session != null) {
                value = (String) session.getAttribute(sessionKey);
            }
        }
        return StringUtils.isNotBlank(value) ? value : "all";
    }
}
