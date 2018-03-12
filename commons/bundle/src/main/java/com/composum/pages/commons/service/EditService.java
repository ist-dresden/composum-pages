package com.composum.pages.commons.service;

import com.composum.pages.commons.model.ElementTypeFilter;
import com.composum.pages.commons.model.ResourceReference;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.filter.StringFilter;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.RepositoryException;

/**
 * the service interface for Pages editing operations
 */
public interface EditService {

    /**
     * Determines the list of potential target containers for a page content element.
     */
    ResourceReference.List filterTargetContainers(ResourceResolver resolver,
                                                  ResourceReference.List candidates,
                                                  ResourceReference element);

    /**
     * Determines the list of resource types (nodes of type 'cpp:Component') for any of the containers.
     *
     * @param resolver   the requests resolver (session)
     * @param containers the set of designated container references
     * @return the result of a component type query filtered by the filter object
     */
    java.util.List getAllowedElementTypes(ResourceResolver resolver,
                                          ResourceReference.List containers,
                                          boolean resourceTypePath);

    /**
     * Determines the list of resource types (nodes of type 'cpp:Component') which are accepted by the filter.
     *
     * @param resolver   the requests resolver (session)
     * @param containers the set of designated container references
     * @param filter     the filter instance (resource type pattern filter)
     * @return the result of a component type query filtered by the filter object
     */
    java.util.List getAllowedElementTypes(ResourceResolver resolver,
                                          ResourceReference.List containers,
                                          ElementTypeFilter filter,
                                          boolean resourceTypePath);

    /**
     * Returns or creates and returns the resource addressed by a reference.
     *
     * @param resolver  the resolver to use for resource resolving and creation
     * @param reference the path and type of the resource
     * @return the resource instance
     */
    Resource getReferencedResource(ResourceResolver resolver, ResourceReference reference)
            throws PersistenceException;

    /**
     * Inserts a new resource.
     *
     * @param resolver     the resolver (session context)
     * @param resourceType the type of the new resource
     * @param target       the target (the parent resource) of the move
     * @param before       the designated sibling in an ordered target collection
     */
    void insertComponent(ResourceResolver resolver, String resourceType,
                         ResourceReference target, Resource before)
            throws RepositoryException, PersistenceException;

    /**
     * Moves a resource and adopts all references to the moved resource or one of its children.
     *
     * @param resolver   the resolver (session context)
     * @param changeRoot the root element for reference search and change
     * @param source     the resource to move
     * @param target     the target (the parent resource) of the move
     * @param before     the designated sibling in an ordered target collection
     */
    void moveComponent(ResourceResolver resolver, Resource changeRoot,
                       Resource source, ResourceReference target, Resource before)
            throws RepositoryException, PersistenceException;

    /**
     * Changes the 'oldPath' references in each property of a tree to the 'newPath'.
     *
     * @param resourceFilter change all resources accepted by this filter, let all other resources unchanged
     * @param propertyFilter change only the properties with names matching to this property name filter
     * @param resource       the resource to change (recursive! - the root in the initial call)
     * @param oldPath        the old path of a moved resource
     * @param newPath        the new path of the resource
     */
    void changeReferences(ResourceFilter resourceFilter, StringFilter propertyFilter,
                          Resource resource, String oldPath, String newPath);

    /**
     * Changes the 'oldTypePattern' resource types in every appropriate component using the 'newTypeRule'.
     *
     * @param resourceFilter change all resources accepted by this filter, let all other resources unchanged
     * @param resource       the resource to change (recursive! - the root in the initial call)
     * @param oldTypePattern the resource type pattern to change
     * @param newTypeRule    the pattern matcher rule to build the new type
     */
    void changeResourceType(ResourceFilter resourceFilter,
                            Resource resource, String oldTypePattern, String newTypeRule);
}
