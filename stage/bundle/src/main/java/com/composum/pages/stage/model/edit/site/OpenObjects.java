package com.composum.pages.stage.model.edit.site;

import com.composum.pages.commons.model.AbstractModel;
import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.service.PageManager;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.sling.api.resource.Resource;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.version.Version;
import javax.jcr.version.VersionManager;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author Mirko Zeibig
 */
public class OpenObjects extends AbstractModel {

    private transient VersionManager versionManager;

    public List<Resource> getObjectList() {
        List<Resource> result = new ArrayList<>();
        final Page currentPage = getCurrentPage();
        final Site site = currentPage.getSite();
        final Resource siteResource = site.getResource();
        collectChildren(siteResource, result);
        return result;
    }

    private boolean isModified(Page page) {
        try {
            Calendar lastModified = getLastModified(page);
            if (lastModified != null) {
                VersionManager versionManager = getVersionManager();
                Version currentVersion = versionManager.getBaseVersion(page.getContent().getPath());
                Calendar currentVersionCreated = currentVersion.getCreated();
                return (lastModified.after(currentVersionCreated) || currentVersion.getName().equals("jcr:rootVersion"));
            }
            return false;
        } catch (RepositoryException e) {
            return false;
        }
    }

    private Calendar getLastModified(Page page) {
        return page.getContent().getLastModified();
    }

    private VersionManager getVersionManager() throws RepositoryException {
        if (versionManager == null) {
            final JackrabbitSession session = (JackrabbitSession) resolver.adaptTo(Session.class);
            versionManager = session.getWorkspace().getVersionManager();
        }
        return versionManager;
    }

    private void collectChildren(Resource parent, List<Resource> result) {
        PageManager pageManager = getPageManager();
        final Iterable<Resource> children = parent.getChildren();
        for (Resource r:children) {
            if (Page.isPage(r)) {
                final Page page = pageManager.createBean(context, r);
                if (isModified(page)) {
                    result.add(page.getResource());
                }
                collectChildren(page.getResource(), result);
            }
        }
    }

}
