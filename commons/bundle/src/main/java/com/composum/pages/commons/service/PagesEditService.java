package com.composum.pages.commons.service;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.ElementTypeFilter;
import com.composum.pages.commons.util.ResolverUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.platform.staging.query.Query;
import com.composum.sling.platform.staging.query.QueryBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component(
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Elements Manager"
        }
)
public class PagesEditService implements EditService {

    private static final Logger LOG = LoggerFactory.getLogger(PagesEditService.class);

    public static final String PROP_COLLECTION_NAME = "collection/name";
    public static final String PROP_COLLECTION_TYPE = "collection/resourceType";

    public static final Pattern NAME_PATTERN = Pattern.compile("^(.*[^\\d])(\\d+)$");

    @Reference
    protected ResourceManager resourceManager;

    @Reference
    protected PageManager pageManager;

    //
    // hierarchy management for the page content
    //

    /**
     * @return 'true' if the element can be a child of the container
     */
    @Override
    public boolean isAllowedElement(@Nonnull ResourceResolver resolver,
                                    @Nonnull ResourceManager.ResourceReference container,
                                    @Nonnull ResourceManager.ResourceReference element) {
        ResourceManager.ReferenceList containers = resourceManager.getReferenceList(container);
        ElementTypeFilter filter = new ElementTypeFilter(resolver, containers);
        return filter.isAllowedElement(element);
    }

    /**
     * Determines the list of potential target containers for a page content element.
     *
     * @param resolver   the requests resolver (session)
     * @param candidates the list of container candidates determined int the context of a hole page
     * @param element    the element which should be inserted in an(other) container
     * @return the list of target containers (not null, can be empty)
     */
    @Override
    public ResourceManager.ReferenceList filterTargetContainers(ResourceResolver resolver,
                                                                ResourceManager.ReferenceList candidates,
                                                                ResourceManager.ResourceReference element) {
        ResourceManager.ReferenceList result = resourceManager.getReferenceList();
        ElementTypeFilter filter = new ElementTypeFilter(resolver, candidates);
        for (ResourceManager.ResourceReference candidate : candidates) {
            if (filter.isAllowedElement(element, candidate)) {
                result.add(candidate);
            }
        }
        return result;
    }

    /**
     * Determines the list of resource types (nodes of type 'cpp:Component') for any of the containers.
     *
     * @param resolver   the requests resolver (session)
     * @param containers the set of designated container references
     * @return the result of a component type query filtered by the filter object
     */
    @Override
    public List<String> getAllowedElementTypes(ResourceResolver resolver,
                                               ResourceManager.ReferenceList containers,
                                               boolean resourceTypePath) {
        ElementTypeFilter filter = new ElementTypeFilter(resolver, containers);
        return getAllowedElementTypes(resolver, containers, filter, resourceTypePath);
    }

    /**
     * Determines the list of resource types (nodes of type 'cpp:Component') which are accepted by the filter.
     *
     * @param resolver   the requests resolver (session)
     * @param containers the set of designated container references
     * @param filter     the filter instance (resource type pattern filter)
     * @return the result of a component type query filtered by the filter object
     */
    @Override
    public List<String> getAllowedElementTypes(ResourceResolver resolver,
                                               ResourceManager.ReferenceList containers,
                                               ElementTypeFilter filter,
                                               boolean resourceTypePath) {
        List<String> allowedTypes = new ArrayList<>();
        QueryBuilder queryBuilder = resolver.adaptTo(QueryBuilder.class);
        if (queryBuilder != null) {
            for (String path : resolver.getSearchPath()) {
                Query query = queryBuilder.createQuery().path(path).type("cpp:Component");
                try {
                    for (Resource component : query.execute()) {
                        String type = component.getPath().substring(path.length());
                        if (!allowedTypes.contains(type) && filter.isAllowedType(type)) {
                            allowedTypes.add(resourceTypePath ? path + type : type);
                        }
                    }
                } catch (RepositoryException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
        }
        return allowedTypes;
    }

    /**
     * get or create referenced resource
     */
    @Override
    public Resource getReferencedResource(ResourceResolver resolver, ResourceManager.ResourceReference reference)
            throws PersistenceException {
        Resource resource = resolver.resolve(reference.getPath());
        if (ResourceUtil.isNonExistingResource(resource)) {
            Resource parent = resource.getParent();
            if (parent != null) {
                String resourceType = reference.getType();
                String primaryType = ResolverUtil.getTypeProperty(resolver, resourceType,
                        PagesConstants.PROP_COMPONENT_TYPE, "");
                Map<String, Object> properties = new HashMap<>();
                properties.put(JcrConstants.JCR_PRIMARYTYPE, StringUtils.isNotBlank(primaryType)
                        ? primaryType : JcrConstants.NT_UNSTRUCTURED);
                properties.put(ResourceUtil.PROP_RESOURCE_TYPE, resourceType);
                resource = resolver.create(parent, resource.getName(), properties);
            }
        }
        return resource;
    }

    /**
     * Inserts a new resource.
     *
     * @param resolver     the resolver (session context)
     * @param resourceType the type of the new resource
     * @param target       the target (the parent resource) of the move
     * @param before       the designated sibling in an ordered target collection
     */
    @Override
    public Resource insertElement(ResourceResolver resolver, String resourceType,
                                  ResourceManager.ResourceReference target, Resource before)
            throws RepositoryException, PersistenceException {

        BeanContext context = new BeanContext.Service(resolver);

        // use the containers collection (can be the target itself) to move the source into
        Resource collection = getContainerCollection(resolver, target);

        int lastSlash = resourceType.lastIndexOf('/');
        String name = resourceType.substring(lastSlash + 1);
        String siblingName = before != null ? before.getName() : null;

        if (LOG.isInfoEnabled()) {
            LOG.info("insertElement(" + resourceType + " > " + collection.getPath() + " < " + siblingName + ")...");
        }

        String newName = checkNameCollision(collection, name);

        // determine the primary type for the designated resource type
        PagesConstants.ComponentType componentType = PagesConstants.ComponentType.typeOf(resolver, null, resourceType);
        String primaryType = PagesConstants.ComponentType.getPrimaryType(componentType);

        Map<String, Object> properties = new HashMap<>();
        properties.put(JcrConstants.JCR_PRIMARYTYPE, primaryType);
        properties.put(ResourceUtil.PROP_RESOURCE_TYPE, resourceType);
        Resource result = resolver.create(collection, newName, properties);
        pageManager.touch(context, collection, null, false);

        Session session = resolver.adaptTo(Session.class);
        if (session != null && StringUtils.isNotBlank(siblingName)) {
            // move to the designated position in the target collection
            session.refresh(true);
            Node parentNode = session.getNode(collection.getPath());
            parentNode.orderBefore(newName, siblingName);
        }
        return result;
    }

    /**
     * Determine a container element collection resource (can be the container itself).
     */
    @Nonnull
    protected Resource getContainerCollection(ResourceResolver resolver, ResourceManager.ResourceReference target)
            throws RepositoryException, PersistenceException {

        // get or create the target (the parent)
        Resource targetResource = getReferencedResource(resolver, target);

        // check the configuration for an embedded collection node to use
        Resource collection = targetResource;
        String collectionName = ResolverUtil.getTypeProperty(
                targetResource, target.getType(), PROP_COLLECTION_NAME, "");

        if (StringUtils.isNotBlank(collectionName)) {
            // prepare the collection id such an ebendded resource is configured
            collection = targetResource.getChild(collectionName);
            if (collection == null) {
                String collectionType = ResolverUtil.getTypeProperty(
                        targetResource, target.getType(), PROP_COLLECTION_TYPE, "");
                if (StringUtils.isNotBlank(collectionType)) {
                    String collectionPath = targetResource.getPath() + "/" + collectionName;
                    collection = getReferencedResource(resolver,
                            resourceManager.getReference(resolver, collectionPath, collectionType));
                }
            }
        }

        if (collection == null || ResourceUtil.isNonExistingResource(collection)) {
            throw new RepositoryException(
                    "container collection not found: " + target.getPath() + " / " + collectionName);
        }

        return collection;
    }

    /**
     * Moves a resource and adopts all references to the moved resource or one of its children.
     *
     * @param resolver     the resolver (session context)
     * @param changeRoot   the root element for reference search and change
     * @param source       the resource to move
     * @param targetParent the target (a reference to the parent resource) of the move
     * @param before       the designated sibling in an ordered target collection
     * @return the new resource at the target path
     */
    @Override
    public Resource moveElement(ResourceResolver resolver, Resource changeRoot,
                                Resource source, ResourceManager.ResourceReference targetParent, Resource before)
            throws RepositoryException, PersistenceException {
        Resource result = null;

        BeanContext context = new BeanContext.Service(resolver);
        Resource currentParent = source.getParent();
        if (currentParent != null) {

            // use the containers collection (can be the target itself) to move the source into
            Resource collection = getContainerCollection(resolver, targetParent);
            boolean isAnotherParent = !collection.getPath().equals(currentParent.getPath());

            String newName = source.getName();
            if (isAnotherParent) {
                pageManager.touch(context, currentParent, null, false);
                newName = checkNameCollision(collection, newName);
            }

            result = resourceManager.moveContentResource(resolver, changeRoot, source, collection,
                    isAnotherParent ? newName : null, before);
            pageManager.touch(context, collection, null, false);
        }
        return result;
    }

    /**
     * Copies an element.
     *
     * @param resolver     the resolver (session context)
     * @param source       the resource to move
     * @param targetParent the target (a reference to the parent resource) of the move
     * @param before       the designated sibling in an ordered target collection
     * @return the new resource at the target path
     */
    @Override
    public Resource copyElement(ResourceResolver resolver,
                                Resource source, ResourceManager.ResourceReference targetParent, Resource before)
            throws RepositoryException, PersistenceException {

        BeanContext context = new BeanContext.Service(resolver);

        // use the containers collection (can be the target itself) to move the source into
        Resource collection = getContainerCollection(resolver, targetParent);
        String newName = checkNameCollision(collection, source.getName());
        Resource result = resourceManager.copyContentResource(resolver, source, collection, newName, before);
        pageManager.touch(context, collection, null, false);
        return result;
    }

    /**
     * check name collision before insert into a new target collection
     */
    protected String checkNameCollision(Resource collection, String name) {
        Matcher matcher = NAME_PATTERN.matcher(name);
        String base = name;
        int number = 0;
        if (matcher.matches()) {
            base = matcher.group(1);
            number = Integer.parseInt(matcher.group(2));
        }
        String newName = name;
        while (collection.getChild(newName) != null) {
            newName = base + (++number);
        }
        return newName;
    }
}
