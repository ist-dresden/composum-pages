package com.composum.pages.commons.service;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.ElementTypeFilter;
import com.composum.pages.commons.model.ResourceReference;
import com.composum.pages.commons.util.ResolverUtil;
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

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Elements Manager"
        }
)
public class PagesEditService implements EditService {

    private static final Logger LOG = LoggerFactory.getLogger(PagesEditService.class);

    public static final String PROP_COLLECTION_NAME = "collection/name";
    public static final String PROP_COLLECTION_TYPE = "collection/resourceType";

    @Reference
    protected ResourceManager resourceManager;

    //
    // hierarchy management for the page content
    //

    /**
     * Determines the list of potential target containers for a page content element.
     *
     * @param resolver   the requests resolver (session)
     * @param candidates the list of container candidates determined int the context of a hole page
     * @param element    the element which should be inserted in an(other) container
     * @return the list of target containers (not null, can be empty)
     */
    @Override
    public ResourceReference.List filterTargetContainers(ResourceResolver resolver,
                                                         ResourceReference.List candidates,
                                                         ResourceReference element) {
        ResourceReference.List result = new ResourceReference.List();
        ElementTypeFilter filter = new ElementTypeFilter(resolver, candidates);
        for (ResourceReference candidate : candidates) {
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
    public List getAllowedElementTypes(ResourceResolver resolver,
                                       ResourceReference.List containers,
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
    public List getAllowedElementTypes(ResourceResolver resolver,
                                       ResourceReference.List containers,
                                       ElementTypeFilter filter,
                                       boolean resourceTypePath) {
        List<String> allowedTypes = new ArrayList<>();
        QueryBuilder queryBuilder = resolver.adaptTo(QueryBuilder.class);
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
        return allowedTypes;
    }

    @Override
    public Resource getReferencedResource(ResourceResolver resolver, ResourceReference reference)
            throws PersistenceException {
        Resource resource = resolver.resolve(reference.getPath());
        if (ResourceUtil.isNonExistingResource(resource)) {
            Map<String, Object> properties = new HashMap<>();
            properties.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED);
            properties.put(ResourceUtil.PROP_RESOURCE_TYPE, reference.getType());
            resource = resolver.create(resource.getParent(), resource.getName(), properties);
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
    public void insertComponent(ResourceResolver resolver, String resourceType,
                                ResourceReference target, Resource before)
            throws RepositoryException, PersistenceException {

        // use the containers collection (can be the target itself) to move the source into
        Resource collection = getContainerCollection(resolver, target);
        Session session = resolver.adaptTo(Session.class);

        int lastSlash = resourceType.lastIndexOf('/');
        String name = resourceType.substring(lastSlash + 1);
        String siblingName = before != null ? before.getName() : null;

        if (LOG.isInfoEnabled()) {
            LOG.info("insertComponent(" + resourceType + " > " + collection.getPath() + " < " + siblingName + ")...");
        }

        // check name collision before move into a new target collection
        String newName = name;
        for (int i = 1; collection.getChild(newName) != null; i++) {
            newName = name + '_' + i;
        }

        // determine the primary type for the designated resource type
        PagesConstants.ComponentType componentType = PagesConstants.ComponentType.typeOf(resolver, null, resourceType);
        String primaryType = PagesConstants.ComponentType.getPrimaryType(componentType);

        Map<String, Object> properties = new HashMap<>();
        properties.put(JcrConstants.JCR_PRIMARYTYPE, primaryType);
        properties.put(ResourceUtil.PROP_RESOURCE_TYPE, resourceType);
        resolver.create(collection, newName, properties);

        if (StringUtils.isNotBlank(siblingName)) {
            // move to the designated position in the target collection
            session.refresh(true);
            Node parentNode = session.getNode(collection.getPath());
            parentNode.orderBefore(newName, siblingName);
        }
    }

    /**
     * Determine a container element collection resource (can be the container itself).
     */
    protected Resource getContainerCollection(ResourceResolver resolver, ResourceReference target)
            throws RepositoryException, PersistenceException {

        // get or creae the target (the parent)
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
                            new ResourceReference(resolver, collectionPath, collectionType));
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
    public Resource moveComponent(ResourceResolver resolver, Resource changeRoot,
                                  Resource source, ResourceReference targetParent, Resource before)
            throws RepositoryException, PersistenceException {

        // use the containers collection (can be the target itself) to move the source into
        Resource collection = getContainerCollection(resolver, targetParent);
        return resourceManager.moveContentResource(resolver, changeRoot, source, collection, null, before);
    }
}
