/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.service;

import com.composum.pages.commons.model.Component.ComponentPieces;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;

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
    public void createComponent(@Nonnull final Resource parent,
                                @Nonnull final String name,
                                @Nullable final String primaryType,
                                @Nullable final String componentType,
                                @Nullable final String superType,
                                @Nullable final String title,
                                @Nullable final String description,
                                @Nullable final String[] categories,
                                @Nonnull final ComponentPieces requested) throws PersistenceException {
        ResourceResolver resolver = parent.getResourceResolver();
        Map<String, Object> properties = new HashMap<>();
        properties.put(JcrConstants.JCR_PRIMARYTYPE, StringUtils.isNotBlank(primaryType) ? primaryType : NT_COMPONENT);
        if (StringUtils.isNotBlank(componentType)) {
            properties.put(PN_COMPONENT_TYPE, componentType);
        }
        if (StringUtils.isNotBlank(componentType)) {
            properties.put(ResourceUtil.PROP_RESOURCE_SUPER_TYPE, superType);
        }
        if (StringUtils.isNotBlank(title)) {
            properties.put(ResourceUtil.JCR_TITLE, title);
        }
        if (StringUtils.isNotBlank(description)) {
            properties.put(ResourceUtil.JCR_DESCRIPTION, description);
        }
        if (categories != null && categories.length > 0) {
            properties.put(PN_CATEGORY, categories);
        }
        Resource component = resolver.create(parent, name, properties);
        adjustComponent(component, requested);
    }

    @Override
    public void adjustComponent(@Nonnull final Resource component, @Nonnull final ComponentPieces requested) throws PersistenceException {
        final ComponentPieces existing = new ComponentPieces(component);
        if (existing.editDialog != requested.editDialog) {
            if (requested.editDialog) {
                applyComponentPieceTemplate(component, EDIT_DIALOG_PATH);
            } else {
                removeComponentPiece(component, EDIT_DIALOG_PATH);
            }
        }
        if (existing.createDialog != requested.createDialog) {
            if (requested.createDialog) {
                applyComponentPieceTemplate(component, CREATE_DIALOG_PATH);
            } else {
                removeComponentPiece(component, CREATE_DIALOG_PATH);
            }
        }
        if (existing.deleteDialog != requested.deleteDialog) {
            if (requested.deleteDialog) {
                applyComponentPieceTemplate(component, DELETE_DIALOG_PATH);
            } else {
                removeComponentPiece(component, DELETE_DIALOG_PATH);
            }
        }
        if (existing.editTile != requested.editTile) {
            if (requested.editTile) {
                applyComponentPieceTemplate(component, EDIT_TILE_PATH);
            } else {
                removeComponentPiece(component, EDIT_TILE_PATH);
            }
        }
        if (existing.thumbnail != requested.thumbnail) {
            if (requested.thumbnail) {
                applyComponentPieceTemplate(component, THUMBNAIL_PATH);
            } else {
                removeComponentPiece(component, THUMBNAIL_PATH);
            }
        }
        if (existing.helpPage != requested.helpPage) {
            if (requested.helpPage) {
                applyComponentPieceTemplate(component, HELP_PAGE_PATH);
            } else {
                removeComponentPiece(component, HELP_PAGE_PATH);
            }
        }
        if (existing.editToolbar != requested.editToolbar) {
            if (requested.editToolbar) {
                applyComponentPieceTemplate(component, EDIT_TOOLBAR_PATH);
            } else {
                removeComponentPiece(component, EDIT_TOOLBAR_PATH);
            }
        }
        if (existing.treeActions != requested.treeActions) {
            if (requested.treeActions) {
                applyComponentPieceTemplate(component, TREE_ACTIONS_PATH);
            } else {
                removeComponentPiece(component, TREE_ACTIONS_PATH);
            }
        }
        if (existing.contextActions != requested.contextActions) {
            if (requested.contextActions) {
                applyComponentPieceTemplate(component, CONTEXT_ACTIONS_PATH);
            } else {
                removeComponentPiece(component, CONTEXT_ACTIONS_PATH);
            }
        }
    }

    protected void applyComponentPieceTemplate(@Nonnull final Resource component,
                                               @Nonnull final String piecePath) throws PersistenceException {
        ResourceResolver resolver = component.getResourceResolver();
        Resource templateRoot = resolver.getResource("/libs/composum/pages/commons/template/component");
        if (templateRoot != null) {
            Resource templateNode = templateRoot;
            Resource componentNode = component;
            String[] path = piecePath.split(("/"));
            for (int i = 0; templateNode != null && i < path.length; i++) {
                templateNode = templateNode.getChild(path[i]);
                if (templateNode != null) {
                    Resource child = componentNode.getChild(path[i]);
                    if (child == null) {
                        child = resolver.create(componentNode, templateNode.getName(), templateNode.getValueMap());
                    }
                    componentNode = child;
                }
            }
        }
    }

    protected void removeComponentPiece(@Nonnull final Resource component,
                                        @Nonnull String piecePath) throws PersistenceException {
        if (StringUtils.isNotBlank(piecePath)) {
            Resource piece = component.getChild(piecePath);
            if (piece != null) {
                ResourceResolver resolver = component.getResourceResolver();
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
