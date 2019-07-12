/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.model;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.properties.Language;
import com.composum.pages.commons.model.properties.LanguageSet;
import com.composum.pages.commons.model.properties.Languages;
import com.composum.pages.commons.request.DisplayMode;
import com.composum.pages.commons.service.PageManager;
import com.composum.pages.commons.util.LinkUtil;
import com.composum.platform.models.annotations.DetermineResourceStategy;
import com.composum.platform.models.annotations.PropertyDetermineResourceStrategy;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.core.util.SlingResourceUtil;
import com.composum.sling.platform.security.AccessMode;
import com.composum.sling.platform.staging.versions.PlatformVersionsService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.RepositoryException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
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
import static com.composum.pages.commons.PagesConstants.PROP_VIEW_CATEGORY;
import static com.composum.pages.commons.PagesConstants.VERSION_DATE_FORMAT;

@PropertyDetermineResourceStrategy(Page.ContainingPageResourceStrategy.class)
public class Page extends ContentDriven<PageContent> implements Comparable<Page> {

    private static final Logger LOG = LoggerFactory.getLogger(Page.class);

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
        public void toString(StringBuilder builder) {
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
            isLanguageSplit = Boolean.FALSE;
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
            return languageSet.containsKey(language.getKey());
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
            return languageSet.getLanguages();
        }

        @Nullable
        public Language getLanguage(String key) {
            return languageSet.get(key);
        }
    }

    // page attributes

    private transient Site site;
    private transient Page parent;

    private transient String subtitle;

    private transient Language language;
    private transient PageLanguages languages;

    private transient StatusModel releaseStatus;
    private transient PlatformVersionsService versionsService;

    private transient Resource metaData;

    public Page() {
    }

    protected Page(BeanContext context, Resource resource) {
        initialize(context, resource);
    }

    public Page(PageManager manager, BeanContext context, Resource resource) {
        this.pageManager = manager;
        initialize(context, resource);
    }

    @Override
    public int compareTo(@Nonnull Page page) {
        CompareToBuilder builder = new CompareToBuilder();
        builder.append(getName(), page.getName());
        builder.append(getPath(), page.getPath());
        return builder.toComparison();
    }

    // initializer extensions

    /** Compatible to Page{@link #determineResource(Resource)}. */
    public static class ContainingPageResourceStrategy implements DetermineResourceStategy {
        @Override
        public Resource determineResource(BeanContext beanContext, Resource requestResource) {
            return beanContext.getService(PageManager.class).getContainingPageResource(requestResource);
        }
    }

    @Override
    protected Resource determineResource(Resource initialResource) {
        return getPageManager().getContainingPageResource(initialResource);
    }

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
        final String sitePath = containingSite.getPath();
        final String path = getPath();
        return path.replace(sitePath, ".");
    }

    public boolean isTemplate() {
        return getResourceManager().isTemplate(getContext(), this.getResource());
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
        return site != null ? site.getHomepage() : new Homepage(context, resource);
    }

    /**
     * returns the parent page if this page is not the homepage, otherwise this page itself
     */
    public Page getParentPage() {
        if (parent == null) {
            Resource parentRes = isHome() ? null : resource.getParent();
            while (parent == null && parentRes != null) {
                if (isPage(parentRes)) {
                    parent = new Page(context, parentRes);
                } else {
                    parentRes = parentRes.getParent();
                }
            }
        }
        return parent;
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
     * @return the pages URL - the URL in the context of the pages language - the canonical URL
     */
    @Nonnull
    @Override
    public String getUrl() {
        if (url == null) {
            url = getUrl(getLanguage());
        }
        return url;
    }

    /**
     * @return the pages URL in the context of the given language
     * - decorated with the locale URL parameter if the language is not the default language
     */
    @Nonnull
    public String getUrl(@Nonnull final Language language) {
        SlingHttpServletRequest request = context.getRequest();
        String pageUrl = LinkUtil.getUrl(request, getPath(), null, null);
        if (!language.equals(getPageLanguages().getDefaultLanguage())) {
            pageUrl += "?pages.locale=" + language.getKey();
        }
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

    @Nonnull
    public String getViewClientlibCategory() {
        return getInherited(PROP_VIEW_CATEGORY, DEFAULT_VIEW_CATEGORY);
    }

    @Nonnull
    public String getEditClientlibCategory() {
        return getInherited(PROP_EDIT_CATEGORY, DEFAULT_EDIT_CATEGORY);
    }

    // forward / redirect

    public String getSlingTarget() {
        return getProperty(PROP_SLING_TARGET, "");
    }

    public String getSlingTargetUrl() {
        String targetUrl = getSlingTarget();
        if (StringUtils.isNotBlank(targetUrl)) {
            SlingHttpServletRequest request = context.getRequest();
            targetUrl = LinkUtil.getUrl(request, targetUrl);
        }
        return targetUrl;
    }

    // release

    /** Pages-Adapter around {@link PlatformVersionsService.Status}. */
    public static class StatusModel {

        protected final PlatformVersionsService.Status releaseStatus;

        public StatusModel(PlatformVersionsService.Status status) {
            releaseStatus = status;
        }

        public PlatformVersionsService.ActivationState getActivationState() {
            return releaseStatus.getActivationState();
        }

        public String getLastModified() {
            Calendar lastModified = releaseStatus.getLastModified();
            return lastModified != null ? new SimpleDateFormat(VERSION_DATE_FORMAT).format(lastModified.getTime()) : "";
        }

        public String getLastModifiedBy() {
            return releaseStatus.getLastModifiedBy();
        }

        public String getReleaseLabel() {
            String label = releaseStatus.getRelease().getReleaseLabel();
            Matcher matcher = PagesConstants.RELEASE_LABEL_PATTERN.matcher(label);
            return matcher.matches() ? matcher.group(1) : label;
        }

        public String getLastActivated() {
            Calendar calendar = releaseStatus.getActivationInfo() != null ? releaseStatus.getActivationInfo().getLastActivated() : null;
            return calendar != null ? new SimpleDateFormat(VERSION_DATE_FORMAT).format(calendar.getTime()) : "";
        }

        public String getLastActivatedBy() {
            return releaseStatus.getActivationInfo() != null ? releaseStatus.getActivationInfo().getLastActivatedBy() : null;
        }

        public String getLastDeactivated() {
            Calendar calendar = releaseStatus.getActivationInfo() != null ? releaseStatus.getActivationInfo().getLastDeactivated() : null;
            return calendar != null ? new SimpleDateFormat(VERSION_DATE_FORMAT).format(calendar.getTime()) : "";
        }

        public String getLastDeactivatedBy() {
            return releaseStatus.getActivationInfo() != null ? releaseStatus.getActivationInfo().getLastDeactivatedBy() : null;
        }
    }

    public StatusModel getReleaseStatus() {
        if (releaseStatus == null) {
            try {
                PlatformVersionsService.Status status = getPlatformVersionsService().getStatus(getResource(), null);
                if (status == null) { // rare strange case - needs to be investigated.
                    LOG.warn("No release status for {}", SlingResourceUtil.getPath(getResource()));
                }
                releaseStatus = new StatusModel(status);
            } catch (RepositoryException ex) {
                LOG.error("Error calculating status for " + SlingResourceUtil.getPath(getResource()), ex);
            }
        }
        if (releaseStatus != null && releaseStatus.releaseStatus == null)
            return null;
        return releaseStatus;
    }

    protected PlatformVersionsService getPlatformVersionsService() {
        if (versionsService == null) {
            versionsService = context.getService(PlatformVersionsService.class);
        }
        return versionsService;
    }
}
