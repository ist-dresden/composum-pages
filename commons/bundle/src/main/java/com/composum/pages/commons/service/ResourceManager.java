package com.composum.pages.commons.service;

import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.filter.StringFilter;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.RepositoryException;
import java.util.List;

public interface ResourceManager {

    /**
     * Checks the policies of the resource hierarchy for a given parent and child (for move and copy operations).
     *
     * @param resolver the resolver of the current request
     * @param parent   the designated parent resource
     * @param child    the resource to check
     * @return 'true' if the child could be a child of the given parent
     */
    boolean isAllowedChild(@Nonnull ResourceResolver resolver, @Nonnull Resource parent, @Nonnull Resource child);

    /**
     * Moves a resource and adopts all references to the moved resource or one of its children.
     *
     * @param resolver     the resolver (session context)
     * @param changeRoot   the root element for reference search and change
     * @param source       the resource to move
     * @param targetParent the target (the parent resource) of the move
     * @param before       the designated sibling in an ordered target collection
     * @return the new resource at the target path
     */
    @Nonnull
    Resource moveContentResource(@Nonnull ResourceResolver resolver, @Nonnull Resource changeRoot,
                                 @Nonnull Resource source, @Nonnull Resource targetParent,
                                 @Nullable Resource before)
            throws RepositoryException;

    /**
     * Changes the 'oldPath' references in each property of a tree to the 'newPath'.
     *
     * @param resourceFilter change all resources accepted by this filter, let all other resources unchanged
     * @param propertyFilter change only the properties with names matching to this property name filter
     * @param resource       the resource to change (recursive! - the root in the initial call)
     * @param foundReferrers the List of referers found (to fill during traversal, empty in the initial call)
     * @param scanOnly       if 'true' no changes are made but the referer list is filled
     * @param oldPath        the old path of a moved resource
     * @param newPath        the new path of the resource
     */
    void changeReferences(@Nonnull ResourceFilter resourceFilter, @Nonnull StringFilter propertyFilter,
                          @Nonnull Resource resource, @Nonnull List<Resource> foundReferrers, boolean scanOnly,
                          @Nonnull String oldPath, @Nonnull String newPath);

    /**
     * Changes the 'oldTypePattern' resource types in every appropriate component using the 'newTypeRule'.
     *
     * @param resourceFilter change all resources accepted by this filter, let all other resources unchanged
     * @param resource       the resource to change (recursive! - the root in the initial call)
     * @param oldTypePattern the resource type pattern to change
     * @param newTypeRule    the pattern matcher rule to build the new type
     */
    void changeResourceType(@Nonnull ResourceFilter resourceFilter, @Nonnull Resource resource,
                            @Nonnull String oldTypePattern, @Nonnull String newTypeRule);

    //
    // templates and copies
    //

    interface TemplateContext {

        ResourceResolver getResolver();

        String applyTemplatePlaceholders(@Nonnull Resource target, @Nonnull String value);
    }

    /**
     * only properties of a template accepted by this filter are copied (filter out template settings)
     */
    StringFilter TEMPLATE_PROPERTY_FILTER = new StringFilter.BlackList(
            "^jcr:(primaryType|created.*|uuid)$",
            "^jcr:(baseVersion|predecessors|versionHistory|isCheckedOut)$",
            "^(allowed|forbidden)(Child|Parent)(Elements|Containers|Templates|Types)$",
            "^(allowed|forbidden)(Paths)$",
            "^isTemplate$",
            "^jcr:(title|description)$"
    );

    /**
     * properties of a target accepted by this filter are not replaced by values from a template
     */
    StringFilter TEMPLATE_TARGET_KEEP = new StringFilter.WhiteList(
            "^jcr:(primaryType|created.*|uuid)$",
            "^jcr:(baseVersion|predecessors|versionHistory|isCheckedOut)$",
            "^jcr:(title|description)$"
    );

    /**
     * Creates a new resource as a copy of another resource.
     *
     * @param resolver the resolver to use for CRUD operations
     * @param parent   the parent resource for the new resource
     * @param name     the name of the new resource
     * @param template the template content resource
     * @return the resource created
     * @throws PersistenceException if an error is occurring
     */
    Resource copyContentResource(@Nonnull ResourceResolver resolver, @Nonnull Resource parent,
                                 @Nonnull String name, @Nonnull Resource template)
            throws PersistenceException;

    /**
     * @return 'true' if the resource is a content template
     */
    boolean isTemplate(@Nonnull Resource resource);

    /**
     * Creates a new resource as a copy of a template. If content nodes of such a template are referencing
     * other templates by a 'template' property the content of these referenced templates is copied
     * (used in site templates to reference the normal page templates of the site inside of a site template).
     *
     * @param context             the resolver to use for CRUD operations
     * @param parent              the parent resource for the new resource
     * @param name                the name of the new resource
     * @param template            the template content resource
     * @param setTemplateProperty if 'true' the 'template' property is filled with the template path
     * @return the resource created
     * @throws PersistenceException if an error is occurring
     */
    Resource createFromTemplate(@Nonnull TemplateContext context, @Nonnull Resource parent, @Nonnull String name,
                                @Nonnull Resource template, boolean setTemplateProperty)
            throws PersistenceException;
}
