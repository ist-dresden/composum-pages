package com.composum.pages.commons.service;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.ElementTypeFilter;
import com.composum.pages.commons.model.ResourceReference;
import com.composum.pages.commons.util.ResolverUtil;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.filter.StringFilter;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.platform.staging.query.Query;
import com.composum.sling.platform.staging.query.QueryBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.composum.pages.commons.PagesConstants.PROP_ALLOWED_CONTAINERS;
import static com.composum.pages.commons.PagesConstants.PROP_ALLOWED_ELEMENTS;

@Component(
        label = "Composum Pages Edit Service",
        immediate = true,
        metatype = false
)
@Service
public class PagesEditService implements EditService {

    private static final Logger LOG = LoggerFactory.getLogger(PagesEditService.class);

    public static final String PROP_COLLECTION_NAME = "collection/name";
    public static final String PROP_COLLECTION_TYPE = "collection/resourceType";

    //
    // hierarchy management for the content tree
    //

    /**
     * Determines the list of resource types (nodes of type 'cpp:Component') which are accepted by the filter.
     *
     * @param resolver   the requests resolver (session)
     * @param containers the set of designated container references
     * @param filter     the filter instance (resource type pattern filter)
     * @return the result of a component type query filtered by the filter object
     */
    public List getAllowedContentTypes(ResourceResolver resolver,
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
    public ResourceReference.List filterTargetContainers(ResourceResolver resolver,
                                                         ResourceReference.List candidates,
                                                         ResourceReference element) {
        ResourceReference.List result = new ResourceReference.List();
        ElementTypeFilter filter = new ElementTypeFilter(resolver, candidates,
                PROP_ALLOWED_CONTAINERS, PROP_ALLOWED_ELEMENTS);
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
    public List getAllowedElementTypes(ResourceResolver resolver,
                                       ResourceReference.List containers,
                                       boolean resourceTypePath) {
        ElementTypeFilter filter = new ElementTypeFilter(resolver, containers,
                PROP_ALLOWED_CONTAINERS, PROP_ALLOWED_ELEMENTS);
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
     * Moves a resource and adopts all references to the moved resource or one of its children.
     *
     * @param resolver   the resolver (session context)
     * @param changeRoot the root element for reference search and change
     * @param source     the resource to move
     * @param target     the target (the parent resource) of the move
     * @param before     the designated sibling in an ordered target collection
     */
    public void moveComponent(ResourceResolver resolver, Resource changeRoot,
                              Resource source, ResourceReference target, Resource before)
            throws RepositoryException, PersistenceException {

        // use the containers collection (can be the target itself) to move the source into
        Resource collection = getContainerCollection(resolver, target);
        Session session = resolver.adaptTo(Session.class);

        String oldPath = source.getPath();
        int lastSlash = oldPath.lastIndexOf('/');
        String name = oldPath.substring(lastSlash + 1);
        String newName = name;
        String oldParentPath = oldPath.substring(0, lastSlash);
        String newParentPath = collection.getPath();
        String newPath = newParentPath + "/" + name;
        String siblingName = before != null ? before.getName() : null;

        if (LOG.isInfoEnabled()) {
            LOG.info("moveComponent(" + oldPath + " > " + newPath + " < " + siblingName + ")...");
        }

        if (!oldParentPath.equals(newParentPath)) {
            // check name collision before move into a new target collection
            for (int i = 1; resolver.getResource(newPath) != null; i++) {
                newPath = newParentPath + "/" + (newName = name + i);
            }
            session.move(oldPath, newPath);
            // adopt all references to the source and use the new target path
            changeReferences(ResourceFilter.ALL, StringFilter.ALL, changeRoot, oldPath, newPath);
        }

        if (StringUtils.isNotBlank(siblingName)) {
            // move to the designated position in the target collection
            session.refresh(true);
            Node parentNode = session.getNode(newParentPath);
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

    //
    //
    //

    /**
     * Changes the 'oldPath' references in each property of a tree to the 'newPath'.
     *
     * @param resourceFilter change all resources accepted by this filter, let all other resources unchanged
     * @param propertyFilter change only the properties with names matching to this property name filter
     * @param resource       the resource to change (recursive! - the root in the initial call)
     * @param oldPath        the old path of a moved resource
     * @param newPath        the new path of the resource
     */
    public void changeReferences(ResourceFilter resourceFilter, StringFilter propertyFilter,
                                 Resource resource, String oldPath, String newPath) {
        // check resource filter
        if (resourceFilter.accept(resource)) {

            ModifiableValueMap values = resource.adaptTo(ModifiableValueMap.class);
            for (Map.Entry<String, Object> entry : values.entrySet()) {

                String key = entry.getKey();
                // check property by name
                if (propertyFilter.accept(key)) {

                    Object value = entry.getValue();
                    if (value instanceof String) {
                        // change single string values (probably rich text)
                        String newValue = changeReferences((String) value, oldPath, newPath);
                        if (newValue != null) {
                            values.put(key, newValue);
                        }

                    } else if (value instanceof String[]) {
                        // change each value of a multi string (probably rich text)
                        boolean changed = false;
                        List<String> newList = new ArrayList<>();
                        for (String val : (String[]) value) {
                            String newValue = changeReferences(val, oldPath, newPath);
                            if (newValue != null) {
                                newList.add(newValue);
                                changed = true;
                            } else {
                                newList.add(val);
                            }
                        }
                        if (changed) {
                            // perform a change if one value is changed
                            values.put(key, newList.toArray());
                        }
                    }
                }
            }

            // recursive traversal
            for (Resource child : resource.getChildren()) {
                changeReferences(resourceFilter, propertyFilter, child, oldPath, newPath);
            }
        }
    }

    /**
     * Changes all references to 'oldPath' and use the 'newPath' in simple values or rich text strings.
     *
     * @param value   the string value (probably rich text)
     * @param oldPath the old path of the references to change
     * @param newPath the new path value
     * @return the changed value if changed otherwise 'null'
     */
    protected String changeReferences(String value, String oldPath, String newPath) {
        if (value.matches("^" + oldPath + "(/.*)?$")) {
            // simple path value...
            return newPath + value.substring(oldPath.length());
        } else {
            // check for HTML patterns and change all references if found
            Pattern htmlPattern = Pattern.compile("(href|src)=\"" + oldPath + "(/[^\"]*)?\"");
            Matcher matcher = htmlPattern.matcher(value);
            if (matcher.matches()) {
                return matcher.replaceAll("$1=\"" + newPath + "$2\"");
            }
        }
        return null;
    }

    /**
     * Changes the 'oldTypePattern' resource types in every appropriate component using the 'newTypeRule'.
     *
     * @param resourceFilter change all resources accepted by this filter, let all other resources unchanged
     * @param resource       the resource to change (recursive! - the root in the initial call)
     * @param oldTypePattern the resource type pattern to change
     * @param newTypeRule    the pattern matcher rule to build the new type
     */
    public void changeResourceType(ResourceFilter resourceFilter,
                                   Resource resource, String oldTypePattern, String newTypeRule) {
        changeResourceType(resourceFilter, resource, Pattern.compile(oldTypePattern), newTypeRule);
    }

    /**
     * Changes the 'oldTypePattern' resource types in every appropriate component using the 'newTypeRule'.
     *
     * @param resourceFilter change all resources accepted by this filter, let all other resources unchanged
     * @param resource       the resource to change (recursive! - the root in the initial call)
     * @param oldTypePattern the resource type pattern to change
     * @param newTypeRule    the pattern matcher rule to build the new type
     */
    public void changeResourceType(ResourceFilter resourceFilter,
                                   Resource resource, Pattern oldTypePattern, String newTypeRule) {
        // check resource filter
        if (resourceFilter.accept(resource)) {

            ModifiableValueMap values = resource.adaptTo(ModifiableValueMap.class);
            String resourceType = values.get(ResourceUtil.PROP_RESOURCE_TYPE, "");
            if (StringUtils.isNotBlank(resourceType)) {
                Matcher matcher = oldTypePattern.matcher(resourceType);
                if (matcher.matches()) {
                    String newResourceType = matcher.replaceAll(newTypeRule);
                    if (!resourceType.equals(newResourceType)) {
                        if (LOG.isInfoEnabled()) {
                            LOG.info("changeResourceType(" + resource.getPath() + "): "
                                    + resourceType + " -> " + newResourceType);
                        }
                        values.put(ResourceUtil.PROP_RESOURCE_TYPE, newResourceType);
                    }
                }
            }

            // recursive traversal
            for (Resource child : resource.getChildren()) {
                changeResourceType(resourceFilter, child, oldTypePattern, newTypeRule);
            }
        }
    }
}
