package com.composum.pages.commons.request;

import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.service.SiteManager;
import com.composum.sling.core.BeanContext;
import com.composum.sling.platform.security.AccessMode;
import com.composum.sling.platform.staging.ReleaseChangeEventPublisher;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.composum.pages.commons.PagesConstants.COMPOSUM_PREFIX;
import static com.composum.platform.commons.request.service.InternalRequestService.RA_IS_INTERNAL_REQUEST;
import static com.composum.sling.platform.staging.impl.ResourceResolverChangeFilter.ATTR_CPM_RELEASE;
import static com.composum.sling.platform.staging.impl.ResourceResolverChangeFilter.ATTR_CPM_VERSION;
import static com.composum.sling.platform.staging.impl.ResourceResolverChangeFilter.PARAM_CPM_RELEASE;
import static com.composum.sling.platform.staging.impl.ResourceResolverChangeFilter.PARAM_CPM_VERSION;

@Component(
        service = {Filter.class},
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Release Filter",
                "sling.filter.scope=REQUEST",
                "service.ranking:Integer=" + 5070
        }
)
@Designate(ocd = PagesReleaseFilter.Config.class)
public class PagesReleaseFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(PagesReleaseFilter.class);

    public static final String PUBLIC_RELEASE_LABEL = "public";

    public static final String ATTR_CACHE_DISABLED = "com.composum.platform.cache.component.ComponentCacheService#cacheDisabled";

    @ObjectClassDefinition(
            name = "Composum Pages Release Filter Configuration",
            description = "Enables Configures for which URLs a release has to be chosen according to the host's access mode."
    )
    @interface Config {

        @AttributeDefinition(
                name = "enabled",
                description = "the on/off switch for the Release Filter"
        )
        boolean release_filter_enabled() default true;

        @AttributeDefinition(
                name = "unreleased Hosts",
                description = "the hostname patterns for general (unreleased) artifacts"
        )
        String[] unreleased_host_allow() default {};

        @AttributeDefinition(
                name = "unreleased URIs",
                description = "the URI patterns for general (unreleased) artifacts"
        )
        String[] unreleased_uri_allow() default {
                "^/(apps|libs)/.+$",
                "^/bin/public/.+$",
                "^/j_security_check$"
        };

        @AttributeDefinition(
                name = "unreleased Paths",
                description = "the path patterns for general (unreleased) artifacts"
        )
        String[] unreleased_path_allow() default {
                "^/(apps|libs)/.+$",
                "^/libs/sling/servlet/errorhandler/.+$",
                "^/libs/(themes|fonts|jslibs|composum|sling)/.+$",
                "^/libs(/composum/platform/security)?/login.*$",
                "^/libs/composum/pages/home(/.*)?$",
                "^/content/shared/.*$"
        };
    }

    private PagesReleaseFilter.Config config;

    private List<Pattern> ignoredHostPatterns;

    private List<Pattern> ignoredUriPatterns;

    private List<Pattern> ignoredPathPatterns;

    @Reference
    protected SiteManager siteManager;

    @Reference
    protected ReleaseChangeEventPublisher releaseChangeEventPublisher;

    protected ServletContext servletContext;

    protected BundleContext bundleContext;

    /**
     * Determines the release to render depending on the access mode...
     * <dl>
     * <dt>'inPlace' staging mode is set for the requested site</dt>
     * <dd>is checking that the requested path matches to the replication path; rejects the request if this doesn't match</dd>
     * <dt>'versions' staging mode is set for the requested site</dt>
     * <dd>is setting the 'composum-platform-release-label' the the release label declared for the requested access mode</dd>
     * <dt>'live' staging mode is set for the requested site</dt>
     * <dd>no check, render author content as is as public content</dd>
     * </dl>
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        if (config.release_filter_enabled()) {
            String release = null;

            SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
            Resource resource = slingRequest.getResource();
            BeanContext context = new BeanContext.Servlet(servletContext, bundleContext, slingRequest,
                    (SlingHttpServletResponse) response);

            Boolean isInternalRequest = (Boolean) request.getAttribute(RA_IS_INTERNAL_REQUEST);
            if (isInternalRequest == null) {
                isInternalRequest = false;
            }

            Site site = siteManager.getContainingSite(context, resource);

            AccessMode accessMode = AccessMode.requestMode(slingRequest);
            if (accessMode != null && accessMode != AccessMode.AUTHOR) {

                // not an AUTHOR access; check public/preview access against the staging policy of the site...

                String uri = ((SlingHttpServletRequest) request).getRequestURI();
                String contextPath = slingRequest.getContextPath();
                if (uri.startsWith(contextPath)) {
                    uri = uri.substring(contextPath.length());
                }

                if (!isInternalRequest &&
                        !isUnreleasedPublicAccessAllowed(request.getServerName(), uri, resource.getPath())) {

                    if (site != null) {
                        String mode = site.getPublicMode();
                        switch (mode) {
                            case Site.PUBLIC_MODE_IN_PLACE:
                                String replicationRoot = this.releaseChangeEventPublisher.getStagePath(site.getResource(), accessMode.name().toLowerCase());
                                if (replicationRoot != null) {
                                    String path = resource.getPath();
                                    if (!path.startsWith(replicationRoot + "/")) {
                                        sendReject(response, "not a released resource", uri, resource);
                                        return;
                                    }
                                    if (LOG.isDebugEnabled()) {
                                        LOG.debug("replication: '{}' ('{}')", accessMode + "@" + site.getPath(), resource);
                                    }
                                } else {
                                    sendReject(response, "no replication configured", uri, resource);
                                    return;
                                }
                                break;
                            case Site.PUBLIC_MODE_VERSIONS:
                                release = site.getReleaseNumber(accessMode.name().toLowerCase());
                                if (StringUtils.isBlank(release)) {
                                    sendReject(response, "no appropriate release found", uri, resource);
                                    return;
                                }
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("release: '{}' ({})", release + "@" + site.getPath(), resource);
                                }
                                break;
                        }
                    } else {
                        sendReject(response, "no appropriate site found", uri, resource);
                        return;
                    }
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("unreleased: '{}' ({})", uri, resource);
                    }
                }

            } else {

                // if Author or unspecific mode use requested release...

                release = request.getParameter(PARAM_CPM_RELEASE);
                if (StringUtils.isBlank(release)) {
                    HttpSession session = slingRequest.getSession(false);
                    if (session != null) {
                        release = (String) session.getAttribute(ATTR_CPM_RELEASE);
                    }
                }

                if (StringUtils.isNotBlank(release)) {
                    if (!release.startsWith(COMPOSUM_PREFIX)) {
                        if (site != null) {
                            // see whether that's an access mode / mark for the requested release
                            String mapped = site.getReleaseNumber(release);
                            if (StringUtils.isNotBlank(mapped)) {
                                release = mapped;
                            }
                        }
                    }
                } else {

                    // for version compare a version of a page can be requested...
                    String version = request.getParameter(PARAM_CPM_VERSION);

                    if (StringUtils.isNotBlank(version)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("{} access mode, version '{}' selected", accessMode, version);
                        }
                        request.setAttribute(ATTR_CPM_VERSION, version);
                        // disable editing for release requests
                        Objects.requireNonNull(slingRequest.adaptTo(DisplayMode.class)).reset(DisplayMode.Value.NONE);
                    }
                }
            }

            if (StringUtils.isNotBlank(release)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("{} access mode, release '{}' selected", accessMode, release);
                }
                request.setAttribute(ATTR_CPM_RELEASE, release);
                // disable editing for release requests
                Objects.requireNonNull(slingRequest.adaptTo(DisplayMode.class)).reset(DisplayMode.Value.NONE);
            }

            // disable component caching if in edit context ('edit', 'preview', 'browse', 'develop')
            if (DisplayMode.isEditMode(context) || DisplayMode.isPreviewMode(context)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("EDIT mode, no cache for '{}'", resource.getPath());
                }
                slingRequest.setAttribute(ATTR_CACHE_DISABLED, Boolean.TRUE);
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("chain.doFilter()...");
        }
        chain.doFilter(request, response);
    }

    @SuppressWarnings("Duplicates")
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

    @Override
    public void init(FilterConfig filterConfig) {
        servletContext = filterConfig.getServletContext();
    }

    @Override
    public void destroy() {
    }

    @Activate
    @Modified
    public void activate(final PagesReleaseFilter.Config config, final BundleContext bundleContext) {
        this.config = config;
        this.bundleContext = bundleContext;
        ignoredHostPatterns = new ArrayList<>();
        for (String rule : PropertiesUtil.toStringArray(config.unreleased_host_allow())) {
            if (StringUtils.isNotBlank(rule = rule.trim())) ignoredHostPatterns.add(Pattern.compile(rule));
        }
        ignoredUriPatterns = new ArrayList<>();
        for (String rule : PropertiesUtil.toStringArray(config.unreleased_uri_allow())) {
            if (StringUtils.isNotBlank(rule = rule.trim())) ignoredUriPatterns.add(Pattern.compile(rule));
        }
        ignoredPathPatterns = new ArrayList<>();
        for (String rule : PropertiesUtil.toStringArray(config.unreleased_path_allow())) {
            if (StringUtils.isNotBlank(rule = rule.trim())) ignoredPathPatterns.add(Pattern.compile(rule));
        }
    }
}
