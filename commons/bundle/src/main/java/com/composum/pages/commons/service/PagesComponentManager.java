/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.service;

import com.composum.pages.commons.model.Component.ComponentPieces;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.filter.StringFilter;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.platform.staging.query.Query;
import com.composum.sling.platform.staging.query.QueryBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

    @Override
    public void createComponent(@Nonnull final ResourceResolver resolver,
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
        Resource templateRoot = getTemplateRoot(resolver);
        if (templateRoot != null) {
            copyFiles(resolver, templateRoot, component);
        }
        adjustComponent(resolver, component, requested);
    }

    @Override
    public void adjustComponent(@Nonnull final ResourceResolver resolver,
                                @Nonnull final Resource component,
                                @Nonnull final ComponentPieces requested)
            throws PersistenceException {
        final ComponentPieces existing = new ComponentPieces(component);
        if (existing.editDialog != requested.editDialog) {
            if (requested.editDialog) {
                applyComponentPieceTemplate(resolver, component, EDIT_DIALOG_PATH);
            } else {
                removeComponentPiece(resolver, component, EDIT_DIALOG_PATH);
            }
        }
        if (existing.createDialog != requested.createDialog) {
            if (requested.createDialog) {
                applyComponentPieceTemplate(resolver, component, CREATE_DIALOG_PATH);
            } else {
                removeComponentPiece(resolver, component, CREATE_DIALOG_PATH);
            }
        }
        if (existing.deleteDialog != requested.deleteDialog) {
            if (requested.deleteDialog) {
                applyComponentPieceTemplate(resolver, component, DELETE_DIALOG_PATH);
            } else {
                removeComponentPiece(resolver, component, DELETE_DIALOG_PATH);
            }
        }
        if (existing.editTile != requested.editTile) {
            if (requested.editTile) {
                applyComponentPieceTemplate(resolver, component, EDIT_TILE_PATH);
            } else {
                removeComponentPiece(resolver, component, EDIT_TILE_PATH);
            }
        }
        if (existing.thumbnail != requested.thumbnail) {
            if (requested.thumbnail) {
                applyComponentPieceTemplate(resolver, component, THUMBNAIL_PATH);
            } else {
                removeComponentPiece(resolver, component, THUMBNAIL_PATH);
            }
        }
        if (existing.helpPage != requested.helpPage) {
            if (requested.helpPage) {
                applyComponentPieceTemplate(resolver, component, HELP_PAGE_PATH);
            } else {
                removeComponentPiece(resolver, component, HELP_PAGE_PATH);
            }
        }
        if (existing.editToolbar != requested.editToolbar) {
            if (requested.editToolbar) {
                applyComponentPieceTemplate(resolver, component, EDIT_TOOLBAR_PATH);
            } else {
                removeComponentPiece(resolver, component, EDIT_TOOLBAR_PATH);
            }
        }
        if (existing.treeActions != requested.treeActions) {
            if (requested.treeActions) {
                applyComponentPieceTemplate(resolver, component, TREE_ACTIONS_PATH);
            } else {
                removeComponentPiece(resolver, component, TREE_ACTIONS_PATH);
            }
        }
        if (existing.contextActions != requested.contextActions) {
            if (requested.contextActions) {
                applyComponentPieceTemplate(resolver, component, CONTEXT_ACTIONS_PATH);
            } else {
                removeComponentPiece(resolver, component, CONTEXT_ACTIONS_PATH);
            }
        }
    }

    protected Resource getTemplateRoot(@Nonnull final ResourceResolver resolver) {
        return resolver.getResource("/libs/composum/pages/commons/template/component");
    }

    protected void applyComponentPieceTemplate(@Nonnull final ResourceResolver resolver,
                                               @Nonnull final Resource component,
                                               @Nonnull final String piecePath)
            throws PersistenceException {
        Resource templateRoot = getTemplateRoot(resolver);
        if (templateRoot != null) {
            Resource templateNode = templateRoot;
            Resource componentNode = component;
            String[] path = piecePath.split(("/"));
            for (int i = 0; templateNode != null && i < path.length; i++) {
                templateNode = templateNode.getChild(path[i]);
                if (templateNode != null) {
                    Resource child = componentNode.getChild(path[i]);
                    if (child == null) {
                        if (i == path.length - 1) {
                            child = applyTemplateTarget(resolver, piecePath, templateNode, componentNode);
                        } else {
                            child = resolver.create(componentNode, templateNode.getName(), templateNode.getValueMap());
                        }
                    }
                    componentNode = child;
                }
            }
        }
    }

    protected Resource applyTemplateTarget(@Nonnull final ResourceResolver resolver,
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

    protected void copyFiles(@Nonnull final ResourceResolver resolver,
                             @Nonnull final Resource templateNode,
                             @Nonnull final Resource componentNode)
            throws PersistenceException {
        Pattern templateNodeNameFile = Pattern.compile("^" + templateNode.getName() + "(\\.[\\w]+)$");
        for (Resource child : templateNode.getChildren()) {
            if (FILE_FILTER.accept(child)) {
                Matcher matcher = templateNodeNameFile.matcher(child.getName());
                resourceManager.createFromTemplate(new ResourceManager.NopTemplateContext(resolver), componentNode,
                        matcher.matches() ? componentNode.getName() + matcher.group(1) : child.getName(),
                        child, false);
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
}
