/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons;

import com.composum.pages.commons.model.Component;
import com.composum.pages.commons.model.Container;
import com.composum.pages.commons.model.Element;
import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.util.ResolverUtil;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.platform.security.PlatformAccessFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.regex.Pattern;

public interface PagesConstants {

    String ENCODING = "UTF-8";

    String CPP_NAMESPACE = "cpp";
    String CPP_PREFIX = CPP_NAMESPACE + ":";
    String COMPOSUM_PREFIX = "composum-";
    String PAGES_PREFIX = COMPOSUM_PREFIX + "pages-";

    /** request attributes */

    String RA_CONTEXT_PATH = "contextPath";
    String RA_CURRENT_PAGE = "currentPage";

    /** I18N */

    String PROP_PAGE_LANGUAGES = "pageLanguages";
    String LANGUAGES_PATH = "jcr:content/languages";
    String DEFAULT_LANGUAGES = "/libs/composum/pages/commons/default/" + LANGUAGES_PATH;
    String LANGUAGE_NAME_SEPARATOR = "_";

    String LANGUAGES_ATTR = PAGES_PREFIX + "declared-languages";
    String LANGUAGE_CSS_KEY = PAGES_PREFIX + "language";

    /** request aspects */

    String ACCESS_MODE_REQ_PARAM = PlatformAccessFilter.ACCESS_MODE_PARAM;
    String DISPLAY_MODE_SELECT_PARAM = "pages.mode";
    String DISPLAY_MODE_VIEW_PARAM = "pages.view";
    String LOCALE_REQUEST_PARAM = "pages.locale";

    String ACCESS_MODE_ATTR = PlatformAccessFilter.ACCESS_MODE_KEY;
    String DISPLAY_MODE_ATTR = "composum-pages-request-display";
    String RA_PAGES_LOCALE = "composum-pages-request-locale";
    String RA_PAGES_LANGUAGE = "composum-pages-request-language";
    String RA_STICKY_LOCALE = "composum-pages-sticky-locale";

    String PAGES_FRAME_PATH = "/bin/pages";

    /** Component declarations */

    String NT_COMPONENT = CPP_PREFIX + "Component";
    String PN_COMPONENT_TYPE = "componentType";
    String PN_CATEGORY = "category";
    String CATEGORY_OTHER = "other";

    /* Content elements */

    /** the reference path property name of a reference component (for the referrers query) */
    String PN_CONTENT_REFERENCE = "contentReference";
    /** the key part of a reference component type (for the referrers query) */
    String RES_TYPE_KEY_REFERENCE = "/reference";

    String NODE_TYPE_SOMETHING = "nt:unstructured";
    String NODE_TYPE_ELEMENT = CPP_PREFIX + "Element";
    String NODE_TYPE_CONTAINER = CPP_PREFIX + "Container";

    String PROP_ALLOWED_CONTAINERS = "allowedContainers";
    String PROP_ALLOWED_ELEMENTS = "allowedElements";

    /** Page */

    String NODE_TYPE_PAGE = CPP_PREFIX + "Page";
    String NODE_TYPE_PAGE_CONTENT = CPP_PREFIX + "PageContent";

    String META_ROOT_PATH = "/var/composum/content";
    Pattern META_PATH_PATTERN = Pattern.compile("^/[^/]+(/.+)(/jcr:content(/.*)?)?$");
    String META_NODE_NAME = CPP_PREFIX + "metaData";
    String META_NODE_TYPE = CPP_PREFIX + "MetaData";

    String PROP_ALLOWED_PARENTS = "allowedParents";
    String PROP_ALLOWED_CHILDREN = "allowedChildren";

    String NAVIGATION_PROPS = "navigation/";
    String PROP_HIDE_IN_NAV = NAVIGATION_PROPS + "hideInNav";
    String PROP_NAV_TITLE = NAVIGATION_PROPS + "title";

    String PROP_SLING_TARGET = "sling:target";

    String SUBNODE_STYLE = "style/";
    String PROP_VIEW_CATEGORY = SUBNODE_STYLE + "category.view";
    String PROP_EDIT_CATEGORY = SUBNODE_STYLE + "category.edit";
    String DEFAULT_VIEW_CATEGORY = "composum.pages.components.view";
    String DEFAULT_EDIT_CATEGORY = "composum.pages.components.edit";

    String SEARCH_PROPS = "search/";
    /** Property of a page that, if true, hides the page in search results. */
    String PROP_IGNORE_IN_SEARCH = SEARCH_PROPS + "ignoreInSearch";

    /** Site */

    String DEFAULT_SITES_ROOT = "sites";
    String NODE_TYPE_SITE = CPP_PREFIX + "Site";
    String NODE_TYPE_SITE_CONFIGURATION = CPP_PREFIX + "SiteConfiguration";
    String PROP_HOMEPAGE = "homepage";
    String DEFAULT_HOMEPAGE_PATH = "home";

    /** Template */

    String PROP_TEMPLATE = "template";
    String PROP_IS_TEMPLATE = "isTemplate";
    String PROP_TEMPLATE_REF = "templateRef";

    String NODE_NAME_DESIGN = CPP_PREFIX + "design";
    String PROP_DESIGN_REF = "designRef";
    String PROP_TYPE_PATTERNS = "typePatterns";

    String PROP_ALLOWED_PARENT_TEMPLATES = "allowedParentTemplates";
    String PROP_FORBIDDEN_PARENT_TEMPLATES = "forbiddenParentTemplates";
    String PROP_ALLOWED_CHILD_TEMPLATES = "allowedChildTemplates";
    String PROP_FORBIDDEN_CHILD_TEMPLATES = "forbiddenChildTemplates";

    String PROP_ALLOWED_PARENT_TYPES = "allowedParentTypes";
    String PROP_FORBIDDEN_PARENT_TYPES = "forbiddenParentTypes";
    String PROP_ALLOWED_CHILD_TYPES = "allowedChildTypes";
    String PROP_FORBIDDEN_CHILD_TYPES = "forbiddenChildTypes";

    String PROP_ALLOWED_PATHS = "allowedPaths";
    String PROP_FORBIDDEN_PATHS = "forbiddenPaths";

    /** Widget */

    String NODE_TYPE_WIDGET = CPP_PREFIX + "Widget";
    String PROP_WIDGET_TYPE = "widgetType";

    /** general properties */

    String PN_TITLE = "title";
    String PN_JCR_TITLE = ResourceUtil.PROP_TITLE;
    String[] PN_TITLE_KEYS = new String[]{PN_TITLE, PN_JCR_TITLE};
    String PN_SUBTITLE = "subtitle";
    String PN_DESCRIPTION = "description";

    String PROP_CREATION_DATE = "jcr:created";
    String PROP_LAST_MODIFIED = ResourceUtil.PROP_LAST_MODIFIED;
    String PROP_LAST_MODIFIED_BY = "jcr:lastModifiedBy";
    String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";

    String NP_SETTINGS = "settings";

    /** release & version */

    String KEY_CURRENT_RELEASE = "current";
    String VERSION_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    Pattern RELEASE_LABEL_PATTERN = Pattern.compile("^composum-release-(.+)$");

    /** date & time */

    String PP_FORMAT = "format/";

    String SP_DAY_FMT = PP_FORMAT + "day";
    String SP_TIME_FMT = PP_FORMAT + "time";
    String SP_DATE_FMT = PP_FORMAT + "value";
    String SP_DATETIME_FMT = PP_FORMAT + "datetime";

    String DEF_DAY_FMT = "d";
    String DEF_TIME_FMT = "HH:mm";
    String DEF_DATE_FMT = "yyyy-MM-dd";
    String DEF_DATETIME_FMT = DEF_DATE_FMT + " " + DEF_TIME_FMT;

    /**
     *
     */
    enum ComponentType {

        site, page, container, element, something;

        public static ComponentType typeOf(String string) {
            ComponentType type = something;
            if (StringUtils.isNotBlank(string)) {
                if (string.startsWith(CPP_PREFIX)) {
                    string = string.substring(4);
                }
                try {
                    type = valueOf(ComponentType.class, string.toLowerCase());
                } catch (IllegalArgumentException ex) {
                    // ok, something
                }
            }
            return type;
        }

        public static ComponentType typeOf(ResourceResolver resolver, Resource resource, String typeHint) {
            if (Site.isSite(resource)) {
                return site;
            } else if (Page.isPage(resource) || Page.isPageContent(resource)) {
                return page;
            } else if (Container.isContainer(resolver, resource, typeHint)) {
                return container;
            } else if (Element.isElement(resolver, resource, typeHint)) {
                return element;
            } else if (Component.isComponent(resource)) {
                return typeOf(ResolverUtil.getTypeProperty(resource, PN_COMPONENT_TYPE, ""));
            } else {
                return something;
            }
        }

        public static String getPrimaryType(ComponentType type) {
            if (type != null) {
                switch (type) {
                    case site:
                        return NODE_TYPE_SITE;
                    case page:
                        return NODE_TYPE_PAGE;
                    case container:
                        return NODE_TYPE_CONTAINER;
                    case element:
                        return NODE_TYPE_ELEMENT;
                    default:
                        return NODE_TYPE_SOMETHING;
                }
            }
            return null;
        }
    }

    enum ReferenceType {page, asset}
}
