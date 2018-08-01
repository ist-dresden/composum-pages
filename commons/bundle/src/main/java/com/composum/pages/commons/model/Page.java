package com.composum.pages.commons.model;

import com.composum.pages.commons.model.properties.Language;
import com.composum.pages.commons.model.properties.Languages;
import com.composum.pages.commons.request.DisplayMode;
import com.composum.pages.commons.request.PagesLocale;
import com.composum.pages.commons.service.PageManager;
import com.composum.platform.models.annotations.DetermineResourceStategy;
import com.composum.platform.models.annotations.PropertyDetermineResourceStrategy;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.util.LinkUtil;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.platform.security.AccessMode;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.composum.pages.commons.PagesConstants.DEFAULT_EDIT_CATEGORY;
import static com.composum.pages.commons.PagesConstants.DEFAULT_VIEW_CATEGORY;
import static com.composum.pages.commons.PagesConstants.LANGUAGE_CSS_KEY;
import static com.composum.pages.commons.PagesConstants.LANGUAGE_NAME_SEPARATOR;
import static com.composum.pages.commons.PagesConstants.NODE_TYPE_PAGE;
import static com.composum.pages.commons.PagesConstants.NODE_TYPE_PAGE_CONTENT;
import static com.composum.pages.commons.PagesConstants.PAGES_PREFIX;
import static com.composum.pages.commons.PagesConstants.PROP_EDIT_CATEGORY;
import static com.composum.pages.commons.PagesConstants.PROP_PAGE_LANGUAGES;
import static com.composum.pages.commons.PagesConstants.PROP_VIEW_CATEGORY;

@PropertyDetermineResourceStrategy(Page.ContainingPageResourceStrategy.class)
public class Page extends ContentDriven<PageContent> implements Comparable<Page> {

    public static final String DISPLAY_MODE_CSS_CLASS = PAGES_PREFIX + "display-mode";

    // static resource type determination

    /**
     * check the 'cpp:Page' type for a resource
     */
    public static boolean isPage(Resource resource) {
        return ResourceUtil.isResourceType(resource, NODE_TYPE_PAGE);
    }

    /**
     * check the 'cpp:PageContent' type for a resource
     */
    public static boolean isPageContent(Resource resource) {
        return ResourceUtil.isResourceType(resource, NODE_TYPE_PAGE_CONTENT);
    }

    // specified languages for a page

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
    }

    // child pages filter base

    public static class DefaultPageFilter implements ResourceFilter {

        protected final BeanContext context;
        protected final Locale locale;
        protected final Language language;

        public DefaultPageFilter(BeanContext context) {
            this.context = context;
            locale = context.getRequest().adaptTo(PagesLocale.class).getLocale();
            language = Languages.get(context).getLanguage(locale);
        }

        protected Page isAcceptedPage(Resource resource) {
            if (Page.isPage(resource)) {
                Page page = context.getService(PageManager.class).createBean(context, resource);
                if (page.isValid()) {
                    if (language != null) {
                        PageLanguages pageLanguages = page.getPageLanguages();
                        if (!pageLanguages.contains(language)) {
                            return null;
                        }
                    }
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

    private transient PageLanguages pageLanguages;
    private transient Boolean isDefaultLangPage;
    private transient Page defaultLanguagePage;

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
        return getName().compareTo(page.getName());
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
        return getResourceManager().isTemplate(this.getResource());
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
            Resource parentRes = isHome() ? resource : resource.getParent();
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

    /**
     * @return the language subset of the sites languages supported by this page
     */
    public PageLanguages getPageLanguages() {
        if (pageLanguages == null) {
            pageLanguages = new PageLanguages();
            Languages declaredLanguages = getLanguages();
            if (declaredLanguages != null) {
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
        }
        return pageLanguages;
    }

    /**
     * @return 'true' if this page can render the content of the default language
     */
    public boolean isDefaultLanguagePage() {
        if (isDefaultLangPage == null) {
            PageLanguages pageLanguages = getPageLanguages();
            isDefaultLangPage = pageLanguages.contains(getLanguages().getDefaultLanguage());
        }
        return isDefaultLangPage;
    }

    /**
     * @return the Page to use to build the general URL of the page even if this page is language split
     */
    public Page getDefaultLanguagePage() {
        if (defaultLanguagePage == null) {
            if (!isDefaultLanguagePage()) {
                String baseName = StringUtils.substringBeforeLast(getName(), LANGUAGE_NAME_SEPARATOR);
                for (Resource sibling : getResource().getParent().getChildren()) {
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

    // date / time properties

    public String getLastModifiedString() {
        return getContent().getLastModifiedString();
    }

    // rendering

    /**
     * @return the URL to reference this page;
     * this can be the URL of the default language sibling if this page itself is a language variation instance
     */
    public String getUrl() {
        return LinkUtil.getUrl(getContext().getRequest(), getDefaultLanguagePage().getPath());
    }

    public String getHtmlLangAttribute() {
        Language language = getLanguage();
        if (language != null) {
            return "lang=\"" + language.getKey() + "\"";
        }
        return "";
    }

    public String getHtmlDirAttribute() {
        Language language = getLanguage();
        if (language != null) {
            String dir = language.getDirection();
            if (StringUtils.isNotBlank(dir)) {
                return "dir=\"" + dir + "\"";
            }
        }
        return "";
    }

    public String getHtmlClasses() {
        return StringUtils.join(collectHtmlClasses(new ArrayList<String>()), " ");
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
}
