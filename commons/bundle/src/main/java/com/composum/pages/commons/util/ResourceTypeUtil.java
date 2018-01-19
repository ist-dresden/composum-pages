package com.composum.pages.commons.util;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.Container;
import com.composum.pages.commons.model.Element;
import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.Site;
import com.composum.sling.core.filter.ResourceFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.HashMap;
import java.util.Map;

public class ResourceTypeUtil {

    public static final String EDIT_PATH = "edit";

    public static final String EDIT_DIALOG_PATH = EDIT_PATH + "/dialog";
    public static final String NEW_DIALOG_PATH = EDIT_DIALOG_PATH + "/new";
    public static final String CREATE_DIALOG_PATH = EDIT_DIALOG_PATH + "/create";
    public static final String DELETE_DIALOG_PATH = EDIT_DIALOG_PATH + "/delete";
    public static final String EDIT_TILE_PATH = EDIT_PATH + "/tile";
    public static final String EDIT_THUMBNAIL_PATH = EDIT_PATH + "/thumbnail";
    public static final String EDIT_TOOLBAR_PATH = EDIT_PATH + "/toolbar";
    public static final String TREE_ACTIONS_PATH = EDIT_PATH + "/tree";

    public static final String EDIT_DEFAULT_ROOT = "composum/pages/stage/edit/default/";

    public static final String DEFAULT_ELEMENT_DIALOG = EDIT_DEFAULT_ROOT + "element/dialog";
    public static final String DEFAULT_CREATE_DIALOG = DEFAULT_ELEMENT_DIALOG + "/create";
    public static final String DEFAULT_DELETE_DIALOG = DEFAULT_ELEMENT_DIALOG + "/delete";

    public static final String DEFAULT_CONTAINER_DIALOG = EDIT_DEFAULT_ROOT + "container/dialog";
    public static final String DEFAULT_NEW_DIALOG = DEFAULT_CONTAINER_DIALOG + "/new";

    public static final String DEFAULT_PAGE_DIALOG = EDIT_DEFAULT_ROOT + "page/dialog";
    public static final String DEFAULT_PAGE_CREATE_DIALOG = DEFAULT_PAGE_DIALOG + "/create";
    public static final String DEFAULT_PAGE_DELETE_DIALOG = DEFAULT_PAGE_DIALOG + "/delete";

    public static final String DEFAULT_SITE_DIALOG = EDIT_DEFAULT_ROOT + "site/dialog";
    public static final String DEFAULT_SITE_CREATE_DIALOG = DEFAULT_SITE_DIALOG + "/create";
    public static final String DEFAULT_SITE_DELETE_DIALOG = DEFAULT_SITE_DIALOG + "/delete";

    public static final String DEFAULT_ELEMENT_TILE = EDIT_DEFAULT_ROOT + "element/tile";
    public static final String DEFAULT_CONTAINER_TILE = EDIT_DEFAULT_ROOT + "container/tile";
    public static final String DEFAULT_PAGE_TILE = EDIT_DEFAULT_ROOT + "page/tile";
    public static final String DEFAULT_SITE_TILE = EDIT_DEFAULT_ROOT + "site/tile";

    public static final String DEFAULT_ELEMENT_THUMBNAIL = EDIT_DEFAULT_ROOT + "element/thumbnail";
    public static final String DEFAULT_CONTAINER_THUMBNAIL = EDIT_DEFAULT_ROOT + "container/thumbnail";
    public static final String DEFAULT_PAGE_THUMBNAIL = EDIT_DEFAULT_ROOT + "page/thumbnail";
    public static final String DEFAULT_SITE_THUMBNAIL = EDIT_DEFAULT_ROOT + "site/thumbnail";

    public static final String DEFAULT_TOOLBAR_PATH = EDIT_DEFAULT_ROOT + "element/toolbar";
    public static final String DEFAULT_CONTAINER_TOOLBAR = EDIT_DEFAULT_ROOT + "container/toolbar";
    public static final String DEFAULT_PAGE_TOOLBAR = EDIT_DEFAULT_ROOT + "page/toolbar";
    public static final String DEFAULT_SITE_TOOLBAR = EDIT_DEFAULT_ROOT + "site/toolbar";

    public static final String DEFAULT_TREE_ACTIONS = EDIT_DEFAULT_ROOT + "element/tree";
    public static final String DEFAULT_CONTAINER_ACTIONS = EDIT_DEFAULT_ROOT + "container/tree";
    public static final String DEFAULT_PAGE_ACTIONS = EDIT_DEFAULT_ROOT + "page/tree";
    public static final String DEFAULT_SITE_ACTIONS = EDIT_DEFAULT_ROOT + "site/tree";
    public static final String DEFAULT_FOLDER_ACTIONS = EDIT_DEFAULT_ROOT + "folder/tree";
    public static final String DEFAULT_NONE_ACTIONS = EDIT_DEFAULT_ROOT + "none/tree";

    public static boolean isFolder(Resource resource) {
        return ResourceFilter.FOLDER.accept(resource);
    }

    public interface SubtypeStrategy {

        String getDefaultResourcePath(ResourceResolver resolver, Resource resource, String type);
    }

    public static class ComponentTileStrategy implements SubtypeStrategy {

        public String getDefaultResourcePath(ResourceResolver resolver, Resource resource, String type) {
            return Site.isSite(resource) || Site.isSiteConfiguration(resource) ? DEFAULT_SITE_TILE
                    : Page.isPage(resource) || Page.isPageContent(resource) ? DEFAULT_PAGE_TILE
                    : Container.isContainer(resolver, resource, type) ? DEFAULT_CONTAINER_TILE
                    : DEFAULT_ELEMENT_TILE;
        }
    }

    public static class ComponentThumbnailStrategy implements SubtypeStrategy {

        public String getDefaultResourcePath(ResourceResolver resolver, Resource resource, String type) {
            return Site.isSite(resource) || Site.isSiteConfiguration(resource) ? DEFAULT_SITE_THUMBNAIL
                    : Page.isPage(resource) || Page.isPageContent(resource) ? DEFAULT_PAGE_THUMBNAIL
                    : Container.isContainer(resolver, resource, type) ? DEFAULT_CONTAINER_THUMBNAIL
                    : DEFAULT_ELEMENT_THUMBNAIL;
        }
    }

    public static class EditDialogStrategy implements SubtypeStrategy {

        public String getDefaultResourcePath(ResourceResolver resolver, Resource resource, String type) {
            return Page.isPage(resource) || Page.isPageContent(resource) ? DEFAULT_PAGE_DIALOG
                    : Site.isSite(resource) || Site.isSiteConfiguration(resource) ? DEFAULT_SITE_DIALOG
                    : null;
        }
    }

    public static class NewDialogStrategy extends EditDialogStrategy {

        public String getDefaultResourcePath(ResourceResolver resolver, Resource resource, String type) {
            return DEFAULT_NEW_DIALOG;
        }
    }

    public static class CreateDialogStrategy extends EditDialogStrategy {

        public String getDefaultResourcePath(ResourceResolver resolver, Resource resource, String type) {
            PagesConstants.ComponentType componentType = PagesConstants.ComponentType.typeOf(type);
            return componentType == PagesConstants.ComponentType.page
                    || Page.isPage(resource) || Page.isPageContent(resource) ? DEFAULT_PAGE_CREATE_DIALOG
                    : componentType == PagesConstants.ComponentType.site
                    || Site.isSite(resource) || Site.isSiteConfiguration(resource) ? DEFAULT_SITE_CREATE_DIALOG
                    : DEFAULT_CREATE_DIALOG;
        }
    }

    public static class DeleteDialogStrategy extends EditDialogStrategy {

        public String getDefaultResourcePath(ResourceResolver resolver, Resource resource, String type) {
            return Page.isPage(resource) || Page.isPageContent(resource) ? DEFAULT_PAGE_DELETE_DIALOG
                    : Site.isSite(resource) || Site.isSiteConfiguration(resource) ? DEFAULT_SITE_DELETE_DIALOG
                    : DEFAULT_DELETE_DIALOG;
        }
    }

    public static class EditToolbarStrategy implements SubtypeStrategy {

        public String getDefaultResourcePath(ResourceResolver resolver, Resource resource, String type) {
            return Site.isSite(resource) || Site.isSiteConfiguration(resource) ? DEFAULT_SITE_TOOLBAR
                    : Page.isPage(resource) || Page.isPageContent(resource) ? DEFAULT_PAGE_TOOLBAR
                    : Container.isContainer(resolver, resource, type) ? DEFAULT_CONTAINER_TOOLBAR
                    : DEFAULT_TOOLBAR_PATH;
        }
    }

    public static class TreeActionsStrategy implements SubtypeStrategy {

        public String getDefaultResourcePath(ResourceResolver resolver, Resource resource, String type) {
            return Site.isSite(resource) || Site.isSiteConfiguration(resource) ? DEFAULT_SITE_ACTIONS
                    : Page.isPage(resource) || Page.isPageContent(resource) ? DEFAULT_PAGE_ACTIONS
                    : Container.isContainer(resolver, resource, type) ? DEFAULT_CONTAINER_ACTIONS
                    : Element.isElement(resolver, resource, type) ? DEFAULT_TREE_ACTIONS
                    : isFolder(resource) ? DEFAULT_FOLDER_ACTIONS
                    : DEFAULT_NONE_ACTIONS;
        }
    }

    public static final Map<String, SubtypeStrategy> SUBTYPES;

    static {
        SUBTYPES = new HashMap<>();
        SUBTYPES.put(EDIT_TILE_PATH, new ComponentTileStrategy());
        SUBTYPES.put(EDIT_THUMBNAIL_PATH, new ComponentThumbnailStrategy());
        SUBTYPES.put(EDIT_DIALOG_PATH, new EditDialogStrategy());
        SUBTYPES.put(NEW_DIALOG_PATH, new NewDialogStrategy());
        SUBTYPES.put(CREATE_DIALOG_PATH, new CreateDialogStrategy());
        SUBTYPES.put(DELETE_DIALOG_PATH, new DeleteDialogStrategy());
        SUBTYPES.put(EDIT_TOOLBAR_PATH, new EditToolbarStrategy());
        SUBTYPES.put(TREE_ACTIONS_PATH, new TreeActionsStrategy());
    }

    public static String getSubtypePath(ResourceResolver resolver, Resource resource, String type, String subtype, String selectors) {
        Resource typeResource = getSubtype(resolver, resource, type, subtype, selectors);
        return typeResource != null ? typeResource.getPath() : null;
    }

    public static Resource getSubtype(ResourceResolver resolver, Resource resource, String type, String subtype, String selectors) {
        Resource typeResource = resource != null
                ? ResolverUtil.getResourceType(resource, type, subtype)
                : ResolverUtil.getResourceType(resolver, type, subtype);
        if (typeResource == null) {
            if (StringUtils.isNotBlank(selectors)) {
                typeResource = ResolverUtil.getResourceType(resolver,
                        ResourceTypeUtil.getDefaultResourcePath(resolver, resource, type,
                                subtype + "/" + selectors.replace('.', '/')));
            }
            if (typeResource == null) {
                typeResource = ResolverUtil.getResourceType(resolver,
                        ResourceTypeUtil.getDefaultResourcePath(resolver, resource, type, subtype));
            }
        }
        return typeResource;
    }

    public static String getDefaultResourcePath(ResourceResolver resolver, Resource resource, String type,
                                                String subtype) {
        SubtypeStrategy strategy = SUBTYPES.get(subtype);
        return strategy != null ? strategy.getDefaultResourcePath(resolver, resource, type) : null;
    }
}
