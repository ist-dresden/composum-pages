package com.composum.pages.commons.request;

import com.composum.pages.commons.PagesConfiguration;
import com.composum.pages.commons.model.Page;
import com.composum.platform.commons.request.AccessMode;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.adapter.AdapterFactory;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.composum.pages.commons.PagesConstants.ACCESS_MODE_ATTR;
import static com.composum.pages.commons.PagesConstants.ACCESS_MODE_REQ_PARAM;
import static com.composum.pages.commons.PagesConstants.DISPLAY_MODE_ATTR;
import static com.composum.pages.commons.PagesConstants.DISPLAY_MODE_VIEW_PARAM;
import static com.composum.pages.commons.PagesConstants.LOCALE_REQUEST_PARAM;
import static com.composum.pages.commons.PagesConstants.RA_PAGES_LOCALE;

/**
 * the adapter factory for all aspects in the request context of the Pages module
 * <p>
 * <dl>
 * <dt>access mode</dt>
 * <dd>the access mode is normally set by the platform access filter an can be overridden by the
 * parameter 'cpm.access' with the values 'AUTHOR', 'PREVIEW' or 'PUBLIC'
 * if the request is received via an authoring host</dd>
 * <dt>display mode</dt>
 * <dd>the display mode can be specified by the parameter 'pages.mode' with the
 * values 'NONE', 'EDIT', 'PREVIEW', 'BROWSE' or 'DEVELOP'
 * if the request is received via an authoring host</dd>
 * <dt>locale (language)</dt>
 * <dd>the locale is specified by the parameter 'pages.locale' with
 * normal Locale values, e.g. 'en_EN_US', 'de_DE', 'fr'</dd>
 * </dl>
 * Each aspect value specified by a parameter is kept in the session and reused in further request.
 * </p>
 */
@Component(
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Adapter factory for all request aspects - access mode, display mode, language",
                AdapterFactory.ADAPTER_CLASSES + "=com.composum.sling.platform.security.AccessMode",
                AdapterFactory.ADAPTER_CLASSES + "=com.composum.pages.commons.request.DisplayMode",
                AdapterFactory.ADAPTER_CLASSES + "=com.composum.pages.commons.request.PagesLocale",
                AdapterFactory.ADAPTABLE_CLASSES + "=org.apache.sling.api.SlingHttpServletRequest"
        })
public class RequestAspectAdapterFactory implements AdapterFactory {

    private static final Logger LOG = LoggerFactory.getLogger(RequestAspectAdapterFactory.class);

    public static final String REQUEST_ASPECTS_PREFIX = "pages_request_aspect#";

    protected final Map<String, AspectFactory> factoryMap;

    public RequestAspectAdapterFactory() {
        factoryMap = new HashMap<>();
        factoryMap.put(AccessMode.class.getName(), new AccessModeFactory());
        factoryMap.put(DisplayMode.class.getName(), new DisplayModeFactory());
        factoryMap.put(PagesLocale.class.getName(), new PagesLocaleFactory());
    }

    @Reference
    protected PagesConfiguration pagesConfiguration;

    @Override
    public <AdapterType> AdapterType getAdapter(@Nonnull Object adaptable, @Nonnull Class<AdapterType> type) {
        AdapterType result = null;
        if (adaptable instanceof SlingHttpServletRequest) {
            SlingHttpServletRequest request = (SlingHttpServletRequest) adaptable;
            String typeName = type.getName();
            AspectFactory factory = factoryMap.get(typeName);
            String key = factory.getKey();
            result = type.cast(factory.getFromRequest(request, key));
            if (result == null) {
                result = getSessionAttribute(request, factory, key, null);
                if (result == null) {
                    result = type.cast(factory.createFromRequest(request, key));
                    if (result != null) {
                        HttpSession session = request.getSession(true);
                        factory.keepInSession(session, key, result);
                    }
                }
                if (result != null) {
                    request.setAttribute(key, result);
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug("adaptTo({}): {}", type.getSimpleName(), result);
                }
            }
        }
        return result;
    }

    protected interface AspectFactory<Type> {

        String getKey();

        @Nullable
        Type getFromRequest(@Nonnull SlingHttpServletRequest request, @Nonnull String key);

        @Nullable
        Type getFromSession(@Nonnull SlingHttpServletRequest request, @Nonnull HttpSession session, @Nonnull String key);

        @Nullable
        Type createFromRequest(@Nonnull SlingHttpServletRequest request, @Nonnull String key);

        void keepInSession(@Nonnull HttpSession session, @Nonnull String key, @Nonnull Object value);
    }

    /**
     * this factory is adapting to the instance controlled by the platform access manager only
     * - the name of the enum value is stored in request / session
     */
    protected class AccessModeFactory implements AspectFactory<AccessMode> {

        @Override
        public String getKey() {
            return ACCESS_MODE_ATTR;
        }

        @Override
        @Nullable
        public AccessMode getFromRequest(@Nonnull SlingHttpServletRequest request, @Nonnull String key) {
            return request.getParameter(ACCESS_MODE_REQ_PARAM) == null
                    ? AccessMode.accessModeValue(request.getAttribute(key))
                    : null;
        }

        @Override
        @Nullable
        public AccessMode getFromSession(@Nonnull SlingHttpServletRequest request, @Nonnull HttpSession session, @Nonnull String key) {
            return AccessMode.accessModeValue(session.getAttribute(key));
        }

        /**
         * this should always be done by the platform access manager
         * - but the platform access manager can be switched off...
         */
        @Override
        @Nonnull
        public AccessMode createFromRequest(@Nonnull SlingHttpServletRequest request, @Nonnull String key) {
            AccessMode accessMode = AccessMode.PUBLIC;
            String parameter = request.getParameter(ACCESS_MODE_REQ_PARAM);
            if (StringUtils.isNotBlank(parameter)) {
                accessMode = Objects.requireNonNull(AccessMode.accessModeValue(parameter, accessMode));
            }
            return accessMode;
        }

        @Override
        public void keepInSession(@Nonnull HttpSession session, @Nonnull String key, @Nonnull Object value) {
            session.setAttribute(key, ((AccessMode) value).name());
            if (LOG.isDebugEnabled()) {
                LOG.debug("keepInSession({}): {}", AccessMode.class.getSimpleName(), value);
            }
        }
    }

    /**
     * the display mode is a stack object stored in the request and modified during request rendering
     * - the session does not store this aspect
     * - he PagesFrameServlet is storing another value of the same type and key in the session for the frames display mode
     */
    protected class DisplayModeFactory implements AspectFactory<DisplayMode> {

        @Override
        public String getKey() {
            return DISPLAY_MODE_ATTR;
        }

        @Override
        @Nullable
        public DisplayMode getFromRequest(@Nonnull SlingHttpServletRequest request, @Nonnull String key) {
            return (DisplayMode) request.getAttribute(key);
        }

        @Override
        @Nullable
        public DisplayMode getFromSession(@Nonnull SlingHttpServletRequest request, @Nonnull HttpSession session, @Nonnull String key) {
            return null;
        }

        @Override
        @Nonnull
        public DisplayMode createFromRequest(@Nonnull SlingHttpServletRequest request, @Nonnull String key) {
            String parameter = request.getParameter(DISPLAY_MODE_VIEW_PARAM);
            DisplayMode.Value value = DisplayMode.Value.displayModeValue(parameter, DisplayMode.Value.NONE);
            return new DisplayMode(value);
        }

        @Override
        public void keepInSession(@Nonnull HttpSession session, @Nonnull String key, @Nonnull Object value) {
        }
    }

    /**
     * the locale is stored if specified as is in the request and also in the session for further requests
     */
    protected class PagesLocaleFactory implements AspectFactory<PagesLocale> {

        @Override
        public String getKey() {
            return RA_PAGES_LOCALE;
        }

        @Override
        @Nullable
        public PagesLocale getFromRequest(@Nonnull SlingHttpServletRequest request, @Nonnull String key) {
            return (PagesLocale) request.getAttribute(key);
        }

        @Override
        @Nullable
        public PagesLocale createFromRequest(@Nonnull SlingHttpServletRequest request, @Nonnull String key) {
            Locale locale = request.getLocale();
            String parameter = request.getParameter(LOCALE_REQUEST_PARAM);
            if (StringUtils.isNotBlank(parameter)) {
                locale = getLocale(parameter, locale);
            }
            return locale != null ? new PagesLocale(locale) : null;
        }

        @Override
        @Nullable
        public PagesLocale getFromSession(@Nonnull SlingHttpServletRequest request, @Nonnull HttpSession session, @Nonnull String key) {
            PagesLocale result = null;
            if (request.getParameter(LOCALE_REQUEST_PARAM) == null && !Page.isPage(request.getResource())) {
                Locale locale = (Locale) session.getAttribute(key);
                if (locale != null) {
                    result = new PagesLocale(locale);
                }
            }
            return result;
        }

        @Override
        public void keepInSession(@Nonnull HttpSession session, @Nonnull String key, @Nonnull Object value) {
            session.setAttribute(key, ((PagesLocale) value).getLocale());
            if (LOG.isDebugEnabled()) {
                LOG.debug("keepInSession({}): {}", Locale.class.getSimpleName(), value);
            }
        }

        /**
         * transforms a string value into the best matching Locale instance
         */
        public Locale getLocale(@Nonnull String rule, @Nonnull final Locale defaultValue) {
            Locale locale = defaultValue;
            if (StringUtils.isNotBlank(rule)) {
                rule = rule.trim().replaceAll(" +", "_").replaceAll("-", "_");
                String[] values = StringUtils.split(rule.trim(), '_');
                switch (values.length) {
                    case 0:
                        break;
                    case 1:
                        String country = defaultValue.getCountry();
                        if (StringUtils.isBlank(country)) {
                            country = pagesConfiguration.getPreferredCountry(values[0]);
                        }
                        locale = new Locale(values[0], country);
                        break;
                    case 2:
                        locale = new Locale(values[0], values[1]);
                        break;
                    default:
                        locale = new Locale(values[0], values[1], values[2]);
                        break;
                }
            }
            return locale;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getSessionAttribute(SlingHttpServletRequest request, AspectFactory factory,
                                            String key, T defaultValue) {
        T value = null;
        HttpSession session = request.getSession(false);
        if (session != null) {
            try {
                value = factory != null
                        ? (T) factory.getFromSession(request, session, key)
                        : (T) session.getAttribute(key);
            } catch (ClassCastException ccex) {
                // often happening after deployment - ignored... reset to a new instance
            }
        }
        return value != null ? value : defaultValue;
    }
}
