package com.composum.pages.commons.service;

import com.composum.pages.commons.model.ContentDriven;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.util.SlingResourceUtil;
import com.composum.sling.platform.staging.query.Query;
import com.composum.sling.platform.staging.query.QueryBuilder;
import org.apache.sling.api.SlingException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.RepositoryException;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import static org.apache.jackrabbit.JcrConstants.JCR_NAME;

public abstract class PagesContentManager<ModelType extends ContentDriven> implements ContentManager<ModelType> {

    protected static final Logger LOG = LoggerFactory.getLogger(PagesContentManager.class);

    public abstract ModelType createBean(BeanContext context, Resource resource);

    protected void checkExistence(ResourceResolver resolver, Resource parent, String name)
            throws RepositoryException {
        Resource resource = resolver.getResource(parent, name);
        if (resource != null) {
            throw new RepositoryException("instance exists already '" + parent.getPath() + "'/'" + name + "'");
        }
    }

    @Nonnull
    protected ModelType instanceCreated(BeanContext context, Resource resource) {
        ModelType bean = createBean(context, resource);
        if (LOG.isInfoEnabled()) {
            LOG.info("new {} created: '{}'", bean.getClass().getSimpleName(), bean.getPath());
        }
        return bean;
    }

    protected Set<ModelType> getModels(@Nonnull BeanContext context, @Nonnull String primaryType,
                                       @Nullable Resource searchRoot, @Nonnull ResourceFilter filter) {
        Set<ModelType> result = new LinkedHashSet<>();
        try {
            ResourceResolver resolver = context.getResolver();
            String queryRoot = searchRoot != null ? searchRoot.getPath() : "/content";
            Query query = Objects.requireNonNull(resolver.adaptTo(QueryBuilder.class)).createQuery();
            query.path(queryRoot).type(primaryType).orderBy(JCR_NAME);
            Iterable<Resource> found = query.execute();
            for (Resource resource : found) {
                if (filter.accept(resource)) {
                    result.add(createBean(context, resource));
                }
            }
        } catch (SlingException ex) {
            LOG.error("On path {} : {}", SlingResourceUtil.getPath(searchRoot), ex.toString(), ex);
        }
        return result;
    }
}
