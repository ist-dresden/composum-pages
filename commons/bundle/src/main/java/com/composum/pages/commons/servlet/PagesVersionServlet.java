package com.composum.pages.commons.servlet;

import com.composum.pages.commons.service.VersionsService;
import com.composum.pages.commons.util.RequestUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.mapping.MappingRules;
import com.composum.sling.core.servlet.AbstractServiceServlet;
import com.composum.sling.core.servlet.ServletOperation;
import com.composum.sling.core.servlet.ServletOperationSet;
import com.composum.sling.core.servlet.Status;
import com.composum.sling.core.util.ResourceUtil;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestDispatcherOptions;
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
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.lock.Lock;
import javax.jcr.lock.LockManager;
import javax.jcr.version.VersionManager;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.composum.pages.commons.servlet.PagesContentServlet.forward;

@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Version Servlet",
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/cpm/pages/version",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_POST,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_PUT
        })
public class PagesVersionServlet extends AbstractServiceServlet {

    private static final Logger LOG = LoggerFactory.getLogger(PagesVersionServlet.class);

    public static final String VERSIONS_RESOURCE_TYPE = "composum/pages/stage/edit/tools/page/versions";

    @Reference
    protected VersionsService versionsService;

    protected BundleContext bundleContext;

    @Override
    protected boolean isEnabled() {
        return true;
    }

    @Activate
    private void activate(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    //
    // Servlet operations
    //

    public enum Extension {
        html, json
    }

    public enum Operation {
        list,
        checkpoint, checkin, checkout, toggleCheckout,
        rollbackVersion,
        lock, unlock, toggleLock
    }

    protected PagesEditOperationSet operations = new PagesEditOperationSet();

    @Override
    protected ServletOperationSet getOperations() {
        return operations;
    }

    /** setup of the servlet operation set for this servlet instance */
    @Override
    @SuppressWarnings("Duplicates")
    public void init() throws ServletException {
        super.init();

        // GET
        operations.setOperation(ServletOperationSet.Method.GET, Extension.html,
                Operation.list, new GetVersions());

        // POST
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.checkpoint, new CheckpointOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.checkin, new CheckinOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.checkout, new CheckoutOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.toggleCheckout, new ToggleCheckoutOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.lock, new LockOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.unlock, new UnlockOperation());
        operations.setOperation(ServletOperationSet.Method.POST, Extension.json,
                Operation.toggleLock, new ToggleLockOperation());

        // PUT
        operations.setOperation(ServletOperationSet.Method.PUT, Extension.json,
                Operation.rollbackVersion, new RollbackVersion());
    }

    public class PagesEditOperationSet extends
            ServletOperationSet<Extension, Operation> {

        public PagesEditOperationSet() {
            super(Extension.json);
        }
    }

    //
    // Content Versions
    //

    protected class GetVersions implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws ServletException, IOException {

            String selectors = RequestUtil.getSelectorString(request, null, 1);
            if (LOG.isDebugEnabled()) {
                LOG.debug("GetVersions({},{})...", resource, selectors);
            }

            RequestDispatcherOptions options = new RequestDispatcherOptions();
            options.setForceResourceType(VERSIONS_RESOURCE_TYPE);
            if (StringUtils.isNotBlank(selectors)) {
                options.setReplaceSelectors(selectors);
            }

            forward(request, response, resource, null, options);
        }
    }

    protected abstract class VersionOperation implements ServletOperation {

        abstract void performIt(@Nonnull final Status status,
                                @Nonnull final VersionManager versionManager,
                                @Nonnull final Collection<Resource> versionable)
                throws RepositoryException;

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response, ResourceHandle resource)
                throws IOException {
            Status status = new Status(request, response);
            try {
                List<Resource> versionable = new ArrayList<>();
                final ResourceResolver resolver = request.getResourceResolver();
                final RequestParameter paths = request.getRequestParameter("paths");
                if (paths != null) {
                    for (String path : paths.getString().split(",")) {
                        addVersionable(resolver, versionable, path);
                    }
                } else {
                    addVersionable(resolver, versionable, request.getRequestPathInfo().getSuffix());
                }
                if (versionable.size() > 0) {
                    final JackrabbitSession session = (JackrabbitSession) resolver.adaptTo(Session.class);
                    if (session != null) {
                        final VersionManager versionManager = session.getWorkspace().getVersionManager();
                        performIt(status, versionManager, versionable);
                        session.save();
                    } else {
                        status.withLogging(LOG).error("can't adapt to session");
                    }
                } else {
                    status.withLogging(LOG).error("no versionable resources found");
                }
            } catch (final RepositoryException ex) {
                status.withLogging(LOG).error(ex.getLocalizedMessage(), ex);
            }
            status.sendJson();
        }

        protected void addVersionable(@Nonnull final ResourceResolver resolver,
                                      @Nonnull final List<Resource> versionable, @Nullable final String path) {
            if (StringUtils.isNotBlank(path)) {
                Resource resource = resolver.getResource(path);
                if (!ResourceUtil.isNodeType(resource, JcrConstants.MIX_VERSIONABLE) && resource != null) {
                    resource = resource.getChild(JcrConstants.JCR_CONTENT);
                }
                if (ResourceUtil.isNodeType(resource, JcrConstants.MIX_VERSIONABLE)) {
                    versionable.add(resource);
                }
            }
        }
    }

    protected class CheckpointOperation extends VersionOperation {

        @Override
        void performIt(@Nonnull final Status status,
                       @Nonnull final VersionManager versionManager,
                       @Nonnull final Collection<Resource> versionable)
                throws RepositoryException {
            for (Resource resource : versionable) {
                String path = resource.getPath();
                if (versionManager.isCheckedOut(path)) {
                    versionManager.checkpoint(path);
                }
            }
        }
    }

    protected class ToggleCheckoutOperation extends VersionOperation {

        @Override
        void performIt(@Nonnull final Status status,
                       @Nonnull final VersionManager versionManager,
                       @Nonnull final Collection<Resource> versionable)
                throws RepositoryException {
            for (Resource resource : versionable) {
                String path = resource.getPath();
                if (versionManager.isCheckedOut(path)) {
                    versionManager.checkin(path);
                } else {
                    versionManager.checkout(path);
                }
            }
        }
    }

    protected class CheckinOperation extends VersionOperation {

        @Override
        void performIt(@Nonnull final Status status,
                       @Nonnull final VersionManager versionManager,
                       @Nonnull final Collection<Resource> versionable)
                throws RepositoryException {
            for (Resource resource : versionable) {
                String path = resource.getPath();
                if (versionManager.isCheckedOut(path)) {
                    versionManager.checkin(path);
                }
            }
        }
    }

    protected class CheckoutOperation extends VersionOperation {

        @Override
        void performIt(@Nonnull final Status status,
                       @Nonnull final VersionManager versionManager,
                       @Nonnull final Collection<Resource> versionable)
                throws RepositoryException {
            for (Resource resource : versionable) {
                String path = resource.getPath();
                if (!versionManager.isCheckedOut(path)) {
                    versionManager.checkout(path);
                }
            }
        }
    }

    static class VersionPutParameters {

        public String path;
        public String version;
        public String label;

        public void setPath(String path) {
            this.path = path;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }

    protected class RollbackVersion implements ServletOperation {

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response,
                         ResourceHandle resource)
                throws IOException {
            Status status = new Status(request, response);

            final Gson gson = new Gson();
            final VersionPutParameters params = gson.fromJson(
                    new InputStreamReader(request.getInputStream(), MappingRules.CHARSET.name()),
                    VersionPutParameters.class);

            if (LOG.isDebugEnabled()) {
                LOG.debug("RollbackVersoin({},{})...", params.path, params.version);
            }

            try {
                BeanContext context = new BeanContext.Servlet(getServletContext(), bundleContext, request, response);
                versionsService.rollbackVersion(context, params.path, params.version);

            } catch (RepositoryException ex) {
                status.withLogging(LOG).error(ex.getLocalizedMessage(), ex);
            }

            status.sendJson();
        }
    }

    //
    // lockable content locking
    //

    protected abstract class LockingOperation implements ServletOperation {

        abstract void performIt(@Nonnull final Status status,
                                @Nonnull final Session session,
                                @Nonnull final LockManager lockManager,
                                @Nonnull final Collection<Resource> versionable)
                throws RepositoryException;

        @Override
        public void doIt(SlingHttpServletRequest request, SlingHttpServletResponse response, ResourceHandle resource)
                throws IOException {
            Status status = new Status(request, response);
            try {
                List<Resource> lockable = new ArrayList<>();
                final ResourceResolver resolver = request.getResourceResolver();
                final RequestParameter paths = request.getRequestParameter("paths");
                if (paths != null) {
                    for (String path : paths.getString().split(",")) {
                        addLockable(resolver, lockable, path);
                    }
                } else {
                    addLockable(resolver, lockable, request.getRequestPathInfo().getSuffix());
                }
                if (lockable.size() > 0) {
                    final Session session = resolver.adaptTo(Session.class);
                    if (session != null) {
                        Workspace workspace = session.getWorkspace();
                        LockManager lockManager = workspace.getLockManager();
                        performIt(status, session, lockManager, lockable);
                        session.save();
                    } else {
                        status.withLogging(LOG).error("can't adapt to session");
                    }
                } else {
                    status.withLogging(LOG).error("no lockable resources found");
                }
            } catch (final RepositoryException ex) {
                status.withLogging(LOG).error(ex.getLocalizedMessage(), ex);
            }
            status.sendJson();
        }

        protected void addLockable(@Nonnull final ResourceResolver resolver,
                                   @Nonnull final List<Resource> lockable, @Nullable final String path) {
            if (StringUtils.isNotBlank(path)) {
                Resource resource = resolver.getResource(path);
                if (!ResourceUtil.isNodeType(resource, JcrConstants.MIX_LOCKABLE) && resource != null) {
                    resource = resource.getChild(JcrConstants.JCR_CONTENT);
                }
                if (ResourceUtil.isNodeType(resource, JcrConstants.MIX_LOCKABLE)) {
                    lockable.add(resource);
                }
            }
        }
    }

    protected class ToggleLockOperation extends LockingOperation {

        @Override
        void performIt(@Nonnull final Status status,
                       @Nonnull final Session session,
                       @Nonnull final LockManager lockManager,
                       @Nonnull final Collection<Resource> versionable)
                throws RepositoryException {
            for (Resource resource : versionable) {
                Node node = resource.adaptTo(Node.class);
                if (node != null) {
                    String path = node.getPath();
                    if (node.isLocked()) {
                        Lock lock = lockManager.getLock(path);
                        String token = lock.getLockToken();
                        lockManager.addLockToken(token);
                        lockManager.unlock(path);
                    } else {
                        if (!node.isNodeType(JcrConstants.MIX_LOCKABLE)){
                            node.addMixin(JcrConstants.MIX_LOCKABLE);
                            session.save();
                        }
                        lockManager.lock(path, true, false, Long.MAX_VALUE, session.getUserID());
                    }
                }
            }
        }
    }

    protected class LockOperation extends LockingOperation {

        @Override
        void performIt(@Nonnull final Status status,
                       @Nonnull final Session session,
                       @Nonnull final LockManager lockManager,
                       @Nonnull final Collection<Resource> lockable)
                throws RepositoryException {
            for (Resource resource : lockable) {
                Node node = resource.adaptTo(Node.class);
                if (node != null && !node.isLocked()) {
                    if (!node.isNodeType(JcrConstants.MIX_LOCKABLE)){
                        node.addMixin(JcrConstants.MIX_LOCKABLE);
                        session.save();
                    }
                    lockManager.lock(node.getPath(), true, false, Long.MAX_VALUE, session.getUserID());
                }
            }
        }
    }

    protected class UnlockOperation extends LockingOperation {

        @Override
        void performIt(@Nonnull final Status status,
                       @Nonnull final Session session,
                       @Nonnull final LockManager lockManager,
                       @Nonnull final Collection<Resource> lockable)
                throws RepositoryException {
            for (Resource resource : lockable) {
                Node node = resource.adaptTo(Node.class);
                if (node != null && node.isLocked()) {
                    String path = node.getPath();
                    Lock lock = lockManager.getLock(path);
                    String token = lock.getLockToken();
                    lockManager.addLockToken(token);
                    lockManager.unlock(path);
                }
            }
        }
    }
}
