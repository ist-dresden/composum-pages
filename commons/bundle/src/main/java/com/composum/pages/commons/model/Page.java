/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.model;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.properties.Language;
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

    // specified languages for a page

    /* FIXME deprecated 'hidden language split'
    public static class PageLanguages extends ArrayList<Language> {

        @Override
        public int indexOf(Object object) {
            if (object instanceof Language) {
                String key = ((Language) object).getKey();
                for (int index = 0; index < size(); index++) {
                    if (get(index).getKey().equals(key)) {
                        return index;
                    }
                }
            }
            return -1;
        }
    }*/

    // child pages filter base

    public static class DefaultPageFilter extends ResourceFilter.AbstractResourceFilter {

        protected final BeanContext context;
        /* FIXME deprecated 'hidden language split'
        protected final Locale locale;
        protected final Language language;*/

        public DefaultPageFilter(BeanContext context) {
            this.context = context;
            /* FIXME deprecated 'hidden language split'
            locale = Objects.requireNonNull(context.getRequest().adaptTo(PagesLocale.class)).getLocale();
            language = Languages.get(context).getLanguage(locale);*/
        }

        protected Page isAcceptedPage(Resource resource) {
            if (Page.isPage(resource)) {
                Page page = context.getService(PageManager.class).createBean(context, resource);
                if (page.isValid()) {
                    /* FIXME deprecated 'hidden language split'
                    if (language != null) {
                        PageLanguages pageLanguages = page.getPageLanguages();
                        if (!pageLanguages.contains(language)) {
                            return null;
                        }
                    }*/
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

    // page attributes

    private transient Site site;
    private transient Page parent;

    private transient String subtitle;

    private transient Language language;

    private transient Boolean isLanguageRoot;
    private transient Boolean isLanguageSplit;
    private transient Collection<Language> pageLanguages;
    /* FIXME deprecated 'hidden language split'
    private transient PageLanguages pageLanguages;
    private transient Boolean isDefaultLangPage;
    private transient Page defaultLanguagePage;*/

    private transient StatusModel releaseStatus;
    private transient PlatformVersionsService versionsService;

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
                    parent = getPageManager().createBean(context, parentRes);
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
        getPageLanguages(); /* determines the value if not done always */
        return isLanguageRoot;
    }

    public boolean isLanguageSplit() {
        getPageLanguages(); /* determines the value if not done always */
        return isLanguageSplit;
    }

    public boolean isLanguageSplitLocked() {
        return isLanguageSplit() && !isLanguageRoot();
    }

    /**
     * @return the language of the requested page
     */
    @Override
    @Nonnull
    public Language getLanguage() {
        if (language == null) {
            if (isLanguageSplit()) {
                // i18n by language path split - use the language configured at the page
                language = getPageLanguages().iterator().next();
            } else {
                // i18n by property path split - use the locale URL parameter
                Languages languages = getLanguages();
                SlingHttpServletRequest request = getContext().getRequest();
                String requestedLang = request.getParameter(LOCALE_REQUEST_PARAM);
                if (StringUtils.isNotBlank(requestedLang)) {
                    language = languages.getLanguage(requestedLang);
                }
                if (language == null) {
                    language = languages.getDefaultLanguage();
                }
            }
        }
        return language;
    }

    @Nonnull
    public Collection<Language> getPageLanguages() {
        if (pageLanguages == null) {
            Languages languages = getLanguages();
            String[] languageKeys = getProperty(PROP_PAGE_LANGUAGES, null, new String[0]);
            isLanguageRoot = (languageKeys.length > 0);
            isLanguageSplit = Boolean.FALSE;
            if (!isLanguageRoot) {
                languageKeys = getInherited(PROP_PAGE_LANGUAGES, null, new String[0]);
            }
            if (languageKeys.length > 0) {
                for (String key : languageKeys) {
                    Language language = languages.getLanguage(key);
                    if (language != null) {
                        if (pageLanguages == null) {
                            pageLanguages = new ArrayList<>();
                            isLanguageSplit = Boolean.TRUE;
                        }
                        pageLanguages.add(language);
                    }
                }
            }
            if (pageLanguages == null) {
                pageLanguages = languages.getLanguageList();
                isLanguageRoot = Boolean.FALSE;
            }
        }
        return pageLanguages;
    }

    /* FIXME deprecated 'hidden language split'
    /**
     * @return the language subset of the sites languages supported by this page
     *
    public PageLanguages getPageLanguages() {
        if (pageLanguages == null) {
            pageLanguages = new PageLanguages();
            Languages declaredLanguages = getLanguages();
            String[] languages = getInherited(PROP_PAGE_LANGUAGES, null, String[].class);
            if (languages != null) {
                for (String key : languages) {
                    Language language = declaredLanguages.getLanguage(key);
                    if (language != null) {
                        pageLanguages.add(language);
                    }
                }
            } else {
                pageLanguages.addAll(declaredLanguages.getLanguageList());
            }
        }
        return pageLanguages;
    }

    /**
     * @return 'true' if this page can render the content of the default language
     *
    public boolean isDefaultLanguagePage() {
        if (isDefaultLangPage == null) {
            PageLanguages pageLanguages = getPageLanguages();
            isDefaultLangPage = pageLanguages.contains(getLanguages().getDefaultLanguage());
        }
        return isDefaultLangPage;
    }

    /**
     * @return the Page to use to build the general URL of the page even if this page is language split
     *
    public Page getDefaultLanguagePage() {
        if (defaultLanguagePage == null) {
            if (!isDefaultLanguagePage()) {
                String baseName = StringUtils.substringBeforeLast(getName(), LANGUAGE_NAME_SEPARATOR);
                for (Resource sibling : Objects.requireNonNull(getResource().getParent()).getChildren()) {
                    if (Page.isPage(sibling)) {
                        Page page = getPageManager().createBean(context, sibling);
                        if (page.isValid()
                                // searching for a sibling with the same 'base name' which can render the default language
                                && baseName.equals(StringUtils.substringBeforeLast(page.getName(), LANGUAGE_NAME_SEPARATOR))
                                && page.isDefaultLanguagePage()) {
                            defaultLanguagePage = page;
                        }
                    }
                }
            }
            if (defaultLanguagePage == null) {
                defaultLanguagePage = this;
            }
        }
        return defaultLanguagePage;
    }
    */

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

    // rendering

    /* FIXME deprecated 'hidden language split'
    /**
     * @return the URL to reference this page;
     * this can be the URL of the default language sibling if this page itself is a language variation instance
     *
    @Nonnull
    @Override
    public String getUrl() {
        return LinkUtil.getUrl(getContext().getRequest(), getDefaultLanguagePage().getPath());
    }*/
    @Nonnull
    @Override
    public String getUrl() {
        return getUrl(getLanguage());
    }

    @Nonnull
    public String getUrl(Language language) {
        String pageUrl = super.getUrl();
        if (!isLanguageSplit()) {
            if (!language.equals(getLanguages().getDefaultLanguage())) {
                pageUrl += "?pages.locale=" + language.getKey();
            }
        }
        return pageUrl;
    }

    public String getHtmlLangAttribute() {
        Language language = getLanguage();
        return "lang=\"" + language.getKey() + "\"";
    }

    public String getHtmlDirAttribute() {
        Language language = getLanguage();
        String dir = language.getDirection();
        return StringUtils.isNotBlank(dir) ? "dir=\"" + dir + "\"" : "";
    }

    public String getHtmlClasses() {
        return StringUtils.join(collectHtmlClasses(new ArrayList<>()), " ");
    }

    protected List<String> collectHtmlClasses(List<String> classes) {
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

    public String getViewClientlibCategory() {
        return getInherited(PROP_VIEW_CATEGORY, DEFAULT_VIEW_CATEGORY);
    }

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

    // releases

    public class StatusModel {

        protected final PlatformVersionsService.Status releaseStatus;

        public StatusModel() throws RepositoryException {
            releaseStatus = getPlatformVersionsService().getStatus(getResource(), null);
            if (releaseStatus == null) { // rare strange case - needs to be investigated.
                LOG.warn("No release status for {}", SlingResourceUtil.getPath(getResource()));
            }
        }

        public PlatformVersionsService.ActivationState getActivationState() {
            return releaseStatus.getActivationState();
        }

        public String getLastModified() {
            Calendar date = releaseStatus.getLastModified();
            return date != null ? new SimpleDateFormat(VERSION_DATE_FORMAT).format(date.getTime()) : "";
        }

        public String getLastModifiedBy() {
            return releaseStatus.getLastModifiedBy();
        }

        public String getReleaseLabel() {
            String label = releaseStatus.release().getReleaseLabel();
            Matcher matcher = PagesConstants.RELEASE_LABEL_PATTERN.matcher(label);
            return matcher.matches() ? matcher.group(1) : label;
        }

        public String getLastActivated() {
            Calendar calendar = releaseStatus.getLastActivated();
            return calendar != null ? new SimpleDateFormat(VERSION_DATE_FORMAT).format(calendar.getTime()) : "";
        }

        public String getLastActivatedBy() {
            return releaseStatus.getLastActivatedBy();
        }

        public String getLastDeactivated() {
            Calendar calendar = releaseStatus.getLastDeactivated();
            return calendar != null ? new SimpleDateFormat(VERSION_DATE_FORMAT).format(calendar.getTime()) : "";
        }

        public String getLastDeactivatedBy() {
            return releaseStatus.getLastDeactivatedBy();
        }
    }

    public StatusModel getReleaseStatus() {
        if (releaseStatus == null) {
            try {
                releaseStatus = new StatusModel();
            } catch (RepositoryException ex) {
                LOG.error(ex.getMessage(), ex);
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
