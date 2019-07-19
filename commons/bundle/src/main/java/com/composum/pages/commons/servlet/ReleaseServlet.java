package com.composum.pages.commons.servlet;

import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.service.SiteManager;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.servlet.AbstractServiceServlet;
import com.composum.sling.core.servlet.ServletOperation;
import com.composum.sling.core.servlet.ServletOperationSet;
import com.composum.sling.core.servlet.Status;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.platform.security.AccessMode;
import com.composum.sling.platform.staging.ReleaseNumberCreator;
import com.composum.sling.platform.staging.StagingReleaseManager;
import com.composum.sling.platform.staging.impl.StagingUtils;
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
import java.io.IOException;
import java.util.Calendar;
import java.util.Objects;
import java.util.regex.Matcher;

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

    public static final String PARAM_RELEASE_KEY = "releaseKey";

    public static final String PARAM_NUMBER = "number";
    public static final String PARAM_DESCRIPTION = "description";

    protected BundleContext bundleContext;

    @Reference
    private SiteManager siteManager;

    @Reference
    private StagingReleaseManager releaseManager;

    enum Extension {
        json
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
            super(Extension.json);
        }
    }

    @Override
    public void init() throws ServletException {
        super.init();

        // POST
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json, Operation.finalize, new FinalizeRelease());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json, Operation.change, new ChangeMetadata());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json, Operation.delete, new DeleteRelease());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json, Operation.setpublic, new SetPublicRelease());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json, Operation.setpreview, new SetPreviewRelease());
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
                         @Nonnull final SlingHttpServletResponse response,
                         @Nonnull final ResourceHandle resource)
                throws IOException {
            Status status = new Status(request, response);
            try {
                StagingReleaseManager.Release release = getRelease(request, response, resource, status);
                if (release != null) {
                    AccessMode accessMode = AccessMode.valueOf(getCategoryString().toUpperCase());
                    releaseManager.setMark(accessMode.name().toLowerCase(), release);
                    LOG.info("Release marked {}: {}", release, accessMode.name());
                    // replication is triggered by setMark via the ReleaseChangeEventListener .
                    request.getResourceResolver().commit();
                }
            } catch (Exception ex) {
                status.withLogging(LOG).error("error setting release category: {}", ex);
            }
            status.sendJson();
        }

        @Nonnull
        protected abstract String getCategoryString();

    }

    private class SetPublicRelease extends SetReleaseCategory {
        @Override
        @Nonnull
        protected String getCategoryString() {
            return AccessMode.ACCESS_MODE_PUBLIC;
        }
    }

    private class SetPreviewRelease extends SetReleaseCategory {
        @Override
        @Nonnull
        protected String getCategoryString() {
            return AccessMode.ACCESS_MODE_PREVIEW;
        }
    }

    private class FinalizeRelease implements ServletOperation {

        @Override
        public void doIt(@Nonnull final SlingHttpServletRequest request,
                         @Nonnull final SlingHttpServletResponse response,
                         @Nonnull final ResourceHandle resource)
                throws IOException {
            Status status = new Status(request, response);
            try {

                Site site = getReleaseSite(request, response, resource, status);
                if (site != null) {

                    final String numberPolicy = status.getRequiredParameter(PARAM_NUMBER, null, "no release number policy");

                    if (status.isValid()) {

                        final String title = request.getParameter(PARAM_TITLE);
                        final String description = request.getParameter(PARAM_DESCRIPTION);

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

                        request.getResourceResolver().commit();
                    }
                } else {
                    status.withLogging(LOG).error("no site found ({})", resource.getPath());
                }
            } catch (Exception ex) {
                status.withLogging(LOG).error("error creating release: {}", ex);
            }
            status.sendJson();
        }
    }

    protected class ChangeMetadata implements ServletOperation {

        @Override
        public void doIt(@Nonnull final SlingHttpServletRequest request,
                         @Nonnull final SlingHttpServletResponse response,
                         @Nonnull final ResourceHandle resource)
                throws IOException {
            Status status = new Status(request, response);
            try {
                StagingReleaseManager.Release release = getRelease(request, response, resource, status);
                if (release != null) {
                    changeReleaseMetadata(request, release);
                    LOG.info("Release changed {}", release);
                    request.getResourceResolver().commit();
                }
            } catch (Exception ex) {
                status.withLogging(LOG).error("error changing release: {}", ex);
            }
            status.sendJson();
        }
    }

    protected class DeleteRelease implements ServletOperation {

        @Override
        public void doIt(@Nonnull final SlingHttpServletRequest request,
                         @Nonnull final SlingHttpServletResponse response,
                         @Nonnull final ResourceHandle resource)
                throws IOException {
            Status status = new Status(request, response);
            try {
                StagingReleaseManager.Release release = getRelease(request, response, resource, status);
                if (release != null) {
                    releaseManager.deleteRelease(release);
                    LOG.info("Release deleted {}", release);
                    request.getResourceResolver().commit();
                }
            } catch (StagingReleaseManager.ReleaseProtectedException e) {
                LOG.warn("Trying to delete a release carrying a mark: " + e);
                status.error("Cannot delete a release marked with preview or public.");
            } catch (Exception ex) {
                status.withLogging(LOG).error("error deleting release: {}", ex);
            }
            status.sendJson();
        }
    }

    protected void changeReleaseMetadata(@Nonnull final SlingHttpServletRequest request,
                                         @Nonnull final StagingReleaseManager.Release release)
            throws RepositoryException {

        final String title = request.getParameter(PARAM_TITLE);
        final String description = request.getParameter(PARAM_DESCRIPTION);

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
                                                       @Nonnull final ResourceHandle resource,
                                                       @Nonnull final Status status) {
        Site site = getReleaseSite(request, response, resource, status);
        if (site != null) {
            String releaseKey = getReleaseKey(request, resource, status);
            if (StringUtils.isNotBlank(releaseKey)) {
                return releaseManager.findRelease(site.getResource(), releaseKey);
            }
        } else {
            status.withLogging(LOG).error("no site found ({})", resource.getPath());
        }
        return null;
    }

    protected String getReleaseKey(@Nonnull final SlingHttpServletRequest request,
                                   @Nullable final Resource resource, @Nonnull final Status status) {
        String releaseKey = request.getParameter(PARAM_RELEASE_KEY);
        if (releaseKey == null && resource != null) {
            final String path = resource.getPath();
            final Matcher pathMatcher = StagingUtils.RELEASE_PATH_PATTERN.matcher(path);
            if (pathMatcher.matches()) {
                final String sitePath = pathMatcher.group(1);
                releaseKey = pathMatcher.group(2);
            } else {
                status.withLogging(LOG).error("no release path ({})", path);
            }
        }
        return releaseKey;
    }

    protected Site getReleaseSite(@Nonnull final SlingHttpServletRequest request,
                                  @Nonnull final SlingHttpServletResponse response,
                                  @Nonnull Resource resource, @Nonnull final Status status) {
        String path = request.getParameter(PARAM_PATH);
        if (path != null) {
            final ResourceResolver resourceResolver = request.getResourceResolver();
            resource = resourceResolver.getResource(path);
            if (resource != null && JcrConstants.JCR_CONTENT.equals(resource.getName())) {
                resource = resource.getParent();
            }
        }
        return resource != null
                ? siteManager.getContainingSite(new BeanContext.Servlet(
                getServletContext(), bundleContext, request, response), resource)
                : null;
    }
}
