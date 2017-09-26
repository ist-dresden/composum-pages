package com.composum.pages.commons.taglib;

import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.properties.Languages;
import com.composum.pages.commons.request.AccessMode;
import com.composum.pages.commons.request.DisplayMode;
import com.composum.pages.commons.request.RequestAspect;
import com.composum.pages.commons.request.RequestLocale;
import com.composum.pages.commons.service.PageManager;
import com.composum.sling.core.BeanContext;
import com.composum.sling.platform.security.PlatformAccessFilter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import javax.servlet.jsp.PageContext;

public class DefineObjectsTag extends org.apache.sling.scripting.jsp.taglib.DefineObjectsTag {

    public static final String PAGES_AUTHOR = "pagesAuthor";
    public static final String PAGES_PUBLIC = "pagesPublic";

    public static final String PAGES_MODE_PREFIX = "pagesMode";
    public static final String PAGES_MODE_NONE = PAGES_MODE_PREFIX + "None";
    public static final String PAGES_MODE_PREVIEW = PAGES_MODE_PREFIX + "Preview";
    public static final String PAGES_MODE_EDIT = PAGES_MODE_PREFIX + "Edit";
    public static final String PAGES_MODE_DEVELOP = PAGES_MODE_PREFIX + "Develop";

    public static final String CURRENT_PAGE = "currentPage";

    protected BeanContext context;

    @Override
    public int doEndTag() {
        int result = super.doEndTag();
        context = createContext(pageContext);

        PlatformAccessFilter.AccessMode accessMode = AccessMode.get(context);
        context.setAttribute(PAGES_AUTHOR, accessMode == PlatformAccessFilter.AccessMode.AUTHOR
                ? Boolean.TRUE : Boolean.FALSE, BeanContext.Scope.request);
        context.setAttribute(PAGES_PUBLIC, accessMode == PlatformAccessFilter.AccessMode.PUBLIC
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

        RequestLocale.get(context);
        setCurrentPage();
        setLanguages();

        return result;
    }

    protected BeanContext createContext(PageContext pageContext) {
        BeanContext context = new BeanContext.Page(pageContext);
        context.setAttribute(RequestAspect.FRAME_CONTEXT_ATTR + ":" + RequestLocale.ATTRIBUTE_KEY,
                Boolean.TRUE, BeanContext.Scope.request);
        return context;
    }

    protected void setCurrentPage() {

        if (context.getAttribute(CURRENT_PAGE, Page.class) == null) {
            SlingHttpServletRequest request = context.getRequest();
            Resource resource = determineResource(request);
            PageManager pageManager = context.getService(PageManager.class);
            ;
            Resource pageResource = pageManager.getContainingPageResource(resource);

            if (pageResource != null) {
                Page page = pageManager.createBean(context, pageResource);
                request.setAttribute(CURRENT_PAGE, page);
            }
        }
    }

    protected void setLanguages() {
        if (Languages.get(context) == null) {
            SlingHttpServletRequest request = context.getRequest();
            Resource resource = determineResource(request);
            Languages.set(context, resource);
        }
    }

    protected Resource determineResource(SlingHttpServletRequest request) {
        return request.getResource();
    }
}
