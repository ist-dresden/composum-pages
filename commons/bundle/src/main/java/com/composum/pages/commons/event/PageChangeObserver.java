package com.composum.pages.commons.event;

import com.composum.sling.core.event.AbstractChangeObserver;
import com.composum.sling.core.util.NodeUtil;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.text.SimpleDateFormat;

import static com.composum.pages.commons.PagesConstants.NODE_TYPE_PAGE_CONTENT;
import static com.composum.pages.commons.PagesConstants.NODE_TYPE_SITE_CONFIGURATION;

/**
 * the observer to adjust page change properties on page content resource changes
 */
@Component(immediate = true)
@Service
public class PageChangeObserver extends AbstractChangeObserver {

    private static final Logger LOG = LoggerFactory.getLogger(PageChangeObserver.class);

    public static final String PAGES_SERVICE_USER = "composum-pages-service";

    public static final String PATH = "/content";

    @Reference
    protected SlingRepository repository;

    @Reference
    protected ResourceResolverFactory resolverFactory;

    @Override
    protected String getServiceUserId() {
        return PAGES_SERVICE_USER;
    }

    @Override
    protected String getObservedPath() {
        return PATH;
    }

    /**
     * changes the last modified properties for one change item
     */
    @Override
    protected void doOnChange(ResourceResolver resolver, ChangedResource change)
            throws RepositoryException {
        if (LOG.isInfoEnabled()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(LOG_DATE_FORMAT);
            LOG.info("changed: " + change.getResource().getPath() + ", " +
                    dateFormat.format(change.getTime().getTime()) + ", " +
                    change.getUser());
        }
        Resource resource = change.getResource();
        ModifiableValueMap values = resource.adaptTo(ModifiableValueMap.class);
        values.put(ResourceUtil.PROP_LAST_MODIFIED, change.getTime());
        values.put(PROP_LAST_MODIFIED_BY, change.getUser());
    }

    @Override
    protected boolean isTargetNode(Node node)
            throws RepositoryException {
        return NodeUtil.isNodeType(node,
                NODE_TYPE_PAGE_CONTENT,
                NODE_TYPE_SITE_CONFIGURATION);
    }

    @Override
    protected String getTargetPath(Node node)
            throws RepositoryException {
        return NodeUtil.isNodeType(node,
                ResourceUtil.TYPE_FOLDER,
                ResourceUtil.TYPE_SLING_FOLDER,
                ResourceUtil.TYPE_SLING_ORDERED_FOLDER)
                ? null : node.getPath();
    }

    @Override
    protected ResourceResolver getResolver()
            throws LoginException {
        return resolverFactory != null ? resolverFactory.getServiceResourceResolver(null) : null;
    }

    @Override
    protected Session getSession()
            throws RepositoryException {
        return repository != null ? repository.loginService(null, null) : null;
    }
}