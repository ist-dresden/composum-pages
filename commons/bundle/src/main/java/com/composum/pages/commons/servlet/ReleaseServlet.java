package com.composum.pages.commons.servlet;

import com.composum.pages.commons.filter.SitePageFilter;
import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.replication.ReplicationContext;
import com.composum.pages.commons.replication.ReplicationManager;
import com.composum.pages.commons.service.SiteManager;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.servlet.AbstractServiceServlet;
import com.composum.sling.core.servlet.ServletOperation;
import com.composum.sling.core.servlet.ServletOperationSet;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.platform.security.AccessMode;
import com.composum.sling.platform.staging.ReleasedVersionable;
import com.composum.sling.platform.staging.impl.SiblingOrderUpdateStrategy;
import com.composum.sling.platform.staging.ReleaseNumberCreator;
import com.composum.sling.platform.staging.StagingReleaseManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.composum.pages.commons.servlet.ReleaseServlet.Extension.http;

/**
 * @author Mirko Zeibig
 * @since 16.09.16.
 */
@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Release Servlet",
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/cpm/pages/release",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_POST,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_PUT,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_DELETE
        })
public class ReleaseServlet extends AbstractServiceServlet {

    private static final Logger LOG = LoggerFactory.getLogger(ReleaseServlet.class);

    public static final Pattern RELEASE_PATH_PATTERN = Pattern.compile("^(/.*)/jcr:content/releases/(.+)$");

    protected BundleContext bundleContext;

    @Reference
    private SiteManager siteManager;

    @Reference
    private StagingReleaseManager releaseManager;

    @Reference
    private ReplicationManager replicationManager;

    enum Extension {
        http
    }

    enum Operation {
        release,
        delete,
        setpublic,
        setpreview
    }

    private ReleaseOperationSet operations = new ReleaseOperationSet();

    private class ReleaseOperationSet extends ServletOperationSet<Extension, Operation> {
        ReleaseOperationSet() {
            super(http);
        }
    }

    @Override
    public void init() throws ServletException {
        super.init();

        // POST
        operations.setOperation(ServletOperationSet.Method.POST, Extension.http, Operation.release, new ReleaseOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.http, Operation.delete, new DeleteRelease());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.http, Operation.setpublic, new SetPublicRelease());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.http, Operation.setpreview, new SetPreviewRelease());
    }

    @Activate
    private void activate(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    protected boolean isEnabled() {
        return true;
    }

    @Override
    protected ServletOperationSet getOperations() {
        return operations;
    }


    abstract private class SetReleaseCategory implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response, ResourceHandle resource)
                throws IOException {
            try {
                BeanContext beanContext = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);

                String releaseNumber = getStringParameter(request, response, "releaseName", "release name is required");

                final String accessCategory = getCategoryString();

                StagingReleaseManager.Release release = releaseManager.findRelease(resource, releaseNumber);

                Site site = siteManager.getContainingSite(beanContext, resource);

                AccessMode accessMode = AccessMode.valueOf(accessCategory.toUpperCase());
                releaseManager.setMark(accessMode.name().toLowerCase(), release);

                LOG.info("replication of '{}' for {}...", site.getPath(), accessMode);
                String releaseLabel = site.getReleaseNumber(accessMode.name());
                LOG.debug("'{}': using staging resolver of release '{}'...", resource.getPath(), releaseLabel);

                ResourceResolver stagedResolver = releaseManager.getResolverForRelease(release, replicationManager, false);
                Resource stagedSiteResource = stagedResolver.getResource(site.getResource().getPath());

                ResourceFilter releaseFilter = new SitePageFilter(site.getPath(), ResourceFilter.ALL);
                ReplicationContext replicationContext = new ReplicationContext(beanContext, site, accessMode, releaseFilter, stagedResolver);
                replicationManager.replicateResource(replicationContext, stagedSiteResource, true);
                replicationManager.replicateReferences(replicationContext);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("replication of '{}' for {} done.", resource.getPath(), accessMode);
                }

                request.getResourceResolver().commit();

            } catch (Exception e) {
                LOG.error("error setting release category: " + e.getMessage(), e);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            }
        }

        @Nonnull
        protected abstract String getCategoryString();

    }

    private class SetPublicRelease extends SetReleaseCategory {
        @Override
        @Nonnull
        protected String getCategoryString() {
            return AccessMode.ACCESS_MODE_PUBLIC.toLowerCase();
        }
    }

    private class SetPreviewRelease extends SetReleaseCategory {
        @Override
        @Nonnull
        protected String getCategoryString() {
            return AccessMode.ACCESS_MODE_PREVIEW.toLowerCase();
        }
    }

    private class DeleteRelease implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response, ResourceHandle resource)
                throws IOException {
            try {
                final String path = getStringParameter(request, response, "path", "site path is required");
                if (path == null) return;
                final Matcher pathMatcher = RELEASE_PATH_PATTERN.matcher(path);
                if (!pathMatcher.matches()) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "the path must be the path of a release node");
                    return;
                }
                final String sitePath = pathMatcher.group(1);

                String releaseName = request.getParameter("releaseName");
                if (releaseName == null) {
                    releaseName = pathMatcher.group(2);
                }

                StagingReleaseManager.Release release = releaseManager.findRelease(resource, releaseName);
                releaseManager.removeRelease(release);

                final ResourceResolver resourceResolver = request.getResourceResolver();
                resourceResolver.delete(resource);
                resourceResolver.commit();
            } catch (Exception e) {
                LOG.error("error deleting release: " + e.getMessage(), e);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            }
        }
    }

    private class ReleaseOperation implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response, ResourceHandle resourceHandle)
                throws IOException {
            try {

                final String path = getStringParameter(request, response, "path", "site path is required");
                if (path == null) return;
                final String sitePath = path.endsWith("/jcr:content") ? path.substring(0, path.lastIndexOf('/')) : path;

                final String numberPolicy = getStringParameter(request, response, "number", "release number policy");
                if (numberPolicy == null) return;
                final String title = request.getParameter("title");
                final String description = request.getParameter("description");
                final RequestParameter objectsParameter = request.getRequestParameter("objects");
                final String objectsString;
                if (objectsParameter != null) {
                    objectsString = objectsParameter.getString();
                } else {
                    objectsString = "";
                }

                final ResourceResolver resourceResolver = request.getResourceResolver();

                List<ReleasedVersionable> versionables = new ArrayList<>();
                for (String versionablePath : objectsString.split(",")) {
                    Resource versionable = resourceResolver.getResource(versionablePath).getChild(ResourceUtil.CONTENT_NODE);
                    versionables.add(ReleasedVersionable.forBaseVersion(versionable));
                }

                ReleaseNumberCreator releaseType;
                try {
                    releaseType = ReleaseNumberCreator.valueOf(numberPolicy);
                } catch (IllegalArgumentException e) {
                    releaseType = ReleaseNumberCreator.MAJOR;
                }

                // FIXME hps 2019-04-10 introduce actual parameter for release number type and base release
                StagingReleaseManager.Release release = releaseManager.createRelease(resourceResolver.getResource(path), releaseType);
                LOG.info("Release created {}", release);
                Map<String, SiblingOrderUpdateStrategy.Result> result = releaseManager.updateRelease(release, versionables);
                LOG.info("Release update result: {}", result);

                ResourceHandle metaData = ResourceHandle.use(release.getMetaDataNode());
                if (StringUtils.isNotBlank(title)) {
                    metaData.setProperty(ResourceUtil.PROP_TITLE, title);
                }
                if (StringUtils.isNotBlank(description)) {
                    metaData.setProperty(ResourceUtil.PROP_DESCRIPTION, description);
                }
                metaData.setProperty(ResourceUtil.PROP_LAST_MODIFIED, Calendar.getInstance());
                metaData.setProperty("jcr:lastModifiedBy", resourceResolver.getUserID());
                resourceResolver.commit();
            } catch (Exception e) {
                LOG.error("error creating release: " + e.getMessage(), e);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            }
        }


    }

    @Nullable
    private String getStringParameter(SlingHttpServletRequest request, SlingHttpServletResponse response, String paramName, String errorMesage) throws IOException {
        final RequestParameter requestParameter = request.getRequestParameter(paramName);
        if (requestParameter == null || StringUtils.isEmpty(requestParameter.getString())) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, errorMesage);
            return null;
        }
        return requestParameter.getString();
    }
}
