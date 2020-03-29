package com.composum.pages.commons.service;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.util.ResolverUtil;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.platform.staging.query.Query;
import com.composum.sling.platform.staging.query.QueryBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingException;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import static com.composum.sling.clientlibs.handle.Clientlib.PROP_CATEGORY;
import static com.composum.sling.clientlibs.handle.Clientlib.RESOURCE_TYPE;

@Component(
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Theme Manager"
        }
)
public class PagesThemeManager implements ThemeManager {

    private static final Logger LOG = LoggerFactory.getLogger(PagesThemeManager.class);

    public static final long COLLECT_CACHE_TIME = /*2L * 60L */ 60L * 1000L;   // 2 hours

    public static final String PN_OVERLAYS = "overlays";

    @Reference
    protected ResourceResolverFactory resolverFactory;

    /**
     * the theme declared by a repository resource
     */
    public class ThemeConfiguration implements Theme {

        protected class PatternOverlay {

            protected final String overlay;
            protected final List<Pattern> patterns;

            public PatternOverlay(String overlay, List<Pattern> patterns) {
                this.overlay = overlay;
                this.patterns = patterns;
            }

            public String getOverlay() {
                return overlay;
            }

            public boolean matches(String category) {
                for (Pattern pattern : patterns) {
                    if (pattern.matcher(category).matches()) {
                        return true;
                    }
                }
                return false;
            }
        }

        protected final String name;
        protected final String path;
        protected final ValueMap properties;

        private transient List<PatternOverlay> resourceTypes;
        private transient List<PatternOverlay> pageTemplates;
        private transient List<PatternOverlay> clientlibs;

        public ThemeConfiguration(String name, Resource config) {
            this.name = name;
            this.path = config.getPath();
            this.properties = new ValueMapDecorator(new HashMap<>(config.getValueMap()));
        }

        @Nonnull
        @Override
        public String getName() {
            return name;
        }

        @Nonnull
        public String getPath() {
            return path;
        }

        @Nonnull
        @Override
        public String getTitle() {
            return properties.get(ResourceUtil.JCR_TITLE, getName());
        }

        @Nonnull
        @Override
        public String getResourceType(@Nonnull final Resource resource,
                                      @Nonnull final String resourceType) {
            return getOverlay(getResourceTypes(), resourceType);
        }

        @Nonnull
        @Override
        public String getPageTemplate(@Nonnull final Resource pageResource,
                                      @Nonnull final String templatePath) {
            return getOverlay(getPageTemplates(), templatePath);
        }

        @Nonnull
        @Override
        public String getClientlibCategory(@Nonnull final Resource pageResource,
                                           @Nonnull final String clientlibCategory) {
            return getOverlay(getClientlibs(), clientlibCategory);
        }

        protected String getOverlay(List<PatternOverlay> aspect, String value) {
            for (PatternOverlay pattern : aspect) {
                if (pattern.matches(value)) {
                    return pattern.getOverlay();
                }
            }
            return value;
        }

        protected List<PatternOverlay> getResourceTypes() {
            if (resourceTypes == null) {
                initialize();
            }
            return resourceTypes;
        }

        protected List<PatternOverlay> getPageTemplates() {
            if (pageTemplates == null) {
                initialize();
            }
            return pageTemplates;
        }

        protected List<PatternOverlay> getClientlibs() {
            if (clientlibs == null) {
                initialize();
            }
            return clientlibs;
        }

        protected void initialize() {
            resourceTypes = new ArrayList<>();
            pageTemplates = new ArrayList<>();
            clientlibs = new ArrayList<>();
            try (ResourceResolver resolver = resolverFactory.getServiceResourceResolver(null)) {
                collectOverlays(resolver.getResource(getPath()));
            } catch (LoginException ex) {
                LOG.error(ex.toString());
            }
        }

        protected void collectOverlays(Resource resource) {
            if (resource != null) {
                ResourceResolver resolver = resource.getResourceResolver();
                ValueMap values = resource.getValueMap();
                List<Pattern> patterns;
                String value;
                if (resource.isResourceType(RESOURCE_TYPE)) {
                    patterns = getPatterns(values);
                    if (patterns.size() > 0) {
                        String[] category = values.get(PROP_CATEGORY, new String[0]);
                        if (category.length > 0) {
                            clientlibs.add(new PatternOverlay(category[0], patterns));
                        }
                    }
                    return; // stop recursion
                } else if (values.get(JcrConstants.JCR_PRIMARYTYPE).equals(PagesConstants.NODE_TYPE_PAGE)
                        && (patterns = getPatterns(values)).size() > 0) {
                    Resource pageContent = resource.getChild(JcrConstants.JCR_CONTENT);
                    if (pageContent != null &&
                            pageContent.getValueMap().get(PagesConstants.PROP_IS_TEMPLATE, Boolean.TRUE)) {
                        pageTemplates.add(new PatternOverlay(
                                ResolverUtil.toPageTemplate(resolver, resource.getPath()), patterns));
                    }
                    return; // stop recursion
                } else if (values.get(JcrConstants.JCR_PRIMARYTYPE).equals(ResourceUtil.TYPE_SLING_FOLDER)
                        && (patterns = getPatterns(values)).size() > 0) {
                    resourceTypes.add(new PatternOverlay(
                            ResolverUtil.toResourceType(resolver, resource.getPath()), patterns));
                }
                for (Resource child : resource.getChildren()) {
                    collectOverlays(child);
                }
            }
        }

        protected List<Pattern> getPatterns(ValueMap values) {
            List<Pattern> patterns = new ArrayList<>();
            for (String rule : values.get(PN_OVERLAYS, new String[0])) {
                if (StringUtils.isNotBlank(rule)) {
                    patterns.add(Pattern.compile(rule.trim()));
                }
            }
            return patterns;
        }
    }

    protected long lastCollection = 0;
    protected Map<String, Theme> themes = new ConcurrentHashMap<>();

    @Reference(
            service = Theme.class,
            policy = ReferencePolicy.DYNAMIC,
            cardinality = ReferenceCardinality.MULTIPLE
    )
    protected void addTheme(@Nonnull final Theme theme) {
        LOG.info("addTheme: {}", theme.getName());
        themes.put(theme.getName(), theme);
    }

    protected void removeTheme(@Nonnull final Theme theme) {
        LOG.info("removeTheme: {}", theme.getName());
        themes.remove(theme.getName());
    }

    @Nonnull
    @Override
    public Collection<Theme> getThemes(@Nonnull final ResourceResolver resolver) {
        List<Theme> result = new ArrayList<>();
        for (Theme theme : getThemesMap().values()) {
            if (accessGranted(resolver, theme)) {
                result.add(theme);
            }
        }
        Collections.sort(result);
        return result;
    }

    @Nullable
    @Override
    public Theme getTheme(@Nonnull final ResourceResolver resolver,
                          @Nonnull final String name) {
        Theme theme = getThemesMap().get(name);
        return accessGranted(resolver, theme) ? theme : null;
    }

    protected boolean accessGranted(ResourceResolver resolver, Theme theme) {
        return !(theme instanceof ThemeConfiguration)
                || resolver.getResource(((ThemeConfiguration) theme).getPath()) != null;
    }

    protected Map<String, Theme> getThemesMap() {
        synchronized (this) {
            if (lastCollection < System.currentTimeMillis() - COLLECT_CACHE_TIME) {
                collectThemes();
            }
        }
        return themes;
    }

    protected void collectThemes() {
        try (ResourceResolver resolver = resolverFactory.getServiceResourceResolver(null)) {
            lastCollection = System.currentTimeMillis();
            HashSet<String> removedConfig = new HashSet<>();
            for (String name : themes.keySet()) {
                Theme theme = themes.get(name);
                if (theme instanceof ThemeConfiguration) {
                    themes.remove(name);
                    removedConfig.add(name);
                }
            }
            QueryBuilder queryBuilder = resolver.adaptTo(QueryBuilder.class);
            if (queryBuilder != null) {
                for (String root : resolver.getSearchPath()) {
                    Query query = queryBuilder.createQuery().path(root).type("cpp:Theme");
                    try {
                        for (Resource config : query.execute()) {
                            String name = config.getPath().substring(root.length());
                            if (!themes.containsKey(name)) {
                                if (removedConfig.contains(name)) {
                                    LOG.info("refreshTheme: {}", name);
                                    removedConfig.remove(name);
                                } else {
                                    LOG.info("addTheme: {}", name);
                                }
                                themes.put(name, new ThemeConfiguration(name, config));
                            }
                        }
                    } catch (SlingException ex) {
                        LOG.error("On path {} : {}", root, ex.toString(), ex);
                    }
                }
            }
            for (String name : removedConfig) {
                LOG.info("removeTheme: {}", name);
            }
        } catch (LoginException ex) {
            LOG.error(ex.toString());
        }
    }
}
