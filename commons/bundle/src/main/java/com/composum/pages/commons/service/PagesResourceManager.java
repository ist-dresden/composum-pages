package com.composum.pages.commons.service;

import com.composum.pages.commons.filter.TemplateFilter;
import com.composum.pages.commons.model.ContentTypeFilter;
import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.ResourceReference;
import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.model.Template;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.filter.StringFilter;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.composum.pages.commons.PagesConstants.PROP_TEMPLATE;
import static com.composum.pages.commons.PagesConstants.PROP_TEMPLATE_REF;

@Component(
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Resource Manager"
        }
)
public class PagesResourceManager implements ResourceManager {

    protected static final Logger LOG = LoggerFactory.getLogger(PagesResourceManager.class);

    /**
     * Checks the policies of the resource hierarchy for a given parent and child (for move and copy operations).
     *
     * @param resolver the resolver of the current request
     * @param parent   the designated parent resource
     * @param child    the resource to check
     * @return 'true' if the child could be a child of the given parent
     */
    @Override
    public boolean isAllowedChild(@Nonnull ResourceResolver resolver, @Nonnull Resource parent, @Nonnull Resource child) {
        String referencePath = parent.getPath() + "/" + child.getName();
        Resource childTypeResource = child;
        if (Page.isPage(child) || Site.isSite(child)) {
            childTypeResource = child.getChild(JcrConstants.JCR_CONTENT);
        }
        String referenceType = childTypeResource.getResourceType();
        ContentTypeFilter filter = new ContentTypeFilter(parent);
        return filter.isAllowedChild(Template.getTemplateOf(child),
                new ResourceReference(resolver, referencePath, referenceType));
    }

    /**
     * Moves a resource and adopts all references to the moved resource or one of its children.
     *
     * @param resolver     the resolver (session context)
     * @param changeRoot   the root element for reference search and change
     * @param source       the resource to move
     * @param targetParent the target (the parent resource) of the move
     * @param newName      an optional new name for the resource
     * @param before       the designated sibling in an ordered target collection
     * @return the new resource at the target path
     */
    @Override
    @Nonnull
    public Resource moveContentResource(@Nonnull ResourceResolver resolver, @Nonnull Resource changeRoot,
                                        @Nonnull Resource source, @Nonnull Resource targetParent,
                                        @Nullable String newName, @Nullable Resource before)
            throws RepositoryException {

        Session session = resolver.adaptTo(Session.class);

        String oldPath = source.getPath();
        int lastSlash = oldPath.lastIndexOf('/');
        String oldParentPath = lastSlash == 0 ? "/" : oldPath.substring(0, lastSlash);
        String newParentPath = targetParent.getPath();
        String name = oldPath.substring(lastSlash + 1);
        boolean isAnotherParent = !oldParentPath.equals(newParentPath);

        // determine the name of the next sibling in the target resource (ordering)
        String siblingName = before != null ? before.getName() : null;
        if (StringUtils.isNotBlank(newName) && !isAnotherParent) {
            // preserve ordering on a simple rename
            Iterator<Resource> children = targetParent.listChildren();
            while (children.hasNext()) {
                if (children.next().getPath().equals(oldPath)) {
                    if (children.hasNext()) {
                        siblingName = children.next().getName();
                    }
                    break;
                }
            }
        }

        if (StringUtils.isBlank(newName)) {
            newName = name;
        }
        String newPath = newParentPath + "/" + newName;

        // check name collision before move into a new target resource; on the same parent let it fail
        if (isAnotherParent) {
            String n = newName;
            for (int i = 1; resolver.getResource(newPath) != null; i++) {
                newPath = newParentPath + "/" + (newName = n + i);
            }
        }

        if (LOG.isInfoEnabled()) {
            LOG.info("moveContentResource(" + oldPath + " > " + newPath + " < " + siblingName + ")...");
        }

        // move it if it is a real move and adjust all references
        if (isAnotherParent || !newName.equals(name)) {
            session.move(oldPath, newPath);
            ArrayList<Resource> foundReferers = new ArrayList<>();
            // adopt all references to the source and use the new target path
            changeReferences(ResourceFilter.ALL, StringFilter.ALL, changeRoot, foundReferers, false, oldPath, newPath);
        }

        // move to the designated position in the target collection
        try {
            session.refresh(true);
            Node parentNode = session.getNode(newParentPath);
            parentNode.orderBefore(newName, siblingName);
        } catch (UnsupportedRepositoryOperationException ignore) {
            // ordering not supported - ignore it
        }

        return resolver.getResource(newPath);
    }

    //
    // adjust referenced (paths) on move operations
    //

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
    @Override
    public void changeReferences(@Nonnull ResourceFilter resourceFilter, @Nonnull StringFilter propertyFilter,
                                 @Nonnull Resource resource, @Nonnull List<Resource> foundReferrers, boolean scanOnly,
                                 @Nonnull String oldPath, @Nonnull String newPath) {
        // check resource filter
        if (resourceFilter.accept(resource)) {
            boolean resourceChanged = false;

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
                            if (!scanOnly) {
                                values.put(key, newValue);
                            }
                            resourceChanged = true;
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
                                if (scanOnly) {
                                    break;
                                }
                            } else {
                                newList.add(val);
                            }
                        }
                        if (changed) {
                            // perform a change if one value is changed
                            if (!scanOnly) {
                                values.put(key, newList.toArray());
                            }
                            resourceChanged = true;
                        }
                    }
                }

                if (resourceChanged && scanOnly) {
                    break;
                }
            }
            if (resourceChanged) {
                foundReferrers.add(resource);
            }

            // recursive traversal
            for (Resource child : resource.getChildren()) {
                changeReferences(resourceFilter, propertyFilter, child, foundReferrers, scanOnly, oldPath, newPath);
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
    @Override
    public void changeResourceType(@Nonnull ResourceFilter resourceFilter, @Nonnull Resource resource,
                                   @Nonnull String oldTypePattern, @Nonnull String newTypeRule) {
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
    public void changeResourceType(@Nonnull ResourceFilter resourceFilter, @Nonnull Resource resource,
                                   @Nonnull Pattern oldTypePattern, @Nonnull String newTypeRule) {
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

    //
    // templates and copies
    //

    /**
     * the 'transform nothing' context used in case of a copy operation
     */
    public static class NopTemplateContext implements TemplateContext {

        protected final ResourceResolver resolver;

        public NopTemplateContext(ResourceResolver resolver) {
            this.resolver = resolver;
        }

        @Override
        public ResourceResolver getResolver() {
            return resolver;
        }

        @Override
        public String applyTemplatePlaceholders(@Nonnull Resource target, @Nonnull String value) {
            return value;
        }
    }

    /**
     * Creates a new resource as a copy of another resource.
     *
     * @param resolver the resolver to use for CRUD operations
     * @param template the template content resource
     * @param parent   the parent resource for the new resource
     * @param name     the name of the new resource
     * @param before   the designated sibling in an ordered target collection
     * @return the resource created
     * @throws PersistenceException if an error is occurring
     */
    @Override
    public Resource copyContentResource(@Nonnull ResourceResolver resolver, @Nonnull Resource template,
                                        @Nonnull Resource parent, @Nonnull String name, @Nullable Resource before)
            throws PersistenceException {
        TemplateContext templateContext = new NopTemplateContext(resolver);
        ValueMap templateValues = template.getValueMap();
        Resource target = resolver.create(parent, name, Collections.singletonMap(
                JcrConstants.JCR_PRIMARYTYPE, templateValues.get(JcrConstants.JCR_PRIMARYTYPE)));
        Resource templateContent = template.getChild(JcrConstants.JCR_CONTENT);

        if (templateContent != null) {
            // create the 'jcr:content' child if the template contains such a child
            ValueMap contentValues = templateContent.getValueMap();
            String primaryContentType = (String) contentValues.get(JcrConstants.JCR_PRIMARYTYPE);
            Resource targetContent = resolver.create(target, JcrConstants.JCR_CONTENT, Collections.singletonMap(
                    JcrConstants.JCR_PRIMARYTYPE, (Object) primaryContentType));
            ModifiableValueMap targetValues = targetContent.adaptTo(ModifiableValueMap.class);
            applyContentTemplate(templateContext, templateContent, targetContent, false, false);
        } else {
            applyTemplateProperties(templateContext, template, target, false);
        }
        // create a full structure copy of the template
        for (Resource child : template.getChildren()) {
            String childName = child.getName();
            // maybe the child is always created by the referenced template
            if (!JcrConstants.JCR_CONTENT.equals(childName) && target.getChild(childName) == null) {
                copyContentResource(resolver, child, target, childName, null);
            }
        }
        return target;
    }

    /**
     * @return 'true' if the resource is a content template
     */
    @Override
    public boolean isTemplate(@Nonnull Resource resource) {
        ResourceResolver resolver = resource.getResourceResolver();
        String tenant = null; // TODO tenant support
        String path = resource.getPath();
        for (String root : resolver.getSearchPath()) {
            if (path.startsWith(StringUtils.isNotBlank(tenant) ? root + tenant : root)) {
                return TemplateFilter.INSTANCE.accept(resource);
            }
        }
        return false;
    }

    /**
     * the node filter to prevent from copying template rules to content targets
     */
    public static class TemplateCopyFilter implements ResourceFilter {

        @Override
        public boolean accept(Resource resource) {
            String name = resource.getName();
            return !"cpp:design".equals(name);
        }

        @Override
        public boolean isRestriction() {
            return true;
        }

        @Override
        public void toString(StringBuilder builder) {
            builder.append(getClass().getSimpleName());
        }
    }

    public static final ResourceFilter TEMPLATE_COPY_FILTER = new TemplateCopyFilter();

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
    @Override
    public Resource createFromTemplate(@Nonnull TemplateContext context, @Nonnull Resource parent, @Nonnull String name,
                                       @Nonnull Resource template, boolean setTemplateProperty)
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
            applyContentTemplate(context, templateContent, targetContent, false, true);

            // prevent from unwanted properties in raw node types...
            if (setTemplateProperty && !primaryContentType.startsWith("nt:")) {
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
                if (!JcrConstants.JCR_CONTENT.equals(childName) && TEMPLATE_COPY_FILTER.accept(child)) {
                    createFromTemplate(context, target, child.getName(), child, setTemplateProperty);
                }
            }
        }
        // create a full structure copy of the template
        for (Resource child : template.getChildren()) {
            String childName = child.getName();
            if (!JcrConstants.JCR_CONTENT.equals(childName) && TEMPLATE_COPY_FILTER.accept(child)
                    // maybe the child is always created by the referenced template
                    && target.getChild(childName) == null) {
                createFromTemplate(context, target, childName, child, setTemplateProperty);
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
    protected void applyContentTemplate(@Nonnull TemplateContext context, @Nonnull Resource template,
                                        @Nonnull Resource target, boolean merge, boolean filter)
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
            if (!filter || TEMPLATE_COPY_FILTER.accept(child)) {
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
                applyContentTemplate(context, child, targetChild, merge, filter);
            }
        }
    }

    /**
     * Applies the resource properties from a template resource to a target resource.
     *
     * @param context  the resolver to use for CRUD operations
     * @param template the template content resource
     * @param target   the target content resource
     * @param merge    if 'true' the target properties will be unmodified and all new aspects from the template will be added
     */
    protected void applyTemplateProperties(@Nonnull TemplateContext context, @Nonnull Resource template,
                                           @Nonnull Resource target, boolean merge) {
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
