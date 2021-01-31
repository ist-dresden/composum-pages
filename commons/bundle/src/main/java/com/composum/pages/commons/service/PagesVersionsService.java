package com.composum.pages.commons.service;

import com.composum.pages.commons.model.ContentVersion;
import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.SiteRelease;
import com.composum.platform.commons.util.ExceptionThrowingFunction;
import com.composum.sling.core.BeanContext;
import com.composum.sling.platform.staging.Release;
import com.composum.sling.platform.staging.StagingConstants;
import com.composum.sling.platform.staging.StagingReleaseManager;
import com.composum.sling.platform.staging.VersionReference;
import com.composum.sling.platform.staging.impl.VersionSelectResourceResolver;
import com.composum.sling.platform.staging.versions.PlatformVersionsService;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;
import javax.jcr.version.VersionManager;
import java.util.*;

@Component(
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Versions Service"
        }
)
public class PagesVersionsService implements VersionsService {

    private static final Logger LOG = LoggerFactory.getLogger(PagesVersionsService.class);

    @Reference
    protected PageManager pageManager;

    @Reference
    protected StagingReleaseManager releaseManager;

    @Reference
    protected PlatformVersionsService platformVersionsService;

    protected List<VersionFactory> versionFactories = Collections.synchronizedList(new ArrayList<>());

    @Reference(
            service = VersionFactory.class,
            policy = ReferencePolicy.DYNAMIC,
            cardinality = ReferenceCardinality.MULTIPLE
    )
    protected void addVersionFactory(@Nonnull final VersionFactory factory) {
        LOG.info("addVersionFactory: {}", factory.getClass().getName());
        versionFactories.add(factory);
    }

    protected void removeVersionFactory(@Nonnull final VersionFactory factory) {
        LOG.info("removeVersionFactory: {}", factory.getClass().getName());
        versionFactories.remove(factory);
    }

    /**
     * @return the workspace resource of the staging resource of the referenced version
     */
    @Override
    @Nullable
    public Resource getResource(@Nonnull final BeanContext context,
                                @Nonnull final PlatformVersionsService.Status status) {
        Resource resource = status.getWorkspaceResource();
        if (resource != null) {
            VersionReference reference = status.getVersionReference();
            String uuid;
            if (reference != null && (uuid = reference.getReleasedVersionable().getVersionUuid()) != null) {
                try {
                    resource = historicalVersion(context.getResolver(), status.getPath(), uuid);
                } catch (RepositoryException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
        }
        return resource;
    }

    @Override
    public void rollbackVersion(final BeanContext context, String path, String versionName)
            throws RepositoryException {
        VersionManager manager = getVersionManager(context);
        if (LOG.isInfoEnabled()) {
            LOG.info("rollbackVersion({},{})", path, versionName);
        }
        manager.restore(path, versionName, false);
        // Unfortunately, the VersionManager does not offer any way to copy out an old version, and if we just
        // restore an old version, we'll another branch when checking in again, which would be ... inconvenient. ...
        VersionHistory history = manager.getVersionHistory(path);
        final VersionIterator allVersions = history.getAllVersions();
        while (allVersions.hasNext()) {
            final javax.jcr.version.Version version = allVersions.nextVersion();
            if (version.getName().equals(versionName)) {
                break;
            }
        }
        while (allVersions.hasNext()) {
            final javax.jcr.version.Version version = allVersions.nextVersion();
            if (LOG.isDebugEnabled()) {
                LOG.debug("rollbackVersion.remove({},{})", path, version.getName());
            }
            history.removeVersion(version.getName());
        }
        manager.checkout(path);
    }

    /**
     * @return a collection of all versionables which are changed in a release in comparision to the release before
     */
    @Nonnull
    @Override
    public List<ContentVersion> findReleaseChanges(@Nonnull final BeanContext context,
                                                   @Nullable final SiteRelease siteRelease,
                                                   @Nullable final ContentVersionFilter filter)
            throws RepositoryException {
        ExceptionThrowingFunction<Release, List<PlatformVersionsService.Status>, RepositoryException> getChanges =
                platformVersionsService::findReleaseChanges;
        return findContentVersions(siteRelease, getChanges, filter);
    }

    @Override
    @Nonnull
    public List<ContentVersion> findModifiedContent(@Nonnull final BeanContext context, final SiteRelease siteRelease,
                                                    @Nullable final ContentVersionFilter filter)
            throws RepositoryException {
        ExceptionThrowingFunction<Release, List<PlatformVersionsService.Status>, RepositoryException> getChanges =
                platformVersionsService::findWorkspaceChanges;
        return findContentVersions(siteRelease, getChanges, filter);
    }

    @Override
    public Resource historicalVersion(@Nonnull ResourceResolver resolver, @Nonnull String path,
                                      @Nonnull String versionUuid) throws RepositoryException {
        ResourceResolver versionSelectResolver = new VersionSelectResourceResolver(resolver, false, versionUuid);
        Resource resource = versionSelectResolver.getResource(path);
        if (resource != null) {
            boolean versionNotFound = true;
            Resource checkable = Page.isPage(resource) ? resource.getChild(JcrConstants.JCR_CONTENT) : resource;
            if (checkable != null) {
                for (Resource searchResource = checkable; searchResource != null && versionNotFound;
                     searchResource = searchResource.getParent()) {
                    versionNotFound = !StringUtils.equals(versionUuid,
                            searchResource.getValueMap().get(StagingConstants.PROP_REPLICATED_VERSION, String.class));
                }
                if (versionNotFound) {
                    LOG.warn("historicalVersion: versionUuid '{}' doesn't fit path '{}'", versionUuid, path);
                    resource = null;
                }
            } else {
                LOG.warn("historicalVersion: requested page '{}', version '{}' has no content", path, versionUuid);
                resource = null;
            }
        }
        return resource;
    }

    @Nonnull
    protected List<ContentVersion> findContentVersions(@Nullable SiteRelease siteRelease,
                                                       @Nonnull ExceptionThrowingFunction<Release, List<PlatformVersionsService.Status>, RepositoryException> getChanges,
                                                       @Nullable final ContentVersionFilter filter)
            throws RepositoryException {
        List<ContentVersion> result = new ArrayList<>();
        if (siteRelease != null) {
            Release release = siteRelease.getStagingRelease();
            List<PlatformVersionsService.Status> changes = getChanges.apply(release);
            for (PlatformVersionsService.Status status : changes) {
                ContentVersion pv = getContentVersion(siteRelease, status);
                if (pv != null && (filter == null || filter.accept(pv))) {
                    result.add(pv);
                }
            }
            result.sort(Comparator.comparing(ContentVersion::getPath));
        }
        return result;
    }

    @Nullable
    protected ContentVersion getContentVersion(@Nonnull final SiteRelease siteRelease,
                                               @Nonnull final PlatformVersionsService.Status status) {
        String type = null;
        Resource resource = getResource(siteRelease.getContext(), status);
        if (resource != null) { // deleted in workspace -> look for released version to find out at least the type
            ValueMap values = resource.getValueMap();
            type = values.get(JcrConstants.JCR_PRIMARYTYPE, "");
        } else if (status.getVersionReference() != null) {
            type = status.getVersionReference().getType();
        }
        if (type != null) {
            for (VersionFactory factory : versionFactories) {
                ContentVersion version = factory.getContentVersion(siteRelease, resource, type, status);
                if (version != null) {
                    return version;
                }
            }
        }
        return null;
    }

    protected VersionManager getVersionManager(final BeanContext context)
            throws RepositoryException {
        SlingHttpServletRequest request = context.getRequest();
        VersionManager versionManager = (VersionManager) request.getAttribute(VersionManager.class.getName());
        if (versionManager == null) {
            final JackrabbitSession session = Objects.requireNonNull(
                    (JackrabbitSession) context.getResolver().adaptTo(Session.class));
            versionManager = session.getWorkspace().getVersionManager();
            request.setAttribute(VersionManager.class.getName(), versionManager);
        }
        return versionManager;
    }
}
