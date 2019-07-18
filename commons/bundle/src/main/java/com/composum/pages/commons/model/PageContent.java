package com.composum.pages.commons.model;

import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.lock.Lock;
import javax.jcr.lock.LockManager;

/**
 * Created by rw on 13.01.17.
 */
public class PageContent extends ContentModel<Page> {

    private static final Logger LOG = LoggerFactory.getLogger(PageContent.class);

    private transient Boolean locked;
    private transient String lockOwner;
    private transient Boolean checkedOut;

    public PageContent() {
    }

    public PageContent(BeanContext context, Resource resource) {
        initialize(context, resource);
    }

    public boolean isLocked() {
        if (locked == null) {
            locked = false;
            lockOwner = "";
            Node node = getResource().adaptTo(Node.class);
            if (node != null) {
                Session session = getContext().getResolver().adaptTo(Session.class);
                if (session != null) {
                    Workspace workspace = session.getWorkspace();
                    try {
                        LockManager lockManager = workspace.getLockManager();
                        locked = node.isLocked();
                        if (locked) {
                            Lock lock = lockManager.getLock(node.getPath());
                            lockOwner = lock.getLockOwner();
                        }
                    } catch (RepositoryException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }
            }
        }
        return locked;
    }

    public String getLockOwner() {
        return isLocked() ? lockOwner : "";
    }

    public boolean isCheckedOut() {
        if (checkedOut == null) {
            checkedOut = false;
            Node node = getResource().adaptTo(Node.class);
            if (node != null) {
                try {
                    checkedOut = node.isCheckedOut();
                } catch (RepositoryException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
        }
        return checkedOut;
    }

    public boolean isThumbnailAvailable() {
        return getResource().getChild("thumbnail/image") != null;
    }
}
