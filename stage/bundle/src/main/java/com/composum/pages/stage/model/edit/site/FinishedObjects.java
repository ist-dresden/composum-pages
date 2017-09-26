package com.composum.pages.stage.model.edit.site;

import com.composum.pages.commons.model.AbstractModel;
import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.Release;
import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.service.PageManager;
import com.composum.sling.core.BeanContext;
import com.composum.sling.platform.staging.StagingUtils;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.sling.api.resource.NonExistingResource;
import org.apache.sling.api.resource.Resource;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionManager;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * @author Mirko Zeibig
 */
public class FinishedObjects extends AbstractModel {

    private transient VersionManager versionManager;

    public List<Resource> getObjectList() throws RepositoryException {
        List<Resource> result = new ArrayList<>();
        final Page currentPage = getCurrentPage();
        final Site site = currentPage.getSite();
        final Resource siteResource = site.getResource();
        final List<Release> releases = site.getReleases();
        if (releases.isEmpty()) {
            collectChildren(siteResource, new NoRelease(context), result);
        } else {
            Collections.sort(releases);
            collectChildren(siteResource, releases.get(releases.size() - 1), result);
        }
        return result;
    }

    private void collectChildren(Resource parent, Release release, List<Resource> result) throws RepositoryException {
        final Iterable<Resource> children = parent.getChildren();
        PageManager pageManager = getPageManager();
        for (Resource r: children) {
            if (Page.isPage(r)) {
                final Page page = pageManager.createBean(context, r);
                VersionManager versionManager = getVersionManager();
                if (StagingUtils.isVersionable(page.getContent().getResource())) {
                    Version currentVersion = versionManager.getBaseVersion(page.getContent().getPath());
                    Calendar currentVersionCreated = currentVersion.getCreated();
                    final VersionHistory versionHistory = versionManager.getVersionHistory(page.getContent().getPath());
                    try {
                        final Version versionByLabel = versionHistory.getVersionByLabel(release.getLabel());
                        final Calendar labeledCreated = versionByLabel.getCreated();
                        if (currentVersionCreated.after(labeledCreated)) {
                            result.add(page.getResource());
                        }
                    } catch (VersionException e) {
                        // no label
                        if (!currentVersion.getName().equals("jcr:rootVersion")) {
                            result.add(page.getResource());
                        }
                    }
                }
                collectChildren(page.getResource(), release, result);
            }
        }
    }

    private VersionManager getVersionManager() throws RepositoryException {
        if (versionManager == null) {
            final JackrabbitSession session = (JackrabbitSession) resolver.adaptTo(Session.class);
            versionManager = session.getWorkspace().getVersionManager();
        }
        return versionManager;
    }

    private class NoRelease extends Release {

        NoRelease(BeanContext context) {
            super(context, new NonExistingResource(FinishedObjects.this.resolver, FinishedObjects.this.getPath()));
        }

        @Override
        public String getLabel() {
            //no existing label
            return "w81t5l6pSYAeeN5c";
        }
    }
}
