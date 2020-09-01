package com.composum.pages.commons.taglib;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.model.properties.Languages;
import com.composum.pages.commons.request.DisplayMode;
import com.composum.pages.commons.request.PagesLocale;
import com.composum.pages.commons.service.PageManager;
import com.composum.pages.commons.service.SiteManager;
import com.composum.pages.commons.service.Theme;
import com.composum.sling.core.BeanContext;
import com.composum.sling.platform.security.AccessMode;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import javax.servlet.jsp.PageContext;

import static com.composum.pages.commons.PagesConstants.RA_CONTEXT_PATH;
import static com.composum.pages.commons.PagesConstants.RA_CURRENT_PAGE;
import static com.composum.pages.commons.PagesConstants.RA_CURRENT_SITE;
import static com.composum.pages.commons.PagesConstants.RA_RESOURCE_REF;

public class DefineObjectsTag extends org.apache.sling.scripting.jsp.taglib.DefineObjectsTag {

    public static final String PAGES_ACCESS_PREFIX = "pagesAccess";
    public static final String PAGES_ACCESS_AUTHOR = PAGES_ACCESS_PREFIX + "Author";
    public static final String PAGES_ACCESS_PREVIEW = PAGES_ACCESS_PREFIX + "Preview";
    public static final String PAGES_ACCESS_PUBLIC = PAGES_ACCESS_PREFIX + "Public";

    public static final String PAGES_MODE_PREFIX = "pagesMode";
    public static final String PAGES_MODE_NONE = PAGES_MODE_PREFIX + "None";
    public static final String PAGES_MODE_PREVIEW = PAGES_MODE_PREFIX + "Preview";
    public static final String PAGES_MODE_EDIT = PAGES_MODE_PREFIX + "Edit";
    public static final String PAGES_MODE_DEVELOP = PAGES_MODE_PREFIX + "Develop";

    protected BeanContext context;

    @Override
    public int doEndTag() {
        int result = super.doEndTag();
        context = createContext(pageContext);
        SlingHttpServletRequest request = context.getRequest();

        Resource resourceRef = determineResourceRef(request);
        context.setAttribute(RA_CONTEXT_PATH, context.getRequest().getContextPath(), BeanContext.Scope.request);
        context.setAttribute(RA_RESOURCE_REF, resourceRef, BeanContext.Scope.request);

        AccessMode accessMode = request.adaptTo(AccessMode.class);
        context.setAttribute(PAGES_ACCESS_AUTHOR, accessMode == AccessMode.AUTHOR
                ? Boolean.TRUE : Boolean.FALSE, BeanContext.Scope.request);
        context.setAttribute(PAGES_ACCESS_PREVIEW, accessMode == AccessMode.PREVIEW
                ? Boolean.TRUE : Boolean.FALSE, BeanContext.Scope.request);
        context.setAttribute(PAGES_ACCESS_PUBLIC, accessMode == AccessMode.PUBLIC
                ? Boolean.TRUE : Boolean.FALSE, BeanContext.Scope.request);

        DisplayMode.Value displayMode = DisplayMode.current(context);
        context.setAttribute(PAGES_MODE_NONE, displayMode == DisplayMode.Value.NONE
                ? Boolean.TRUE : Boolean.FALSE, BeanContext.Scope.request);
        context.setAttribute(PAGES_MODE_PREVIEW, displayMode == DisplayMode.Value.PREVIEW || displayMode == DisplayMode.Value.BROWSE
                ? Boolean.TRUE : Boolean.FALSE, BeanContext.Scope.request);
        context.setAttribute(PAGES_MODE_EDIT, displayMode == DisplayMode.Value.EDIT || displayMode == DisplayMode.Value.DEVELOP
                ? Boolean.TRUE : Boolean.FALSE, BeanContext.Scope.request);
        context.setAttribute(PAGES_MODE_DEVELOP, displayMode == DisplayMode.Value.DEVELOP
                ? Boolean.TRUE : Boolean.FALSE, BeanContext.Scope.request);

        request.adaptTo(PagesLocale.class);
        if (resourceRef == null) {
            resourceRef = request.getResource();
        }
        setCurrentPage(resourceRef);
        setCurrentSite(resourceRef);
        setLanguages(resourceRef);

        return result;
    }

    protected BeanContext createContext(PageContext pageContext) {
        return new BeanContext.Page(pageContext);
    }

    protected void setCurrentPage(Resource resource) {

        if (context.getAttribute(RA_CURRENT_PAGE, Page.class) == null) {
            SlingHttpServletRequest request = context.getRequest();
            PageManager pageManager = context.getService(PageManager.class);
            Resource pageResource = pageManager.getContainingPageResource(resource);

            if (pageResource != null) {
                Page page = pageManager.createBean(context, pageResource);
                request.setAttribute(RA_CURRENT_PAGE, page);
                Theme theme = page.getTheme();
                if (theme != null) {
                    request.setAttribute(PagesConstants.RA_CURRENT_THEME, theme);
                }
            }
        }
    }

    protected void setCurrentSite(Resource resource) {

        if (context.getAttribute(RA_CURRENT_SITE, Site.class) == null) {
            SlingHttpServletRequest request = context.getRequest();
            SiteManager siteManager = context.getService(SiteManager.class);
            Resource siteResource = siteManager.getContainingSiteResource(resource);

            if (siteResource != null) {
                Site site = siteManager.createBean(context, siteResource);
                request.setAttribute(RA_CURRENT_SITE, site);
            }
        }
    }

    protected void setLanguages(Resource resource) {
        if (Languages.get(context) == null) {
            Languages.set(context, resource);
        }
    }

    protected Resource determineResourceRef(SlingHttpServletRequest request) {
        return null;
    }
}
