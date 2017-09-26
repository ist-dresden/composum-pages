package com.composum.pages.commons.request;

import com.composum.sling.core.BeanContext;
import com.composum.sling.platform.security.PlatformAccessFilter;
import org.apache.sling.api.SlingHttpServletRequest;

import static com.composum.pages.commons.PagesConstants.PAGES_PREFIX;

public class AccessMode extends RequestAspect<PlatformAccessFilter.AccessMode> {

    public static final String PARAMETER_NAME = "pages.access";
    public static final String ATTRIBUTE_KEY = PAGES_PREFIX + "access";

    public static AccessMode instance = new AccessMode();

    /**
     * returns the locale instance determined using the given context (request)
     */
    public static PlatformAccessFilter.AccessMode get(BeanContext context) {
        return instance.getAspect(context);
    }

    // request aspect

    protected AccessMode() {
    }

    @Override
    protected String getValue(PlatformAccessFilter.AccessMode instance) {
        return instance.name();
    }

    @Override
    protected String getParameterName() {
        return PARAMETER_NAME;
    }

    @Override
    protected String getAttributeKey() {
        return ATTRIBUTE_KEY;
    }

    @Override
    protected PlatformAccessFilter.AccessMode createInstance(String value) {
        return PlatformAccessFilter.AccessMode.valueOf(value);
    }

    @Override
    protected PlatformAccessFilter.AccessMode createInstance(SlingHttpServletRequest request) {
        String accessMode = (String)request.getAttribute(PlatformAccessFilter.ACCESS_MODE_KEY);
        return createInstance(accessMode != null ? accessMode : PlatformAccessFilter.AccessMode.AUTHOR.name());
    }
}
