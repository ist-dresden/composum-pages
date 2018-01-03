package com.composum.pages.commons.service;

import com.composum.sling.core.BeanContext;
import com.composum.sling.platform.staging.query.Query;
import com.composum.sling.platform.staging.query.QueryBuilder;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.composum.pages.commons.PagesConstants.NODE_TYPE_WIDGET;
import static com.composum.pages.commons.PagesConstants.PROP_WIDGET_TYPE;


@Component(
        label = "Composum Pages Widget Manager",
        immediate = true
)
@Service
public class PagesWidgetManager implements WidgetManager {

    private static final Logger LOG = LoggerFactory.getLogger(PagesWidgetManager.class);

    protected Map<String, String> widgetTypes = new HashMap<>();

    public synchronized String getWidgetTypeResourcePath(BeanContext context, String widgetType) {
        String typePath = widgetTypes.get(widgetType);
        if (typePath == null) {
            ResourceResolver resolver = context.getResolver();
            for (String root : resolver.getSearchPath()) {
                try {
                    Resource typeRes = findByName(resolver, widgetType, root);
                    if (typeRes == null) {
                        typeRes = findByProperty(resolver, widgetType, root);
                    }
                    if (typeRes != null) {
                        typePath = typeRes.getPath();
                        widgetTypes.put(widgetType, typePath);
                        return typePath;
                    }
                } catch (RepositoryException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
        }
        return typePath;
    }

    protected Resource findByName(ResourceResolver resolver, String widgetType, String root)
            throws RepositoryException {
        Query query = resolver.adaptTo(QueryBuilder.class).createQuery();
        query.path(root).type(NODE_TYPE_WIDGET).element(widgetType);
        Iterator<Resource> found = query.execute().iterator();
        if (found.hasNext()) {
            return found.next();
        }
        return null;
    }

    protected Resource findByProperty(ResourceResolver resolver, String widgetType, String root)
            throws RepositoryException {
        Query query = resolver.adaptTo(QueryBuilder.class).createQuery();
        query.path(root).type(NODE_TYPE_WIDGET).condition(query.conditionBuilder().in(PROP_WIDGET_TYPE, widgetType));
        Iterator<Resource> found = query.execute().iterator();
        if (found.hasNext()) {
            return found.next();
        }
        return null;
    }
}
