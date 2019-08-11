/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.service;

import com.composum.pages.commons.model.Component.ComponentPieces;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.filter.StringFilter;
import com.composum.sling.core.util.MimeTypeUtil;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.platform.staging.query.Query;
import com.composum.sling.platform.staging.query.QueryBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.tika.mime.MimeType;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.composum.pages.commons.PagesConstants.NT_COMPONENT;
import static com.composum.pages.commons.PagesConstants.PN_CATEGORY;
import static com.composum.pages.commons.PagesConstants.PN_COMPONENT_TYPE;
import static com.composum.pages.commons.model.Component.CONTEXT_ACTIONS_PATH;
import static com.composum.pages.commons.model.Component.CREATE_DIALOG_PATH;
import static com.composum.pages.commons.model.Component.DELETE_DIALOG_PATH;
import static com.composum.pages.commons.model.Component.EDIT_DIALOG_PATH;
import static com.composum.pages.commons.model.Component.EDIT_TILE_PATH;
import static com.composum.pages.commons.model.Component.EDIT_TOOLBAR_PATH;
import static com.composum.pages.commons.model.Component.HELP_PAGE_PATH;
import static com.composum.pages.commons.model.Component.THUMBNAIL_PATH;
import static com.composum.pages.commons.model.Component.TREE_ACTIONS_PATH;

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

    public static final String DEFAULT_COMPONENT_TEMPLATE_PATH = "/libs/composum/pages/commons/template/component";

    public static final List<String> COPY_DEEP = Collections.singletonList(
            HELP_PAGE_PATH
    );
    public static final List<String> COPY_FILES = Arrays.asList(
            EDIT_DIALOG_PATH,
            CREATE_DIALOG_PATH,
            DELETE_DIALOG_PATH,
            EDIT_TILE_PATH,
            THUMBNAIL_PATH,
            EDIT_TOOLBAR_PATH,
            TREE_ACTIONS_PATH,
            CONTEXT_ACTIONS_PATH
    );
    public static final ResourceFilter FILE_FILTER =
            new ResourceFilter.PrimaryTypeFilter(new StringFilter.WhiteList(JcrConstants.NT_FILE));

    public static final Map<String, Object> CREATE_FILE_PROPS = new HashMap<String, Object>() {{
        put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_FILE);
    }};
    public static final Map<String, Object> CREATE_FILE_CONTENT_PROPS = new HashMap<String, Object>() {{
        put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_RESOURCE);
    }};
    public static final Map<String, String> MIME_TYPE_FILTER = new HashMap<String, String>() {{
        put("text/x-jsp", "text/plain");
    }};

    @Reference
    protected ResourceManager resourceManager;

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

    //
    // component templates
    //

    @Nonnull
    protected Resource getDefaultTemplate(@Nonnull final ResourceResolver resolver) {
        return Objects.requireNonNull(resolver.getResource(DEFAULT_COMPONENT_TEMPLATE_PATH));
    }

    @Override
    public void createComponent(@Nonnull final ResourceResolver resolver,
                                @Nullable final Resource template,
                                @Nonnull final Resource parent,
                                @Nonnull final String name,
                                @Nullable final String primaryType,
                                @Nullable final String componentType,
                                @Nullable final String superType,
                                @Nullable final String title,
                                @Nullable final String description,
                                @Nullable final String[] category,
                                @Nonnull final ComponentPieces requested)
            throws PersistenceException {
        Map<String, Object> properties = new HashMap<>();
        properties.put(JcrConstants.JCR_PRIMARYTYPE, StringUtils.isNotBlank(primaryType) ? primaryType : NT_COMPONENT);
        if (StringUtils.isNotBlank(componentType)) {
            properties.put(PN_COMPONENT_TYPE, componentType);
        }
        if (StringUtils.isNotBlank(superType)) {
            properties.put(ResourceUtil.PROP_RESOURCE_SUPER_TYPE, superType);
        }
        if (StringUtils.isNotBlank(title)) {
            properties.put(ResourceUtil.JCR_TITLE, title);
        }
        if (StringUtils.isNotBlank(description)) {
            properties.put(ResourceUtil.JCR_DESCRIPTION, description);
        }
        if (category != null && category.length > 0) {
            properties.put(PN_CATEGORY, category);
        }
        Resource component = resolver.create(parent, name, properties);
        Resource componentTemplate = template != null ? template : getDefaultTemplate(resolver);
        copyFiles(resolver, componentTemplate, component);
        adjustComponent(resolver, componentTemplate, component, requested);
    }

    @Override
    public void adjustComponent(@Nonnull final ResourceResolver resolver,
                                @Nullable Resource template,
                                @Nonnull final Resource component,
                                @Nonnull final ComponentPieces requested)
            throws PersistenceException {
        if (template == null) {
            template = getDefaultTemplate(resolver);
        }
        final ComponentPieces existing = new ComponentPieces(component);
        if (existing.editDialog != requested.editDialog) {
            if (requested.editDialog) {
                applyComponentPieceTemplate(resolver, template, component, EDIT_DIALOG_PATH);
            } else {
                removeComponentPiece(resolver, component, EDIT_DIALOG_PATH);
            }
        }
        if (existing.createDialog != requested.createDialog) {
            if (requested.createDialog) {
                applyComponentPieceTemplate(resolver, template, component, CREATE_DIALOG_PATH);
            } else {
                removeComponentPiece(resolver, component, CREATE_DIALOG_PATH);
            }
        }
        if (existing.deleteDialog != requested.deleteDialog) {
            if (requested.deleteDialog) {
                applyComponentPieceTemplate(resolver, template, component, DELETE_DIALOG_PATH);
            } else {
                removeComponentPiece(resolver, component, DELETE_DIALOG_PATH);
            }
        }
        if (existing.editTile != requested.editTile) {
            if (requested.editTile) {
                applyComponentPieceTemplate(resolver, template, component, EDIT_TILE_PATH);
            } else {
                removeComponentPiece(resolver, component, EDIT_TILE_PATH);
            }
        }
        if (existing.thumbnail != requested.thumbnail) {
            if (requested.thumbnail) {
                applyComponentPieceTemplate(resolver, template, component, THUMBNAIL_PATH);
            } else {
                removeComponentPiece(resolver, component, THUMBNAIL_PATH);
            }
        }
        if (existing.helpPage != requested.helpPage) {
            if (requested.helpPage) {
                applyComponentPieceTemplate(resolver, template, component, HELP_PAGE_PATH);
            } else {
                removeComponentPiece(resolver, component, HELP_PAGE_PATH);
            }
        }
        if (existing.editToolbar != requested.editToolbar) {
            if (requested.editToolbar) {
                applyComponentPieceTemplate(resolver, template, component, EDIT_TOOLBAR_PATH);
            } else {
                removeComponentPiece(resolver, component, EDIT_TOOLBAR_PATH);
            }
        }
        if (existing.treeActions != requested.treeActions) {
            if (requested.treeActions) {
                applyComponentPieceTemplate(resolver, template, component, TREE_ACTIONS_PATH);
            } else {
                removeComponentPiece(resolver, component, TREE_ACTIONS_PATH);
            }
        }
        if (existing.contextActions != requested.contextActions) {
            if (requested.contextActions) {
                applyComponentPieceTemplate(resolver, template, component, CONTEXT_ACTIONS_PATH);
            } else {
                removeComponentPiece(resolver, component, CONTEXT_ACTIONS_PATH);
            }
        }
    }

    protected void applyComponentPieceTemplate(@Nonnull final ResourceResolver resolver,
                                               @Nonnull final Resource template,
                                               @Nonnull final Resource component,
                                               @Nonnull final String piecePath)
            throws PersistenceException {
        Resource templateNode = template;
        Resource componentNode = component;
        String[] path = piecePath.split(("/"));
        for (int i = 0; templateNode != null && i < path.length; i++) {
            templateNode = templateNode.getChild(path[i]);
            if (templateNode != null) {
                Resource child = componentNode.getChild(path[i]);
                if (child == null) {
                    if (i == path.length - 1) {
                        child = applyTemplateResource(resolver, piecePath, templateNode, componentNode);
                    } else {
                        child = resolver.create(componentNode, templateNode.getName(), templateNode.getValueMap());
                    }
                }
                componentNode = child;
            }
        }
    }

    /**
     * create chosen component node from the template node referenced by the piece path
     *
     * @param piecePath     the path of the requested component piece
     * @param templateNode  the pieces template node to use for creation
     * @param componentNode the new component node parent node
     * @return the created component node
     */
    protected Resource applyTemplateResource(@Nonnull final ResourceResolver resolver,
                                             @Nonnull final String piecePath,
                                             @Nonnull final Resource templateNode,
                                             @Nonnull final Resource componentNode)
            throws PersistenceException {
        Resource child;
        if (COPY_DEEP.contains(piecePath)) {
            child = resourceManager.createFromTemplate(new ResourceManager.NopTemplateContext(resolver),
                    componentNode, templateNode.getName(), templateNode, false);
        } else {
            child = resolver.create(componentNode, templateNode.getName(), templateNode.getValueMap());
            if (COPY_FILES.contains(piecePath)) {
                copyFiles(resolver, templateNode, child);
            }
        }
        return child;
    }

    /**
     * copy all file resources of the template node to the component node if accepted by the services file filter
     */
    protected void copyFiles(@Nonnull final ResourceResolver resolver,
                             @Nonnull final Resource templateNode,
                             @Nonnull final Resource componentNode)
            throws PersistenceException {
        Pattern templateNodeNameFile = Pattern.compile("^" + templateNode.getName() + "(\\.[\\w]+)$");
        for (Resource child : templateNode.getChildren()) {
            if (FILE_FILTER.accept(child)) {
                Matcher matcher = templateNodeNameFile.matcher(child.getName());
                // adapt names of files named like the template node to the destination node (component name)
                String fileName = matcher.matches() ? componentNode.getName() + matcher.group(1) : child.getName();
                resourceManager.createFromTemplate(new ResourceManager.NopTemplateContext(resolver),
                        componentNode, fileName, child, false);
            }
        }
    }

    protected void removeComponentPiece(@Nonnull final ResourceResolver resolver,
                                        @Nonnull final Resource component,
                                        @Nonnull String piecePath) throws PersistenceException {
        if (StringUtils.isNotBlank(piecePath)) {
            Resource piece = component.getChild(piecePath);
            if (piece != null) {
                do {
                    resolver.delete(piece);
                    piecePath = StringUtils.substringBeforeLast(piecePath, "/");
                }
                while (StringUtils.isNotBlank(piecePath) &&
                        (piece = component.getChild(piecePath)) != null && !piece.hasChildren());
            }
        }
    }

    public void updateFile(@Nonnull final ResourceResolver resolver,
                           @Nonnull final String path,
                           @Nonnull final InputStream stream)
            throws PersistenceException {
        String fileName = StringUtils.substringAfterLast(path, "/");
        Resource resource = resolver.getResource(path);
        if (resource == null) {
            String parentPath = StringUtils.substringBeforeLast(path, "/");
            Resource parent = resolver.getResource(parentPath);
            if (parent != null) {
                resource = resolver.create(parent, fileName, CREATE_FILE_PROPS);
            } else {
                throw new PersistenceException("can't create file: parent not available");
            }
        }
        Resource content = resource.getChild(JcrConstants.JCR_CONTENT);
        if (content == null) {
            Map<String, Object> properties = new HashMap<>(CREATE_FILE_CONTENT_PROPS);
            String mimeType = getMimeType(fileName);
            if (StringUtils.isNotBlank(mimeType)) {
                properties.put(JcrConstants.JCR_MIMETYPE, mimeType);
            }
            properties.put(JcrConstants.JCR_DATA, stream);
            resolver.create(resource, JcrConstants.JCR_CONTENT, properties);
        } else {
            ModifiableValueMap values = content.adaptTo(ModifiableValueMap.class);
            if (values != null) {
                values.put(JcrConstants.JCR_DATA, stream);
                values.put(JcrConstants.JCR_LASTMODIFIED, Calendar.getInstance());
                values.put(JcrConstants.JCR_LASTMODIFIED + "By", resolver.getUserID());
            } else {
                throw new PersistenceException("can't modify file content");
            }
        }
    }

    public String getMimeType(String fileName) {
        String result = null;
        MimeType mimeType = MimeTypeUtil.getMimeType(fileName);
        if (mimeType != null) {
            String mapped = MIME_TYPE_FILTER.get(result = mimeType.getName());
            if (mapped != null) {
                result = mapped;
            }
        }
        return result;
    }
}
