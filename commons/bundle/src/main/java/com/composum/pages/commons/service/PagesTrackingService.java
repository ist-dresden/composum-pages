package com.composum.pages.commons.service;

import com.composum.pages.commons.model.Page;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

@Component(
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Tracking Service"
        }
)
@Designate(ocd = PagesTrackingService.Config.class)
public class PagesTrackingService implements TrackingService {

    public static final String STATS_NODE_NAME = "cpp:statistics";
    public static final String STATS_NODE_TYPE = "cpp:Statistics";
    public static final String STATS_DATA_TYPE = "cpp:StatsData";

    public static final String PAGE_STATS_PATH = "y-%04d/m-%02d/d-%02d";
    public static final String PAGE_STATS_HOUR = "h-%02d";
    public static final String INTERMEDIATE_TYPE = "nt:unstructured";

    public static final String SA_SESSION_TRACE = SessionTrace.class.getName();

    private static final Logger LOG = LoggerFactory.getLogger(PagesTrackingService.class);

    @ObjectClassDefinition(name = "Composum Pages Tracking Service Configuration",
            description = "Configurations for the Composum Pages Tracking Service")
    public @interface Config {

        @AttributeDefinition(name = "Cookie Policy", options = {
                @Option(label = "Session", value = "session"),
                @Option(label = "Tracking", value = "tracking")
        })
        String cookiePolicy() default "session";

        @AttributeDefinition()
        String webconsole_configurationFactory_nameHint() default
                "{name} (cookiePolicy: {cookiePolicy})";
    }

    public static class TokenRequest {

        public final BeanContext context;
        public final Resource resource;
        public final String referer;
        public final Calendar timestamp;
        public final int year;
        public final int month;
        public final int day;
        public final int hour;

        public TokenRequest(BeanContext context,
                            Resource resource,
                            String referer) {
            this.context = context;
            this.resource = resource;
            this.referer = referer;
            timestamp = new GregorianCalendar();
            timestamp.setTime(new Date());
            year = timestamp.get(Calendar.YEAR);
            month = timestamp.get(Calendar.MONTH) + 1;
            day = timestamp.get(Calendar.DAY_OF_MONTH);
            hour = timestamp.get(Calendar.HOUR_OF_DAY);
        }

        public String toString() {
            return resource.getPath();
        }
    }

    public static class SessionTrace implements Serializable {

        protected class TraceToken implements Serializable {

            public final String path;
            public final Calendar timestamp;

            public TraceToken(TokenRequest request) {
                path = request.resource.getPath();
                timestamp = request.timestamp;
            }
        }

        protected final List<TraceToken> trace = new LinkedList<>();

        public void addRequest(TokenRequest request) {
            trace.add(new TraceToken(request));
        }

        public boolean isNewRequest(TokenRequest request) {
            String path = request.resource.getPath();
            for (int index = trace.size(); --index >= 0; ) {
                if (path.equals(trace.get(index).path)) {
                    return false;
                }
            }
            return true;
        }
    }

    @Reference
    protected ResourceResolverFactory resolverFactory;

    protected Config config;

    public void trackToken(BeanContext context,
                           String path,
                           String referer)
            throws LoginException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("trackToken(" + path + ")");
        }
        try (ResourceResolver resolver = resolverFactory.getServiceResourceResolver(null)) {
            Resource resource = resolver.getResource(path);
            if (resource != null) {
                if (Page.isPage(resource)) {
                    trackPage(new TokenRequest(context, resource, referer));
                } else if (Page.isPageContent(resource)) {
                    resource = resource.getParent();
                    if (Page.isPage(resource)) {
                        trackPage(new TokenRequest(context, resource, referer));
                    } else {
                        LOG.error("invalid page content: " + path);
                    }
                } else {
                    LOG.warn("unexpected token: " + path);
                }
                resolver.commit();
            } else {
                LOG.error("resource not found: " + path);
            }
        } catch (PersistenceException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    protected void trackPage(TokenRequest token) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("trackPage(" + token + ")");
        }
        try {
            SlingHttpServletRequest request = token.context.getRequest();

            Resource statistics = getStatistics(token.context, token.resource);
            Resource contentStats = getStatsData(statistics, token, "jcr:content");

            // count page assess for each hour of a day
            String hourlyPath = new Formatter().format(PAGE_STATS_HOUR, token.hour).toString();
            Resource hourlyStats = ResourceUtil.getOrCreateChild(
                    contentStats,
                    hourlyPath,
                    INTERMEDIATE_TYPE);

            ModifiableValueMap hourlyValues = hourlyStats.adaptTo(ModifiableValueMap.class);
            hourlyValues.put(PROP_TOTAL, hourlyValues.get(PROP_TOTAL, 0) + 1);

            Resource refererStats;
            ModifiableValueMap refererStatsValues = null;

            // count page access requests from one referer (daily count)
            if (StringUtils.isNotBlank(token.referer)) {
                String refererUrl = new String(Base64.decodeBase64(token.referer.getBytes()), CHARSET);
                refererStats = ResourceUtil.getOrCreateChild(
                        contentStats,
                        "referer/" + token.referer,
                        INTERMEDIATE_TYPE);
                refererStatsValues = refererStats.adaptTo(ModifiableValueMap.class);
                refererStatsValues.put(PROP_URL, refererUrl);
                refererStatsValues.put(PROP_TOTAL, refererStatsValues.get(PROP_TOTAL, 0) + 1);
            }

            boolean isUnique = false;
            switch (config.cookiePolicy()) {
                case "tracking":
                    // use a cookie to detect repeated requests from the same client (daily cookie)
                    Cookie cookie = request.getCookie(PAGE_TOKEN_COOKIE);
                    if (cookie == null) {
                        // count 'unique' requests if no cookie found
                        isUnique = true;

                        // set cookie to avoid multiple counts for the same day
                        String uri = request.getRequestURI();
                        if (!uri.endsWith(EXT_PNG)) {
                            uri = uri.substring(0, uri.lastIndexOf("/")); // ignore referer suffix
                        }
                        cookie = new Cookie(PAGE_TOKEN_COOKIE,
                                token.year + "-" + token.month + "-" + token.day + ":" + token.hour);
                        cookie.setPath(uri);
                        cookie.setMaxAge((25 - token.hour) * 3600); // expires near the end of the day
                        token.context.getResponse().addCookie(cookie);
                    }
                    break;
                case "session":
                default:
                    // use session instead of special cookies (one 'technical' session cookie only)
                    HttpSession session = request.getSession(true);
                    SessionTrace trace = (SessionTrace) session.getAttribute(SA_SESSION_TRACE);
                    if (trace == null) {
                        trace = new SessionTrace();
                        session.setAttribute(SA_SESSION_TRACE, trace);
                    }

                    if (trace.isNewRequest(token)) {
                        // count 'unique' requests if resource not found in session trace
                        isUnique = true;
                    }
                    trace.addRequest(token);
                    break;
            }

            // count 'unique' requests
            if (isUnique) {
                hourlyValues.put(PROP_UNIQUE, hourlyValues.get(PROP_UNIQUE, 0) + 1);
                if (refererStatsValues != null) {
                    refererStatsValues.put(PROP_UNIQUE, refererStatsValues.get(PROP_UNIQUE, 0) + 1);
                }
            }

        } catch (RepositoryException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * returns the statistics resource of the containing page of the resource for statistics updates
     */
    protected Resource getStatistics(BeanContext context, Resource requested)
            throws RepositoryException {
        PageManager pageManager = context.getService(PageManager.class);
        Resource page = pageManager.getContainingPageResource(requested);
        Resource statistics = page.getChild(STATS_NODE_NAME);
        if (statistics == null) {
            statistics = ResourceUtil.getOrCreateChild(
                    page,
                    STATS_NODE_NAME,
                    STATS_NODE_TYPE);
        }
        return statistics;
    }

    /**
     * returns the resource to store statistics data for the given path; uses the token to build the 'calendar path'
     */
    protected Resource getStatsData(Resource statistics, TokenRequest token, String relativePath)
            throws RepositoryException {
        String pathToDay = new Formatter().format(PAGE_STATS_PATH,
                token.year, token.month, token.day).toString();
        Resource dayStatistics = ResourceUtil.getOrCreateChild(
                statistics,
                pathToDay,
                INTERMEDIATE_TYPE);
        @SuppressWarnings("UnnecessaryLocalVariable")
        Resource statsData = ResourceUtil.getOrCreateChild(
                dayStatistics,
                relativePath,
                STATS_DATA_TYPE);
        return statsData;
    }

    @Activate
    @Modified
    protected void activate(final Config config) {
        this.config = config;
    }
}
