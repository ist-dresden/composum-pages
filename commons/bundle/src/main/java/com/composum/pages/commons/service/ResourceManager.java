package com.composum.pages.commons.service;

import com.composum.pages.commons.model.AbstractModel;
import com.composum.sling.core.BeanContext;
import com.composum.sling.platform.staging.query.Query;
import com.composum.sling.platform.staging.query.QueryBuilder;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.Collection;

import static org.apache.jackrabbit.JcrConstants.JCR_NAME;

public abstract class ResourceManager<ModelType extends AbstractModel> {

    protected static final Logger LOG = LoggerFactory.getLogger(ResourceManager.class);

    public abstract ModelType createBean(BeanContext context, Resource resource);

    protected void checkExistence(ResourceResolver resolver, Resource parent, String name)
            throws RepositoryException {
        Resource resource = resolver.getResource(parent, name);
        if (resource != null) {
            throw new RepositoryException("instance exists already '" + parent.getPath() + "'/'" + name + "'");
        }
    }

    protected ModelType instanceCreated(BeanContext context, Resource resource) {
        ModelType bean = createBean(context, resource);
        if (LOG.isInfoEnabled()) {
            LOG.info("new {} created: '{}'", bean.getClass().getSimpleName(), bean.getPath());
        }
        return bean;
    }

    protected Collection<ModelType> getModels(@Nonnull BeanContext context, @Nonnull String primaryType,
                                              @Nullable Resource searchRoot) {
        Collection<ModelType> result = new ArrayList<>();
        try {
            ResourceResolver resolver = context.getResolver();
            String queryRoot = searchRoot != null ? searchRoot.getPath() : "/";
            Query query = resolver.adaptTo(QueryBuilder.class).createQuery();
            query.path(queryRoot).type(primaryType).orderBy(JCR_NAME);
            Iterable<Resource> found = query.execute();
            for (Resource pageRes : found) {
                result.add(createBean(context, pageRes));
            }
        } catch (RepositoryException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return result;
    }
}
