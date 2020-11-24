/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.model;

import com.composum.pages.commons.model.properties.Language;
import com.composum.pages.commons.model.properties.LanguageSet;
import com.composum.pages.commons.model.properties.Languages;
import com.composum.pages.commons.request.DisplayMode;
import com.composum.pages.commons.service.PageManager;
import com.composum.pages.commons.service.Theme;
import com.composum.pages.commons.service.ThemeManager;
import com.composum.pages.commons.util.LinkUtil;
import com.composum.pages.commons.util.LinkUtil.Parameters;
import com.composum.pages.commons.util.UrlMap;
import com.composum.platform.models.annotations.DetermineResourceStategy;
import com.composum.platform.models.annotations.PropertyDetermineResourceStrategy;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.util.I18N;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.platform.security.AccessMode;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;

import static com.composum.pages.commons.PagesConstants.DEFAULT_EDIT_CATEGORY;
import static com.composum.pages.commons.PagesConstants.DEFAULT_VIEW_CATEGORY;
import static com.composum.pages.commons.PagesConstants.LANGUAGE_CSS_KEY;
import static com.composum.pages.commons.PagesConstants.LOCALE_REQUEST_PARAM;
import static com.composum.pages.commons.PagesConstants.META_NODE_NAME;
import static com.composum.pages.commons.PagesConstants.META_PATH_PATTERN;
import static com.composum.pages.commons.PagesConstants.META_ROOT_PATH;
import static com.composum.pages.commons.PagesConstants.NODE_TYPE_PAGE;
import static com.composum.pages.commons.PagesConstants.NODE_TYPE_PAGE_CONTENT;
import static com.composum.pages.commons.PagesConstants.PAGES_PREFIX;
import static com.composum.pages.commons.PagesConstants.PN_SUBTITLE;
import static com.composum.pages.commons.PagesConstants.PROP_EDIT_CATEGORY;
import static com.composum.pages.commons.PagesConstants.PROP_PAGE_LANGUAGES;
import static com.composum.pages.commons.PagesConstants.PROP_SLING_TARGET;
import static com.composum.pages.commons.PagesConstants.PROP_THEME;
import static com.composum.pages.commons.PagesConstants.PROP_VIEW_CATEGORY;
import static java.lang.Boolean.FALSE;

@PropertyDetermineResourceStrategy(Page.ContainingPageResourceStrategy.class)
public class Page extends ContentDriven<PageContent> implements Comparable<Page> {

    private static final Logger LOG = LoggerFactory.getLogger(Page.class);

    public static final String LOGO_PATH = "logo";
    public static final String PN_IMAGE_REF = "imageRef";

    public static final String DISPLAY_MODE_CSS_CLASS = PAGES_PREFIX + "display-mode";

    // static resource type determination

    /**
     * check the 'cpp:Page' type for a resource
     */
    public static boolean isPage(@Nullable final Resource resource) {
        return ResourceUtil.isResourceType(resource, NODE_TYPE_PAGE);
    }

    /**
     * check the 'cpp:PageContent' type for a resource
     */
    public static boolean isPageContent(@Nullable final Resource resource) {
        return ResourceUtil.isResourceType(resource, NODE_TYPE_PAGE_CONTENT);
    }

    // child pages filter base

    public static class DefaultPageFilter extends ResourceFilter.AbstractResourceFilter {

        protected final BeanContext context;

        public DefaultPageFilter(BeanContext context) {
            this.context = context;
        }

        protected Page isAcceptedPage(Resource resource) {
            if (Page.isPage(resource)) {
                Page page = context.getService(PageManager.class).createBean(context, resource);
                if (page.isValid()) {
                    return page;
                }
            }
            return null;
        }

        @Override
        public boolean accept(Resource resource) {
            return isAcceptedPage(resource) != null;
        }

        @Override
        public boolean isRestriction() {
            return true;
        }

        @Override
        public void toString(@Nonnull StringBuilder builder) {
            builder.append(getClass().getSimpleName());
        }
    }

    //

    /**
     * the set of the languages edited within this page and the language root status of the page
     */
    public class PageLanguages {

        protected final Languages languages;
        protected String[] languageKeys;
        protected Boolean isLanguageRoot;
        protected Boolean isLanguageSplit;
        protected LanguageSet languageSet;

        public PageLanguages() {
            languages = Page.this.getLanguages();
            languageKeys = Page.this.getProperty(PROP_PAGE_LANGUAGES, null, new String[0]);
            isLanguageRoot = (languageKeys.length > 0);
            isLanguageSplit = FALSE;
            if (!isLanguageRoot) {
                languageKeys = getInherited(PROP_PAGE_LANGUAGES, null, new String[0]);
            }
            if (languageKeys.length > 0) {
                for (String key : languageKeys) {
                    Language language = languages.getLanguage(key);
                    if (language != null) {
                        if (languageSet == null) {
                            languageSet = new LanguageSet() {
                                @Nonnull
                                @Override
                                public Language getDefaultLanguage() {
                                    return size() > 0 ? values().iterator().next() : languages.getDefaultLanguage();
                                }
                            };
                            isLanguageSplit = Boolean.TRUE;
                        }
                        languageSet.put(language.getKey(), language);
                    }
                }
            }
            if (languageSet == null) {
                languageSet = languages.getLanguageSet();
                isLanguageRoot = Boolean.FALSE;
            }
        }

        public boolean contains(Language language) {
            return languageSet.find(language.getKey()) != null;
        }

        public String[] getLanguageKeys() {
            return languageKeys;
        }

        public LanguageSet getLanguageSet() {
            return languageSet;
        }

        public boolean isLanguageRoot() {
            return isLanguageRoot;
        }

        public boolean isLanguageSplit() {
            return isLanguageSplit;
        }

        public Language getDefaultLanguage() {
            return languageSet.size() > 0 ? languageSet.values().iterator().next() : languages.getDefaultLanguage();
        }

        public Collection<Language> getLanguages() {
            return languageSet.values();
        }

        @Nullable
        public Language getLanguage(@Nonnull final String key) {
            return languageSet.get(key);
        }
    }

    // page attributes

    private transient Site site;
    private transient Page parent;
    private transient List<Page> pagesPath;

    private transient String subtitle;

    private transient Image logo;
    private transient String logoUrl;

    private transient Boolean canonicalRequest;
    private transient String canonicalUrl;
    private transient String targetUrl;

    private transient Language language;
    private transient PageLanguages languages;

    private transient Theme theme;
    private transient String themeName;

    private transient Resource metaData;

    public Page() {
    }

    protected Page(BeanContext context, Resource resource) {
        initialize(context, resource);
    }

    public Page(PageManager manager, BeanContext context, Resource resource) {
        setPageManager(manager);
        initialize(context, resource);
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    /**
     * @return the path; supports the use of a page as is (maybe null) in a JSP expression
     */
    @Override
    public String toString() {
        return getPath();
    }

    @Override
    public int compareTo(@Nonnull Page page) {
        CompareToBuilder builder = new CompareToBuilder();
        builder.append(getName(), page.getName());
        builder.append(getPath(), page.getPath());
        return builder.toComparison();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Page && compareTo((Page) other) == 0;
    }

    // initializer extensions

    /**
     * Compatible to Page{@link AbstractModel#determineResource(Resource)}.
     */
    public static class ContainingPageResourceStrategy implements DetermineResourceStategy {
        @Override
        public Resource determineResource(BeanContext beanContext, Resource requestResource) {
            return beanContext.getService(PageManager.class).getContainingPageResource(requestResource);
        }
    }

    @Override
    @Nullable
    protected Resource determineResource(@Nullable Resource initialResource) {
        return initialResource != null ? getPageManager().getContainingPageResource(initialResource) : null;
    }

    @Nonnull
    @Override
    protected PageContent createContentModel(BeanContext context, Resource contentResource) {
        return new PageContent(context, contentResource);
    }

    // page hierarchy

    public Site getSite() {
        if (site == null) {
            site = getSiteManager().getContainingSite(this);
        }
        return site;
    }

    public String getSiteRelativePath() {
        final Site containingSite = getSite();
        final String siteRoot = containingSite.getPath() + "/";
        final String path = getPath();
        return path.startsWith(siteRoot) ? "./" + path.substring(siteRoot.length()) : path;
    }

    public boolean isTemplate() {
        return getResourceManager().isTemplate(getContext(), this.getResource());
    }

    @Nonnull
    public String getLogoUrl() {
        if (logoUrl == null) {
            logoUrl = "";
            Image logo = getLogo();
            if (logo != null) {
                logoUrl = logo.getAssetUrl();
            }
        }
        return logoUrl;
    }

    @Nullable
    public Image getLogo() {
        if (logo == null) {
            Resource logoRes = findInherited(LOGO_PATH, PN_IMAGE_REF);
            if (logoRes != null) {
                logo = new Image(context, logoRes);
            }
        }
        return logo;
    }

    /**
     * find an inherited content node; check optional for a property of that node
     *
     * @param path     the path relative to the 'jcr:content' content node of a page
     * @param property optional; a property name (or path) - this property must be not empty
     * @return the found resource of 'null'
     */
    @Nullable
    public Resource findInherited(@Nonnull final String path, @Nullable final String property) {
        Page page = this;
        Resource resource = null;
        while (page != null && (
                (resource = page.getContent().getResource().getChild(path)) == null ||
                        (property != null && StringUtils.isEmpty(resource.getValueMap().get(property, String.class))))) {
            page = page.getParentPage();
        }
        if (page == null) { // if page is 'null' the property was not found; try the site
            Site sie = getSite();
            if (site != null) {
                resource = site.getContent().getResource().getChild(path);
            }
        }
        return resource;
    }

    /**
     * returns 'true' it this page is the homepage ('isHomepage' would overlap with 'getHomepage' in a template)
     */
    public boolean isHome() {
        return getHomepage().getPath().equals(getPath());
    }

    /**
     * returns the homepage of the site which contains this page
     */
    public Homepage getHomepage() {
        Site site = getSite();
        return site != null ? site.getHomepage(getLocale()) : new Homepage(context, resource);
    }

    /**
     * returns the parent page if this page is not the homepage, otherwise this page itself
     */
    public Page getParentPage() {
        if (parent == null) {
            Resource parentRes = resource.getParent();
            while (parent == null && parentRes != null) {
                if (isPage(parentRes)) {
                    parent = new Page(context, parentRes);
                } else {
                    parentRes = Site.isSite(parentRes) ? null : parentRes.getParent();
                }
            }
        }
        return parent;
    }

    public Collection<Page> getPagesPath() {
        if (pagesPath == null) {
            pagesPath = new ArrayList<>();
            Page parent = this;
            while ((parent = parent.getParentPage()) != null) {
                pagesPath.add(0, parent);
            }
        }
        return pagesPath;
    }

    /**
     * returns a list of all subpages which are accepted by the filter
     */
    public List<Page> getChildPages(ResourceFilter filter) {
        List<Page> children = new ArrayList<>();
        for (Resource child : resource.getChildren()) {
            if (Page.isPage(child) && filter.accept(child)) {
                Page page = new Page(context, child);
                children.add(page);
            }
        }
        return children;
    }

    public List<Page> getChildPages() {
        return getChildPages(new DefaultPageFilter(getContext()));
    }

    // I18N (language split)

    public boolean isLanguageRoot() {
        return getPageLanguages().isLanguageRoot();
    }

    public boolean isLanguageSplit() {
        return getPageLanguages().isLanguageSplit();
    }

    public boolean isLanguageSplitLocked() {
        return isLanguageSplit() && !isLanguageRoot();
    }

    @Nonnull
    public String[] getLanguageKeys() {
        return getPageLanguages().getLanguageKeys();
    }

    @Nonnull
    public PageLanguages getPageLanguages() {
        if (languages == null) {
            languages = new PageLanguages();
        }
        return languages;
    }

    /**
     * @return the language of the requested page
     */
    @Override
    @Nonnull
    public Language getLanguage() {
        if (language == null) {
            PageLanguages languages = getPageLanguages();
            SlingHttpServletRequest request = getContext().getRequest();
            String requestedLang = request.getParameter(LOCALE_REQUEST_PARAM);
            if (StringUtils.isNotBlank(requestedLang)) {
                language = languages.getLanguage(requestedLang);
            }
            if (language == null) {
                language = languages.getDefaultLanguage();
            }
        }
        return language;
    }

    // properties

    public String getSubtitle() {
        if (subtitle == null) {
            subtitle = getProperty(PN_SUBTITLE, "");
        }
        return subtitle;
    }

    @Override
    public String getTypeHint() {
        return getContent().getTypeHint();
    }

    public String getLastModifiedString() {
        return getContent().getLastModifiedString();
    }

    // settings

    public <T> T getSettingsProperty(String key, Locale locale, Class<T> type) {
        return getSite().getContent().getSettingsProperty(key, locale, type);
    }

    public <T> T getSettingsProperty(String key, Locale locale, T defaultValue) {
        return getSite().getContent().getSettingsProperty(key, locale, defaultValue);
    }

    // metaData

    public Resource getMetaData() {
        if (metaData == null) {
            metaData = getResource().getResourceResolver().resolve(getMetaDataPath(getPath()) + "/" + META_NODE_NAME);
        }
        return metaData;
    }

    public static String getMetaDataPath(String pagePath) {
        Matcher metaPath = META_PATH_PATTERN.matcher(pagePath);
        return META_ROOT_PATH + (metaPath.matches() ? metaPath.group(1) : "");
    }

    // rendering

    /**
     * @return 'true' if the current requests URL is equal to this pages canonical URL
     */
    public boolean isCanonicalRequest() {
        if (canonicalRequest == null) {
            SlingHttpServletRequest request = getContext().getRequest();
            String query = request.getQueryString();
            StringBuffer url = request.getRequestURL();
            if (StringUtils.isNotBlank(query)) {
                url.append("?").append(query);
            }
            canonicalRequest = getCanonicalUrl().equals(url.toString());
        }
        return canonicalRequest;
    }

    /**
     * @return the full qualified (external) URL of this page
     */
    @Nonnull
    public String getCanonicalUrl() {
        if (canonicalUrl == null) {
            canonicalUrl = LinkUtil.getAbsoluteUrl(getContext().getRequest(), getUrl());
        }
        return canonicalUrl;
    }

    /**
     * @return the pages URL - the URL in the context of the pages language - the canonical URL
     */
    @Nonnull
    @Override
    public String getUrl() {
        if (url == null) {
            url = getUrl(getLanguage(), false, null);
        }
        return url;
    }

    /**
     * @return a dynamic helper map to decorate the page url with selectors specified by the a maps key
     */
    public Map<String, String> getUrls() {
        return new UrlMap(new UrlMap.Builder() {
            @Nonnull
            @Override
            public String buildUrl(@Nonnull String selectors) {
                return getUrl(getLanguage(), false, selectors);
            }
        });
    }

    @Nonnull
    public String getUrl(boolean preserveParameters) {
        if (url == null) {
            url = getUrl(getLanguage(), preserveParameters, null);
        }
        return url;
    }

    /**
     * @return the pages URL in the context of the given language
     * - decorated with the locale URL parameter if the language is not the default language
     */
    @Nonnull
    public String getUrl(@Nonnull final Language language, boolean preserveParameters,
                         @Nullable final String selectors) {
        SlingHttpServletRequest request = context.getRequest();
        String pageUrl = LinkUtil.getUrl(request, getPath(), selectors, null);
        Parameters parameters = preserveParameters ? new Parameters(request) : new Parameters();
        if (language.equals(getPageLanguages().getDefaultLanguage())) {
            parameters.remove(LOCALE_REQUEST_PARAM);
        } else {
            parameters.set(LOCALE_REQUEST_PARAM, language.getKey());
        }
        pageUrl += parameters.toString();
        return pageUrl;
    }

    @Nonnull
    public String getHtmlLangAttribute() {
        Language language = getLanguage();
        return "lang=\"" + language.getKey() + "\"";
    }

    @Nonnull
    public String getHtmlDirAttribute() {
        Language language = getLanguage();
        String dir = language.getDirection();
        return StringUtils.isNotBlank(dir) ? "dir=\"" + dir + "\"" : "";
    }

    @Nonnull
    public String getHtmlClasses() {
        return StringUtils.join(collectHtmlClasses(new ArrayList<>()), " ");
    }

    @Nonnull
    protected List<String> collectHtmlClasses(@Nonnull final List<String> classes) {
        SlingHttpServletRequest request = context.getRequest();
        AccessMode accessMode = AccessMode.requestMode(request);
        if (AccessMode.AUTHOR == accessMode) {
            DisplayMode.Value displayMode = DisplayMode.requested(context);
            if (displayMode != null) {
                if (displayMode == DisplayMode.Value.DEVELOP) {
                    displayMode = DisplayMode.Value.EDIT;
                }
                classes.add(DISPLAY_MODE_CSS_CLASS + "_" + displayMode.name());
            }
        }
        String languageKey = getLanguageKey();
        if (StringUtils.isNotBlank(languageKey)) {
            classes.add(LANGUAGE_CSS_KEY + "_" + languageKey);
        }
        return classes;
    }

    // theme and clientlibs

    @Nullable
    public Theme getTheme() {
        if (theme == null && themeName == null) {
            themeName = getInherited(PROP_THEME, "");
            if (StringUtils.isNotBlank(themeName)) {
                theme = context.getService(ThemeManager.class).getTheme(context.getResolver(), themeName);
            }
        }
        return theme;
    }

    /**
     * @return the topmost paqe with the same theme configured, maybe the homepage
     */
    @Nonnull
    public Page getThemeRoot() {
        Theme theme = getTheme();
        if (theme != null) {
            Page parent, themeRoot = this;
            while ((parent = themeRoot.getParentPage()) != null) {
                Theme parentTheme = parent.getTheme();
                if (parentTheme == null || !parentTheme.getName().equals(theme.getName())) {
                    break;
                }
                themeRoot = parent;
            }
            return themeRoot;
        } else {
            return getHomepage();
        }
    }

    @Nonnull
    public Map<String, String> getThemes() {
        Collection<Theme> themes = context.getService(ThemeManager.class).getThemes(context.getResolver());
        Map<String, String> result = new LinkedHashMap<>();
        result.put("", I18N.get(context.getRequest(), "default"));
        for (Theme theme : themes) {
            result.put(theme.getName(), theme.getTitle());
        }
        result.put("none", I18N.get(context.getRequest(), "no theme"));
        return result;
    }

    @Override
    public String determineTemplatePath() {
        String templatePath = super.determineTemplatePath();
        if (StringUtils.isNotBlank(templatePath)) {
            Theme theme = getTheme();
            if (theme != null) {
                templatePath = theme.getPageTemplate(getResource(), templatePath);
            }
        }
        return templatePath;
    }

    @Nonnull
    public String getViewClientlibCategory() {
        String category = getInherited(PROP_VIEW_CATEGORY, DEFAULT_VIEW_CATEGORY);
        Theme theme = getTheme();
        if (theme != null) {
            category = theme.getClientlibCategory(getResource(), category);
        }
        return category;
    }

    @Nonnull
    public String getEditClientlibCategory() {
        String category = getInherited(PROP_EDIT_CATEGORY, DEFAULT_EDIT_CATEGORY);
        Theme theme = getTheme();
        if (theme != null) {
            category = theme.getClientlibCategory(getResource(), category);
        }
        return category;
    }

    // forward / redirect

    public String getSlingTarget() {
        return getProperty(PROP_SLING_TARGET, "");
    }

    public String getSlingTargetUrl() {
        if (targetUrl == null) {
            String target = getSlingTarget();
            if (StringUtils.isNotBlank(target)) {
                Page page = getPageManager().getPage(getContext(), target);
                if (page != null) {
                    targetUrl = page.getUrl();
                } else {
                    SlingHttpServletRequest request = context.getRequest();
                    targetUrl = LinkUtil.getUrl(request, target);
                }
            } else {
                targetUrl = "";
            }
        }
        return targetUrl;
    }
}
