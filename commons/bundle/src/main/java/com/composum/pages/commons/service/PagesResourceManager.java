package com.composum.pages.commons.service;

import com.composum.pages.commons.filter.TemplateFilter;
import com.composum.pages.commons.model.ContentDriven;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.filter.StringFilter;
import com.composum.sling.platform.staging.query.Query;
import com.composum.sling.platform.staging.query.QueryBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static com.composum.pages.commons.PagesConstants.PROP_TEMPLATE;
import static com.composum.pages.commons.PagesConstants.PROP_TEMPLATE_REF;
import static org.apache.jackrabbit.JcrConstants.JCR_NAME;

public abstract class PagesResourceManager<ModelType extends ContentDriven> implements ResourceManager<ModelType> {

    protected static final Logger LOG = LoggerFactory.getLogger(PagesResourceManager.class);

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

    protected Collection<ModelType> getModels(@Nonnull BeanContext context, @Nonnull String primaryType,
                                              @Nullable Resource searchRoot, @Nonnull ResourceFilter filter) {
        Collection<ModelType> result = new ArrayList<>();
        try {
            ResourceResolver resolver = context.getResolver();
            String queryRoot = searchRoot != null ? searchRoot.getPath() : "/";
            Query query = resolver.adaptTo(QueryBuilder.class).createQuery();
            query.path(queryRoot).type(primaryType).orderBy(JCR_NAME);
            Iterable<Resource> found = query.execute();
            for (Resource resource : found) {
                if (filter.accept(resource)) {
                    result.add(createBean(context, resource));
                }
            }
        } catch (RepositoryException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return result;
    }

    //
    // templates...
    //

    /**
     * @return 'true' if the resource of the model is a potential template
     */
    @Override
    public boolean isTemplate(@Nonnull ModelType model) {
        if (model.isValid()) {
            ResourceResolver resolver = model.getContext().getResolver();
            String tenant = null; // TODO tenant support
            String path = model.getPath();
            for (String root : resolver.getSearchPath()) {
                if (path.startsWith(StringUtils.isNotBlank(tenant) ? root + tenant : root)) {
                    return TemplateFilter.INSTANCE.accept(model.getResource());
                }
            }
        }
        return false;
    }

    public interface TemplateContext {

        ResourceResolver getResolver();

        String applyTemplatePlaceholders(@Nonnull Resource target, @Nonnull String value);
    }

    /**
     * only properties of a template accepted by this filter are copied (filter out template settings)
     */
    public static final StringFilter TEMPLATE_PROPERTY_FILTER = new StringFilter.BlackList(
            "^jcr:(primaryType|created.*|uuid)$",
            "^jcr:(baseVersion|predecessors|versionHistory|isCheckedOut)$",
            "^(allowed|forbidden)(Child|Parent)(Templates|Types)$",
            "^(allowed|forbidden)(Paths)$",
            "^isTemplate$",
            "^jcr:(title|description)$"
    );

    /**
     * properties of a target accepted by this filter are not replaced by values from a template
     */
    public static final StringFilter TEMPLATE_TARGET_KEEP = new StringFilter.WhiteList(
            "^jcr:(primaryType|created.*|uuid)$",
            "^jcr:(baseVersion|predecessors|versionHistory|isCheckedOut)$",
            "^jcr:(title|description)$"
    );

    /**
     * Creates a new resource as a copy of a template. If content nodes of such a template are referencing
     * other templates by a 'template' property the content of these referenced templates is copied
     * (used in site templates to reference the normal page templates of the site inside of a site template).
     *
     * @param context  the resolver to use for CRUD operations
     * @param parent   the parent resource for the new resource
     * @param name     the name of the new resource
     * @param template the template content resource
     * @return the resource created
     * @throws PersistenceException if an error is occurring
     */
    protected Resource createFromTemplate(TemplateContext context, Resource parent, String name, Resource template)
            throws PersistenceException {
        ResourceResolver resolver = context.getResolver();
        ValueMap templateValues = template.getValueMap();
        Resource target = resolver.create(parent, name, Collections.singletonMap(
                JcrConstants.JCR_PRIMARYTYPE, templateValues.get(JcrConstants.JCR_PRIMARYTYPE)));
        Resource templateContent = template.getChild(JcrConstants.JCR_CONTENT);
        Resource referencedTemplate = null;

        if (templateContent != null) {
            // create the 'jcr:content' child if the template contains such a child
            ValueMap contentValues = templateContent.getValueMap();
            String templateRef = contentValues.get(PROP_TEMPLATE_REF, "");

            if (StringUtils.isNotBlank(templateRef)) {
                // if the templates 'jcr:content' resource has a property 'template'
                // use the template referenced by this property instead if this template exists
                referencedTemplate = resolver.getResource(templateRef);
                if (referencedTemplate != null) {
                    Resource referencedContent = referencedTemplate.getChild(JcrConstants.JCR_CONTENT);
                    if (referencedContent != null) {
                        templateContent = referencedContent;
                        contentValues = templateContent.getValueMap();
                    }
                }
            }
            // create the 'jcr:content' child and mark this resource with the path of the used template
            String primaryContentType = (String) contentValues.get(JcrConstants.JCR_PRIMARYTYPE);
            Resource targetContent = resolver.create(target, JcrConstants.JCR_CONTENT, Collections.singletonMap(
                    JcrConstants.JCR_PRIMARYTYPE, (Object) primaryContentType));
            ModifiableValueMap targetValues = targetContent.adaptTo(ModifiableValueMap.class);
            applyContentTemplate(context, templateContent, targetContent, false);

            // prevent from unwanted properties in raw node types...
            if (!primaryContentType.startsWith("nt:")) {
                if (targetValues.get(PROP_TEMPLATE) == null) {
                    // write template only if not always set by the template properties
                    targetValues.put(PROP_TEMPLATE, templateContent.getParent().getPath());
                }
            }
        } else {
            applyTemplateProperties(context, template, target, false);
        }
        if (referencedTemplate != null) {
            // create a full structure copy of the referenced template
            for (Resource child : template.getChildren()) {
                String childName = child.getName();
                if (!JcrConstants.JCR_CONTENT.equals(childName)) {
                    createFromTemplate(context, target, childName, child);
                }
            }
        }
        // create a full structure copy of the template
        for (Resource child : template.getChildren()) {
            String childName = child.getName();
            // maybe the child is always created by the referenced template
            if (!JcrConstants.JCR_CONTENT.equals(childName) && target.getChild(childName) == null) {
                createFromTemplate(context, target, childName, child);
            }
        }
        return target;
    }

    /**
     * Applies the content resource data from a template content resource to a target content resource.
     *
     * @param context  the resolver to use for CRUD operations
     * @param template the template content resource
     * @param target   the target content resource
     * @param merge    if 'true' the target properties will be unmodified and all new aspects from the template will be added
     * @throws PersistenceException if an error is occurring
     */
    protected void applyContentTemplate(TemplateContext context, Resource template, Resource target, boolean merge)
            throws PersistenceException {
        ResourceResolver resolver = context.getResolver();
        if (!merge) {
            // in case of a 'reset' remove all children from the target resource
            for (Resource child : target.getChildren()) {
                resolver.delete(child);
            }
        }
        applyTemplateProperties(context, template, target, merge);
        for (Resource child : template.getChildren()) {
            // apply the template recursive...
            String name = child.getName();
            Resource targetChild = null;
            if (merge) {
                targetChild = target.getChild(name);
            }
            if (targetChild == null) {
                ValueMap childValues = child.getValueMap();
                targetChild = resolver.create(target, name, Collections.singletonMap(
                        JcrConstants.JCR_PRIMARYTYPE, childValues.get(JcrConstants.JCR_PRIMARYTYPE)));
            }
            applyContentTemplate(context, child, targetChild, merge);
        }
    }

    /**
     * Applies the resource properties from a template resource to a target resource.
     *
     * @param context  the resolver to use for CRUD operations
     * @param template the template content resource
     * @param target   the target content resource
     * @param merge    if 'true' the target properties will be unmodified and all new aspects from the template will be added
     * @throws PersistenceException if an error is occurring
     */
    protected void applyTemplateProperties(TemplateContext context, Resource template, Resource target, boolean merge)
            throws PersistenceException {
        ResourceResolver resolver = context.getResolver();
        ModifiableValueMap values = target.adaptTo(ModifiableValueMap.class);
        ValueMap templateValues = template.getValueMap();
        if (!merge) {
            // in case of a 'reset' remove all properties from the target resource
            for (String key : values.keySet().toArray(new String[0])) {
                if (!TEMPLATE_TARGET_KEEP.accept(key)) {
                    values.remove(key);
                }
            }
        }
        for (Map.Entry<String, Object> entry : templateValues.entrySet()) {
            String key = entry.getKey();
            if (TEMPLATE_PROPERTY_FILTER.accept(key)) {
                // copy template properties if not always present or a 'reset' is requested
                if (values.get(key) == null || (!merge && !TEMPLATE_TARGET_KEEP.accept(key))) {
                    Object value = entry.getValue();
                    if (value instanceof String) {
                        value = context.applyTemplatePlaceholders(target, (String) value);
                    }
                    if (value != null) {
                        values.put(key, value);
                    }
                }
            }
        }
    }
}
