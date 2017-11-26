package com.composum.pages.commons.request;

import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.service.SiteManager;
import com.composum.sling.core.BeanContext;
import com.composum.sling.platform.security.PlatformAccessFilter;
import com.composum.sling.platform.staging.ResourceResolverChangeFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.PropertyUnbounded;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingFilter;
import org.apache.felix.scr.annotations.sling.SlingFilterScope;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static com.composum.pages.commons.PagesConstants.COMPOSUM_PREFIX;
import static com.composum.sling.platform.security.PlatformAccessFilter.ACCESS_MODE_KEY;
import static com.composum.sling.platform.staging.ResourceResolverChangeFilter.COOKIE_NAME;
import static com.composum.sling.platform.staging.ResourceResolverChangeFilter.PARAMETER_NAME;

@SlingFilter(
        label = "Composum Platform Release Filter",
        description = "a servlet filter to select the label for the requested release",
        scope = {SlingFilterScope.REQUEST},
        order = 5070,
        metatype = true)
public class PagesReleaseFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(PagesReleaseFilter.class);

    public static final String ATTR_CACHE_DISABLED = "com.composum.platform.cache.component.ComponentCacheService#cacheDisabled";

    public static final String FILTER_ENABLED = "release.filter.enabled";
    @Property(
            name = FILTER_ENABLED,
            label = "enabled",
            description = "the on/off switch for the Release Filter",
            boolValue = true
    )
    private boolean enabled;

    public static final String IGNORED_HOST_PATTERNS = "unreleased.host.allow";
    @Property(
            name = IGNORED_HOST_PATTERNS,
            label = "Ignored Hosts",
            description = "the hostname patterns for general (unreleased) artifacts",
            value = {""},
            unbounded = PropertyUnbounded.ARRAY
    )
    private List<Pattern> ignoredHostPatterns;

    public static final String IGNORED_URI_PATTERNS = "unreleased.uri.allow";
    @Property(
            name = IGNORED_URI_PATTERNS,
            label = "Ignored URIs",
            description = "the URI patterns for general (unreleased) artifacts",
            value = {"^/(apps|libs)/.*\\.(css|js|jpg|jpeg|gif|png|ttf|woff)$",
                    "^/j_security_check$"},
            unbounded = PropertyUnbounded.ARRAY
    )
    private List<Pattern> ignoredUriPatterns;

    public static final String IGNORED_PATH_PATTERNS = "unreleased.path.allow";
    @Property(
            name = IGNORED_PATH_PATTERNS,
            label = "Ignored Paths",
            description = "the path patterns for general (unreleased) artifacts",
            value = {"^/(apps|libs)/.*\\.(css|js|jpg|jpeg|gif|png|ttf|woff)$",
                    "^/libs/sling/servlet/errorhandler/.*$",
                    "^/libs/(themes|fonts|jslibs|composum|sling)/.*$",
                    "^/libs(/composum/platform/security)?/login.*$"},
            unbounded = PropertyUnbounded.ARRAY
    )
    private List<Pattern> ignoredPathPatterns;

    @Reference
    protected SiteManager siteManager;

    protected ServletContext servletContext;

    protected BundleContext bundleContext;

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        if (enabled) {
            String release = null;

            SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
            Resource resource = slingRequest.getResource();
            BeanContext context = new BeanContext.Servlet(servletContext, bundleContext, slingRequest,
                    (SlingHttpServletResponse) response);

            Site site = siteManager.getContainingSite(context, resource);

            String accessMode = (String) request.getAttribute(ACCESS_MODE_KEY);
            if (StringUtils.isNotBlank(accessMode) &&
                    !PlatformAccessFilter.AccessMode.AUTHOR.name().equals(accessMode.toUpperCase())) {

                String uri = ((SlingHttpServletRequest) request).getRequestURI();
                String contextPath = slingRequest.getContextPath();
                if (uri.startsWith(contextPath)) {
                    uri = uri.substring(contextPath.length());
                }
                String path = resource.getPath();

                if (!isUnreleasedPublicAccessAllowed(request.getServerName(), uri, path)) {

                    if (site != null) {
                        Site.PublicMode mode = site.getPublicMode();
                        if (mode != Site.PublicMode.LIVE) {
                            release = site.getReleaseLabel(mode.name());
                            if (StringUtils.isBlank(release)) {
                                sendReject(response, "no appropriate release found", uri, resource);
                                return;
                            }
                        }
                    } else {
                        sendReject(response, "no appropriate site found", uri, resource);
                        return;
                    }
                }

            } else {

                // if Author or unspecific mode use requested release...
                release = request.getParameter(PARAMETER_NAME);
                if (StringUtils.isBlank(release)) {
                    Cookie cookie = ((SlingHttpServletRequest) request).getCookie(COOKIE_NAME);
                    if (cookie != null) {
                        release = cookie.getValue();
                    }
                }
                if (StringUtils.isNotBlank(release)) {
                    if (Character.isDigit(release.charAt(0))) {
                        release = Site.RELEASE_LABEL_PREFIX + release;
                    } else {
                        if (site != null && !release.startsWith(COMPOSUM_PREFIX)) {
                            String mapped = site.getReleaseLabel(release);
                            if (StringUtils.isNotBlank(mapped)) {
                                release = mapped;
                            }
                        }
                    }
                }
            }

            if (StringUtils.isNotBlank(release)) {
                LOG.debug ("LIVE mode, release '{}' selected", release);
                request.setAttribute(ResourceResolverChangeFilter.ATTRIBUTE_NAME, release);
                request.setAttribute(DisplayMode.ATTRIBUTE_KEY, DisplayMode.create(DisplayMode.Value.NONE));
            }

            // disable component caching if in edit mode
            if (DisplayMode.isEditMode(context)) {
                LOG.debug ("EDIT mode, no cache for '{}'", resource.getPath());
                slingRequest.setAttribute(ATTR_CACHE_DISABLED, Boolean.TRUE);
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("chain.doFilter()...");
        }
        chain.doFilter(request, response);
    }

    protected boolean isUnreleasedPublicAccessAllowed(String hostname, String uri, String path) {
        for (Pattern pattern : ignoredHostPatterns) {
            if (pattern.matcher(hostname).matches()) {
                return true;
            }
        }
        for (Pattern pattern : ignoredUriPatterns) {
            if (pattern.matcher(uri).matches()) {
                return true;
            }
        }
        for (Pattern pattern : ignoredPathPatterns) {
            if (pattern.matcher(path).matches()) {
                return true;
            }
        }
        return false;
    }

    protected void sendReject(ServletResponse response, String message, String uri, Resource resource)
            throws IOException {
        HttpServletResponse slingResponse = (HttpServletResponse) response;
        LOG.warn("REJECT(release): '" + uri + "' - " + message + " (" + resource + ").");
        slingResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        servletContext = filterConfig.getServletContext();
    }

    public void destroy() {
    }

    @Activate
    @Modified
    public void activate(ComponentContext context) {
        bundleContext = context.getBundleContext();
        ignoredHostPatterns = new ArrayList<>();
        ignoredUriPatterns = new ArrayList<>();
        ignoredPathPatterns = new ArrayList<>();
        try {
            Dictionary<String, Object> properties = context.getProperties();
            enabled = PropertiesUtil.toBoolean(properties.get(FILTER_ENABLED), true);
            for (String rule : PropertiesUtil.toStringArray(properties.get(IGNORED_HOST_PATTERNS))) {
                try {
                    if (StringUtils.isNotBlank(rule = rule.trim())) ignoredHostPatterns.add(Pattern.compile(rule));
                } catch (PatternSyntaxException e) {
                    LOG.error("Broken " + IGNORED_HOST_PATTERNS + " pattern ignored: ''{}'' - {}", rule, e);
                }
            }
            for (String rule : PropertiesUtil.toStringArray(properties.get(IGNORED_URI_PATTERNS))) {
                try {
                    if (StringUtils.isNotBlank(rule = rule.trim())) ignoredUriPatterns.add(Pattern.compile(rule));
                } catch (PatternSyntaxException e) {
                    LOG.error("Broken " + IGNORED_URI_PATTERNS + " pattern ignored: ''{}'' - {}", rule, e);
                }
            }
            for (String rule : PropertiesUtil.toStringArray(properties.get(IGNORED_PATH_PATTERNS))) {
                try {
                    if (StringUtils.isNotBlank(rule = rule.trim())) ignoredPathPatterns.add(Pattern.compile(rule));
                } catch (PatternSyntaxException e) {
                    LOG.error("Broken " + IGNORED_PATH_PATTERNS + " pattern ignored: ''{}'' - {}", rule, e);
                }
            }
        } catch (RuntimeException e) {
            LOG.error("Error in activation of " + getClass() + " , configuration for hosts is reset, removing public " +
                    "access : " + e, e);
            ignoredHostPatterns.clear();
        }
    }
}
