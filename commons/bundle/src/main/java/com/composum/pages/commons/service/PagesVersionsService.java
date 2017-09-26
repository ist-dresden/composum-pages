package com.composum.pages.commons.service;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;
import javax.jcr.version.VersionManager;

@Component(
        label = "Composum Pages Versions Service",
        immediate = true,
        metatype = false
)
@Service
public class PagesVersionsService implements VersionsService {

    private static final Logger LOG = LoggerFactory.getLogger(PagesVersionsService.class);

    public void setVersionLabel(final ResourceResolver resolver, String path, String versionName, String label)
            throws RepositoryException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("setVersionLabel(" + path + "," + versionName + "," + label + ")");
        }
        final VersionHistory history = getVersionHistory(resolver, path);
        history.addVersionLabel(versionName, label, true);
    }

    public void removeVersionLabel(final ResourceResolver resolver, String path, String label)
            throws RepositoryException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("removeVersionLabel(" + path + "," + label + ")");
        }
        final VersionHistory history = getVersionHistory(resolver, path);
        history.removeVersionLabel(label);
    }

    public void restoreVersion(final ResourceResolver resolver, String path, String versionName)
            throws RepositoryException {
        VersionManager manager = getVersionManager(resolver);
        if (LOG.isInfoEnabled()) {
            LOG.info("restoreVersion(" + path + "," + versionName + ")");
        }
        manager.restore(path, versionName, false);
        VersionHistory history = manager.getVersionHistory(path);
        final VersionIterator allVersions = history.getAllVersions();
        while (allVersions.hasNext()) {
            final Version version = allVersions.nextVersion();
            if (version.getName().equals(versionName)) {
                break;
            }
        }
        while (allVersions.hasNext()) {
            final Version version = allVersions.nextVersion();
            if (LOG.isDebugEnabled()) {
                LOG.debug("restoreVersion.remove(" + path + "," + version.getName() + ")");
            }
            history.removeVersion(version.getName());
        }
        manager.checkout(path);
    }

    protected VersionManager getVersionManager(ResourceResolver resolver)
            throws RepositoryException {
        final JackrabbitSession session = (JackrabbitSession) resolver.adaptTo(Session.class);
        return session.getWorkspace().getVersionManager();
    }

    protected VersionHistory getVersionHistory(ResourceResolver resolver, String path)
            throws RepositoryException {
        final JackrabbitSession session = (JackrabbitSession) resolver.adaptTo(Session.class);
        final VersionManager manager = session.getWorkspace().getVersionManager();
        return manager.getVersionHistory(path);
    }
}
