package com.composum.pages.commons.request;

import com.composum.sling.core.BeanContext;
import com.composum.sling.platform.security.PlatformAccessFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletResponse;
import javax.servlet.ServletResponseWrapper;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class RequestAspect<Type> {

    private static final Logger LOG = LoggerFactory.getLogger(RequestAspect.class);

    public static final String FRAME_CONTEXT_ATTR = "composum-pages-frame";

    protected abstract String getValue(Type instance);

    protected abstract String getParameterName();

    protected abstract String getAttributeKey();

    protected abstract Type createInstance(String value);

    protected abstract Type createInstance(SlingHttpServletRequest request);

    protected Type getAspect(BeanContext context) {
        SlingHttpServletRequest request = context.getRequest();
        String attributeKey = getAttributeKey();
        Type instance = (Type) request.getAttribute(attributeKey);
        if (instance == null) {
            String parameter = request.getParameter(getParameterName());
            if (StringUtils.isNotBlank(parameter)) {
                if (isFrameContext(context, attributeKey)) {
                    instance = setValue(context, parameter);
                } else {
                    instance = createInstance(parameter);
                }
            }
            if (isFrameContext(context, attributeKey)) {
                if (instance == null) {
                    HttpSession httpSession = request.getSession(
                            AccessMode.get(context) == PlatformAccessFilter.AccessMode.AUTHOR);
                    if (httpSession != null) {
                        String value = (String) httpSession.getAttribute(attributeKey);
                        if (StringUtils.isNotBlank(value)) {
                            try {
                                instance = setValue(context, value);
                            } catch (IllegalArgumentException ex) {
                                LOG.error("invalid session value '" + value + "' (" + ex.toString() + ")");
                            }
                        }
                    }
                }
                if (instance == null) {
                    Cookie cookie = request.getCookie(attributeKey);
                    if (cookie != null) {
                        String value = cookie.getValue();
                        if (StringUtils.isNotBlank(value)) {
                            try {
                                instance = createInstance(value);
                            } catch (IllegalArgumentException ex) {
                                LOG.error("invalid cookie '" + value + "' (" + ex.toString() + ")");
                            }
                        }
                    }
                }
            }
            if (instance == null) {
                instance = createInstance(request);
            }
            request.setAttribute(attributeKey, instance);
        }
        return instance;
    }

    protected boolean isFrameContext(BeanContext context, String attributeKey) {
        Boolean frameContext = context.getAttribute(FRAME_CONTEXT_ATTR + ":" + attributeKey, Boolean.class);
        return frameContext != null && frameContext;
    }

    /**
     *
     */
    public Type setValue(BeanContext context, String value) {
        Type instance = null;
        try {
            instance = createInstance(value);
            String attributeKey = getAttributeKey();
            SlingHttpServletRequest request = context.getRequest();
            HttpSession httpSession = request.getSession(
                    AccessMode.get(context) == PlatformAccessFilter.AccessMode.AUTHOR);
            value = getValue(instance);
            if (httpSession != null) {
                httpSession.setAttribute(attributeKey, value);
            }
            setCookie(context, attributeKey, value);
            request.setAttribute(attributeKey, instance);
        } catch (IllegalArgumentException ex) {
            LOG.error("invalid value '" + value + "' (" + ex.toString() + ")");
        }
        return instance;
    }

    protected void setCookie(BeanContext context, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(10 * 24 * 60 * 60);
        cookie.setPath("/");
        try {
            // for servlet-api 2.5 compatibility - TODO: check from time to time and remove if no longer necessary
            Method method = cookie.getClass().getMethod("setHttpOnly", boolean.class);
            method.invoke(cookie, true);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
        }
        HttpServletResponse response = context.getResponse();
        boolean isCommited = response.isCommitted();
        // it seems that there is a bug in one of the wrappers - the cookie is send only if the wrappers are skipped
        ServletResponse sr;
        while (response instanceof ServletResponseWrapper &&
                (sr = ((ServletResponseWrapper) response).getResponse()) instanceof HttpServletResponse) {
            response = (HttpServletResponse) sr;
        }
        response.addCookie(cookie);
        LOG.info("setCookie: " + cookie.getName() + "='" + cookie.getValue() + "' (commited: " + isCommited + ")");
    }
}
