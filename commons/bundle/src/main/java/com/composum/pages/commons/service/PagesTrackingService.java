package com.composum.pages.commons.service;

import com.composum.pages.commons.model.Page;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.servlet.http.Cookie;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.GregorianCalendar;

@Component(
        label = "Composum Pages Tracking Service",
        immediate = true,
        metatype = false
)
@Service
public class PagesTrackingService implements TrackingService {

    public static final String STATS_NODE_NAME = "cpp:statistics";
    public static final String STATS_NODE_TYPE = "cpp:Statistics";
    public static final String STATS_DATA_TYPE = "cpp:StatsData";

    public static final String PAGE_STATS_PATH = "y-%04d/m-%02d/d-%02d";
    public static final String PAGE_STATS_HOUR = "h-%02d";
    public static final String INTERMEDIATE_TYPE = "nt:unstructured";

    private static final Logger LOG = LoggerFactory.getLogger(PagesTrackingService.class);

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

    @Reference
    ResourceResolverFactory resolverFactory;

    public void trackToken(BeanContext context,
                           String path,
                           String referer)
            throws RepositoryException, LoginException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("trackToken(" + path + ")");
        }
        ResourceResolver resolver = resolverFactory.getServiceResourceResolver(null);
        try {
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
        } finally {
            resolver.close();
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

            Resource refererStats = null;
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

            // use a cookie to detect repeated requests from the same client (daily cookie)
            Cookie cookie = request.getCookie(PAGE_TOKEN_COOKIE);
            if (cookie == null) {

                // count 'unique' requests if no cookie found
                hourlyValues.put(PROP_UNIQUE, hourlyValues.get(PROP_UNIQUE, 0) + 1);
                if (refererStatsValues != null) {
                    refererStatsValues.put(PROP_UNIQUE, refererStatsValues.get(PROP_UNIQUE, 0) + 1);
                }

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
        Resource statsData = ResourceUtil.getOrCreateChild(
                dayStatistics,
                relativePath,
                STATS_DATA_TYPE);
        return statsData;
    }
}
