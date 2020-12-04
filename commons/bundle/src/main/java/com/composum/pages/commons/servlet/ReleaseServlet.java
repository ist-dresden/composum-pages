package com.composum.pages.commons.servlet;

import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.service.SiteManager;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.servlet.AbstractServiceServlet;
import com.composum.sling.core.servlet.ServletOperation;
import com.composum.sling.core.servlet.ServletOperationSet;
import com.composum.sling.core.servlet.Status;
import com.composum.sling.core.util.RequestUtil;
import com.composum.sling.core.util.XSS;
import com.composum.platform.commons.request.AccessMode;
import com.composum.sling.platform.staging.*;
import com.composum.sling.platform.staging.Release;
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

import static com.composum.sling.core.util.CoreConstants.JCR_DESCRIPTION;
import static com.composum.sling.core.util.CoreConstants.JCR_TITLE;
import static com.composum.sling.platform.staging.StagingConstants.CURRENT_RELEASE;
import static com.composum.sling.platform.staging.impl.PlatformStagingServlet.getReleaseKey;
import static org.apache.jackrabbit.JcrConstants.JCR_LASTMODIFIED;

/**
 * @author Mirko Zeibig
 * @since 16.09.16.
 */
@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Release Servlet",
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/cpm/pages/release",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_POST
        })
public class ReleaseServlet extends AbstractServiceServlet {

    private static final Logger LOG = LoggerFactory.getLogger(ReleaseServlet.class);

    public static final String PARAM_NUMBER = "number";

    public static final String PARAM_PUBLISH = "publish";
    public static final String PARAM_CURRENT = "current";

    protected BundleContext bundleContext;

    @Reference
    private SiteManager siteManager;

    @Reference
    private StagingReleaseManager releaseManager;

    @Reference
    private ReleaseChangeEventPublisher releasePublisher;

    enum Extension {
        json
    }

    enum Operation {
        finalize,
        change,
        delete
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
    protected ReleaseOperationSet getOperations() {
        return operations;
    }

    //
    // release state
    //

    private class FinalizeRelease implements ServletOperation {

        @Override
        public void doIt(@Nonnull final SlingHttpServletRequest request,
                         @Nonnull final SlingHttpServletResponse response,
                         @Nullable final ResourceHandle resource)
                throws IOException {
            Status status = new Status(request, response);
            if (resource != null && resource.isValid()) {
                try {

                    Site site = getReleaseSite(request, response, resource, status);
                    if (site != null) {

                        final String numberPolicy = status.getRequiredParameter(PARAM_NUMBER, null, "no release number policy");

                        if (status.isValid()) {

                            ReleaseNumberCreator releaseType;
                            try {
                                releaseType = ReleaseNumberCreator.valueOf(numberPolicy);
                            } catch (IllegalArgumentException e) {
                                releaseType = ReleaseNumberCreator.BUGFIX;
                            }

                            Release release = releaseManager
                                    .finalizeCurrentRelease(Objects.requireNonNull(resource), releaseType);
                            LOG.info("Release created {}", release);

                            changeReleaseMetadata(request, release);

                            releaseToStage(release,
                                    XSS.filter(request.getParameterValues(PARAM_PUBLISH)));
                            releaseToStage(releaseManager.findRelease(resource, CURRENT_RELEASE),
                                    XSS.filter(request.getParameterValues(PARAM_CURRENT)));

                            request.getResourceResolver().commit();
                        }
                    } else {
                        status.error("no site found ({})", resource.getPath());
                    }
                } catch (Exception ex) {
                    status.error("error creating release: {}", ex);
                }
            } else {
                status.error("requests resource not available");
            }
            status.sendJson();
        }
    }

    protected void releaseToStage(@Nullable Release release, @Nullable String[] stages)
            throws ReleaseChangeFailedException, RepositoryException {
        if (release != null && stages != null && stages.length > 0) {
            for (String stage : stages) {
                LOG.info("Publishing release '{}' to stage '{}'.", release, stage);
                stage = AccessMode.valueOf(stage.toUpperCase()).name().toLowerCase();
                // replication is triggered by setMark via the ReleaseChangeEventListener .
                releaseManager.setMark(stage, release, false);
            }
        }
    }

    protected class ChangeMetadata implements ServletOperation {

        @Override
        public void doIt(@Nonnull final SlingHttpServletRequest request,
                         @Nonnull final SlingHttpServletResponse response,
                         @Nullable final ResourceHandle resource)
                throws IOException {
            Status status = new Status(request, response);
            if (resource != null && resource.isValid()) {
                try {
                    Release release = getRelease(request, response, resource, status);
                    if (release != null) {
                        changeReleaseMetadata(request, release);
                        LOG.info("Release changed {}", release);
                        request.getResourceResolver().commit();
                    }
                } catch (Exception ex) {
                    status.error("error changing release: {}", ex);
                }
            } else {
                status.error("requests resource not available");
            }
            status.sendJson();
        }
    }

    protected class DeleteRelease implements ServletOperation {

        @Override
        public void doIt(@Nonnull final SlingHttpServletRequest request,
                         @Nonnull final SlingHttpServletResponse response,
                         @Nullable final ResourceHandle resource)
                throws IOException {
            Status status = new Status(request, response);
            if (resource != null && resource.isValid()) {
                try {
                    Release release = getRelease(request, response, resource, status);
                    if (release != null) {
                        releaseManager.deleteRelease(release);
                        LOG.info("Release deleted {}", release);
                        request.getResourceResolver().commit();
                    }
                } catch (StagingReleaseManager.ReleaseProtectedException e) {
                    LOG.warn("Trying to delete a release carrying a mark: " + e);
                    status.error("Cannot delete a release marked with preview or public.");
                } catch (Exception ex) {
                    status.error("error deleting release: {}", ex);
                }
            } else {
                status.error("requests resource not available");
            }
            status.sendJson();
        }
    }

    protected void changeReleaseMetadata(@Nonnull final SlingHttpServletRequest request,
                                         @Nonnull final Release release)
            throws RepositoryException {

        final String title = RequestUtil.getParameter(request, JCR_TITLE, "");
        final String description = RequestUtil.getParameter(request, JCR_DESCRIPTION, "");

        ResourceHandle metaData = ResourceHandle.use(release.getMetaDataNode());
        metaData.setProperty(JCR_TITLE, StringUtils.isNotBlank(title) ? title : null);
        metaData.setProperty(JCR_DESCRIPTION, StringUtils.isNotBlank(description) ? description : null);
        metaData.setProperty(JCR_LASTMODIFIED, Calendar.getInstance());
        metaData.setProperty(JCR_LASTMODIFIED + "By", request.getResourceResolver().getUserID());
    }

    protected Release getRelease(@Nonnull final SlingHttpServletRequest request,
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
            status.error("no site found ({})", resource.getPath());
        }
        return null;
    }

    protected Site getReleaseSite(@Nonnull final SlingHttpServletRequest request,
                                  @Nonnull final SlingHttpServletResponse response,
                                  @Nonnull Resource resource, @Nonnull final Status status) {
        String path = XSS.filter(request.getParameter(PARAM_PATH));
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
