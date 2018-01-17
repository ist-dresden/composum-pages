package com.composum.pages.commons;

import com.composum.pages.commons.model.Container;
import com.composum.pages.commons.model.Element;
import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.Site;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

public class PagesConstants {

    public static final String ENCODING = "UTF-8";

    public static final String CPP_NAMESPACE = "cpp";
    public static final String CPP_PREFIX = CPP_NAMESPACE + ":";
    public static final String COMPOSUM_PREFIX = "composum-";
    public static final String PAGES_PREFIX = COMPOSUM_PREFIX + "pages-";

    public static final String LANGUAGE_KEY = PAGES_PREFIX + "language";

    /** Component declarations */

    public static final String NODE_TYPE_COMPONENT = CPP_PREFIX + "Component";
    public static final String PROP_COMPONENT_TYPE = "componentType";

    /** Content elements */

    public static final String NODE_TYPE_SOMETHING = "nt:unstructured";
    public static final String NODE_TYPE_ELEMENT = CPP_PREFIX + "Element";
    public static final String NODE_TYPE_CONTAINER = CPP_PREFIX + "Container";

    public static final String PROP_ALLOWED_CONTAINERS = "allowedContainers";
    public static final String PROP_ALLOWED_ELEMENTS = "allowedElements";

    /** Page */

    public static final String NODE_TYPE_PAGE = CPP_PREFIX + "Page";
    public static final String NODE_TYPE_PAGE_CONTENT = CPP_PREFIX + "PageContent";

    public static final String PROP_ALLOWED_PARENTS = "allowedParents";
    public static final String PROP_ALLOWED_CHILDREN = "allowedChildren";

    public static final String NAVIGATION_PROPS = "navigation/";
    public static final String PROP_HIDE_IN_NAV = NAVIGATION_PROPS + "hideInNav";
    public static final String PROP_NAV_TITLE = NAVIGATION_PROPS + "title";

    public static final String PROP_SLING_TARGET = "sling:target";

    public static final String SUBNODE_STYLE = "style/";
    public static final String PROP_VIEW_CATEGORY = SUBNODE_STYLE + "category.view";
    public static final String PROP_EDIT_CATEGORY = SUBNODE_STYLE + "category.edit";
    public static final String DEFAULT_VIEW_CATEGORY = "composum.pages.content.view";
    public static final String DEFAULT_EDIT_CATEGORY = "composum.pages.content.edit";

    public static final String SEARCH_PROPS = "search/";
    /** Property of a page that, if true, hides the page in search results. */
    public static final String PROP_IGNORE_IN_SEARCH = SEARCH_PROPS + "ignoreInSearch";

    /** Site */

    public static final String NODE_TYPE_SITE = CPP_PREFIX + "Site";
    public static final String NODE_TYPE_SITE_CONFIGURATION = CPP_PREFIX + "SiteConfiguration";
    public static final String PROP_HOMEPAGE = "homepage";
    public static final String DEFAULT_HOMEPAGE_PATH = "home";

    /** Widget */

    public static final String NODE_TYPE_WIDGET = CPP_PREFIX + "Widget";
    public static final String PROP_WIDGET_TYPE = "widgetType";

    /** general properties */

    public static final String PROP_CREATION_DATE = "jcr:created";
    public static final String PROP_LAST_MODIFIED = ResourceUtil.PROP_LAST_MODIFIED;
    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /** */

    public enum ComponentType {

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
            } else if (Page.isPage(resource)) {
                return page;
            } else if (Container.isContainer(resolver, resource, typeHint)) {
                return container;
            } else if (Element.isElement(resolver, resource, typeHint)) {
                return element;
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
}
