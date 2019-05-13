/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.service;

import com.composum.sling.platform.staging.query.Query;
import com.composum.sling.platform.staging.query.QueryBuilder;
import org.apache.sling.api.SlingException;
import org.apache.sling.api.resource.QuerySyntaxException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;

import static com.composum.pages.commons.PagesConstants.PN_CATEGORY;

/**
 *
 */
@Component(
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Component Manager"
        }
)
public class PagesComponentManager implements ComponentManager {

    protected static final Logger LOG = LoggerFactory.getLogger(PagesComponentManager.class);

    @Override
    public Collection<String> getComponentCategories(ResourceResolver resolver) {
        TreeSet<String> categories = new TreeSet<>();
        HashSet<String> componentPaths = new HashSet<>();
        QueryBuilder queryBuilder = resolver.adaptTo(QueryBuilder.class);
        if (queryBuilder != null) {
            for (String path : resolver.getSearchPath()) {
                Query query = queryBuilder.createQuery().path(path).type("cpp:Component");
                try {
                    for (Resource component : query.execute()) {
                        String type = component.getPath().substring(path.length());
                        if (!componentPaths.contains(type)) {
                            componentPaths.add(type);
                            ValueMap values = component.getValueMap();
                            categories.addAll(Arrays.asList(values.get(PN_CATEGORY, new String[0])));
                        }
                    }
                } catch (SlingException ex) {
                    LOG.error("On path {} : {}", path, ex.toString(), ex);
                }
            }
        }
        return categories;
    }
}
