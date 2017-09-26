package com.composum.pages.commons.taglib;

import com.composum.pages.commons.request.DisplayMode;
import com.composum.pages.commons.request.RequestAspect;
import com.composum.sling.core.BeanContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import javax.servlet.jsp.PageContext;

public class DefineFrameObjectsTag extends DefineObjectsTag {

    @Override
    protected BeanContext createContext(PageContext pageContext) {
        BeanContext context = super.createContext(pageContext);
        context.setAttribute(RequestAspect.FRAME_CONTEXT_ATTR + ":" + DisplayMode.ATTRIBUTE_KEY,
                Boolean.TRUE, BeanContext.Scope.request);
        return context;
    }

    @Override
    protected Resource determineResource(SlingHttpServletRequest request) {
        Resource resource = null;
        String path = request.getRequestPathInfo().getSuffix();
        if (StringUtils.isNotBlank(path)) {
            resource = request.getResourceResolver().resolve(path);
        }
        if (resource == null) {
            resource = request.getResource();
        }
        return resource;
    }
}
