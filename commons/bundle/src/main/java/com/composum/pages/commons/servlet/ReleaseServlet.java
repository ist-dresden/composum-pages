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
import com.composum.sling.platform.staging.ReleaseNumberCreator;
import com.composum.sling.platform.staging.StagingReleaseManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
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
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;
import java.util.Objects;
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

    enum Extension {
        http
    }

    enum Operation {
        finalize,
        change,
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
        operations.setOperation(ServletOperationSet.Method.POST, Extension.http, Operation.finalize, new FinalizeRelease());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.http, Operation.change, new ChangeMetadata());
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
        public void doIt(@Nonnull final SlingHttpServletRequest request,
                         @Nonnull final  SlingHttpServletResponse response,
                         @Nonnull final ResourceHandle resource)
                throws IOException {
            try {
                final BeanContext beanContext = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
                final Validation validation = new Validation();

                String releaseNumber = validation.getRequiredParameter(request, "releaseName", null, "release number is required");

                if (validation.sendError(response)) return;

                StagingReleaseManager.Release release =
                        releaseManager.findRelease(resource, Objects.requireNonNull(releaseNumber));

                Site site = siteManager.getContainingSite(beanContext, resource);

                AccessMode accessMode = AccessMode.valueOf(getCategoryString().toUpperCase());
                releaseManager.setMark(accessMode.name().toLowerCase(), release);
                // replication is triggered by setMark via the ReleaseChangeEventListener .

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

    private class FinalizeRelease implements ServletOperation {

        @Override
        public void doIt(@Nonnull final SlingHttpServletRequest request,
                         @Nonnull final SlingHttpServletResponse response,
                         @Nonnull final ResourceHandle resourceHandle)
                throws IOException {
            try {
                final ResourceResolver resourceResolver = request.getResourceResolver();
                final Validation validation = new Validation();

                Resource resource = getReleaseResource(request, resourceHandle);
                if (resource == null) {
                    validation.addMessage(request, "site path is required", null);
                }

                final String numberPolicy = validation.getRequiredParameter(request,
                        "number", null, "no release number policy");

                if (validation.sendError(response)) return;

                final String title = request.getParameter(PARAM_TITLE);
                final String description = request.getParameter("description");

                ReleaseNumberCreator releaseType;
                try {
                    releaseType = ReleaseNumberCreator.valueOf(numberPolicy);
                } catch (IllegalArgumentException e) {
                    releaseType = ReleaseNumberCreator.MAJOR;
                }

                StagingReleaseManager.Release release =
                        releaseManager.finalizeCurrentRelease(Objects.requireNonNull(resource), releaseType);
                LOG.info("Release created {}", release);

                changeReleaseMetadata(request, release);

                resourceResolver.commit();

            } catch (Exception e) {
                LOG.error("error creating release: " + e.getMessage(), e);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            }
        }
    }

    private class ChangeMetadata implements ServletOperation {

        @Override
        public void doIt(@Nonnull final SlingHttpServletRequest request,
                         @Nonnull final SlingHttpServletResponse response,
                         @Nonnull final ResourceHandle resourceHandle)
                throws IOException {
            try {
                StagingReleaseManager.Release release = getRelease(request, response, resourceHandle);
                if (release != null) {
                    changeReleaseMetadata(request, release);
                    request.getResourceResolver().commit();
                }
            } catch (Exception e) {
                LOG.error("error creating release: " + e.getMessage(), e);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            }
        }
    }

    private class DeleteRelease implements ServletOperation {

        @Override
        public void doIt(@Nonnull final SlingHttpServletRequest request,
                         @Nonnull final SlingHttpServletResponse response,
                         @Nonnull final ResourceHandle resourceHandle)
                throws IOException {
            try {
                StagingReleaseManager.Release release = getRelease(request, response, resourceHandle);
                if (release != null) {
                    releaseManager.deleteRelease(release);
                    request.getResourceResolver().commit();
                }
            } catch (Exception e) {
                LOG.error("error deleting release: " + e.getMessage(), e);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            }
        }
    }

    protected void changeReleaseMetadata(@Nonnull final SlingHttpServletRequest request,
                                         @Nonnull final StagingReleaseManager.Release release)
            throws RepositoryException {

        final String title = request.getParameter(PARAM_TITLE);
        final String description = request.getParameter("description");

        ResourceHandle metaData = ResourceHandle.use(release.getMetaDataNode());
        if (StringUtils.isNotBlank(title)) {
            metaData.setProperty(ResourceUtil.PROP_TITLE, title);
        }
        if (StringUtils.isNotBlank(description)) {
            metaData.setProperty(ResourceUtil.PROP_DESCRIPTION, description);
        }
        metaData.setProperty(JcrConstants.JCR_LASTMODIFIED, Calendar.getInstance());
        metaData.setProperty(JcrConstants.JCR_LASTMODIFIED + "By", request.getResourceResolver().getUserID());

    }

    protected StagingReleaseManager.Release getRelease(@Nonnull final SlingHttpServletRequest request,
                                                       @Nonnull final SlingHttpServletResponse response,
                                                       @Nonnull final ResourceHandle resourceHandle)
            throws IOException {
        final ResourceResolver resourceResolver = request.getResourceResolver();
        final Validation validation = new Validation();

        Resource resource = getReleaseResource(request, resourceHandle);
        String releaseKey = getReleaseKey(request, resource, validation);

        if (validation.sendError(response)) return null;

        return releaseManager.findRelease(Objects.requireNonNull(resource), releaseKey);
    }

    protected String getReleaseKey(@Nonnull final SlingHttpServletRequest request,
                                   @Nullable final Resource resource, @Nonnull final Validation validation) {
        String releaseKey = request.getParameter("releaseName");
        if (resource != null) {
            final String path = resource.getPath();
            final Matcher pathMatcher = RELEASE_PATH_PATTERN.matcher(path);
            if (!pathMatcher.matches()) {
                validation.addMessage(request, "the path must be the path of a release node", path);
            }
            final String sitePath = pathMatcher.group(1);
            if (releaseKey == null) {
                releaseKey = pathMatcher.group(2);
            }
        } else {
            validation.addMessage(request, "release path is required", null);
        }
        return releaseKey;
    }

    protected Resource getReleaseResource(@Nonnull final SlingHttpServletRequest request, @Nonnull Resource resource) {
        final ResourceResolver resourceResolver = request.getResourceResolver();
        String path = request.getParameter(PARAM_PATH);
        if (path != null) {
            resource = resourceResolver.getResource(path);
            if (resource != null && JcrConstants.JCR_CONTENT.equals(resource.getName())) {
                resource = resource.getParent();
            }
        }
        return resource;
    }
}
