package com.composum.pages.commons.model;

import com.composum.pages.commons.PagesConstants;
import com.composum.pages.commons.model.properties.Language;
import com.composum.pages.commons.model.properties.Languages;
import com.composum.pages.commons.service.PageManager;
import com.composum.pages.commons.service.SiteManager;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * the model of a resource which is the root of a language split in the context of the current page
 * (the site with the configured site language set is used if no language split declared)
 * assuming that a language split root page has siblings for the other languages of a site;
 * the alternatives of the current page are searched in the siblings path relative to the language root
 */
public class LanguageRoot extends GenericModel {

    @Nullable
    public static Resource findLanguageRootResource(@Nonnull final BeanContext context,
                                                    @Nullable final Resource resource) {
        Resource rootRes = null;
        Page relatedPage = context.getAttribute(PagesConstants.RA_CURRENT_PAGE, Page.class);
        if (relatedPage == null) {
            PageManager pageManager = context.getService(PageManager.class);
            relatedPage = pageManager.getContainingPage(context, resource != null ? resource : context.getResource());
        }
        while (relatedPage != null && !relatedPage.isLanguageRoot()) {
            relatedPage = relatedPage.getParentPage();
        }
        if (relatedPage != null) {
            rootRes = relatedPage.getResource();
        } else {
            SiteManager siteManager = context.getService(SiteManager.class);
            Site site = siteManager.getContainingSite(context, resource != null ? resource : context.getResource());
            if (site != null) {
                rootRes = site.getResource();
            }
        }
        return rootRes;
    }

    public class Alternative {

        protected final Language language;
        protected final Page page;

        public Alternative(Language language, Page page) {
            this.language = language;
            this.page = page;
        }

        public Language getLanguage() {
            return language;
        }

        public Page getPage() {
            return page;
        }

        public String getUrl() {
            return page.getUrl(language);
        }
    }

    private transient Site site;
    private transient Page page;
    private transient Languages languages;

    private transient List<Alternative> alternatives;

    private transient Resource languageSiblingsParent;

    private transient PageManager pageManager;
    private transient SiteManager siteManager;

    public LanguageRoot() {
    }

    public LanguageRoot(BeanContext context, Resource resource) {
        initialize(context, resource);
    }

    @Override
    protected Resource determineDelegateResource(BeanContext context, Resource resource) {
        Resource languageRootRes = findLanguageRootResource(context, resource);
        return languageRootRes != null ? languageRootRes : resource;
    }

    @Nullable
    public Site getSite() {
        if (site == null) {
            Page page = getPage();
            site = page != null ? page.getSite() : getSiteManager().getContainingSite(getContext(), getResource());
        }
        return site;
    }

    @Nullable
    public Page getPage() {
        if (page == null) {
            page = getCurrentPage();
        }
        return page;
    }

    public boolean isRootPage() {
        return !Site.isSite(getResource());
    }

    @Override
    @Nonnull
    public Language getLanguage() {
        Page page = getPage();
        return page != null ? page.getLanguage() : delegate.getLanguage();
    }

    @Nonnull
    public String getLanguageKeyLabel() {
        return getLanguage().getKey().toUpperCase();
    }

    @Nonnull
    public Collection<Language> getPageLanguages() {
        Page page = getPage();
        return page != null ? page.getPageLanguages().getLanguages() : getSiteLanguages();
    }

    @Nonnull
    public Collection<Language> getSiteLanguages() {
        return getLanguages().getLanguages();
    }

    @Nonnull
    public List<Alternative> getAlternatives() {
        if (alternatives == null) {
            alternatives = new ArrayList<>();
            Page page = getPage();
            if (page != null) {
                BeanContext context = getContext();
                ResourceResolver resolver = context.getResolver();
                Resource languageSiblingsParent = getLanguageSiblingsParent();
                String alternativesPath = getAlternativesPath();
                Language pageLanguage = page.getLanguage();
                Collection<Language> pageLanguages = getPageLanguages();
                Languages languages = getLanguages();
                Language defaultLanguage = languages.getDefaultLanguage();
                for (Language language : languages.getLanguages()) {
                    if (!language.equals(pageLanguage)) {
                        if (pageLanguages.contains(language)) {
                            // i18n by property switch
                            alternatives.add(new Alternative(language, page));
                        } else {
                            // i18n by path split
                            Page alternativeRoot = getLanguageRoot(language);
                            if (alternativeRoot != null) {
                                Resource alternativeRes = resolver.getResource(alternativeRoot.getResource(), alternativesPath);
                                if (Page.isPage(alternativeRes)) {
                                    Page alternativePage = getPageManager().createBean(context, alternativeRes);
                                    alternatives.add(new Alternative(language, alternativePage));
                                }
                            }
                        }
                    }
                }
            }
        }
        return alternatives;
    }

    @Nullable
    protected Page getLanguageRoot(@Nonnull final Locale locale) {
        Language language = getLanguages().getLanguage(locale.getLanguage());
        return language != null ? getLanguageRoot(language) : null;
    }

    @Nullable
    protected Page getLanguageRoot(@Nonnull final Language language) {
        for (Resource child : getLanguageSiblingsParent().getChildren()) {
            if (Page.isPage(child)) {
                Page page = getPageManager().createBean(getContext(), child);
                if (page.getPageLanguages().contains(language)) {
                    return page;
                }
            }
        }
        return null;
    }

    protected String getAlternativesPath() {
        Page page = getPage();
        if (page != null) {
            Resource resource = getResource();
            String rootPath = resource.getPath();
            String pagePath = page.getPath();
            if (pagePath.startsWith(rootPath)) {
                pagePath = pagePath.substring(rootPath.length());
                if (pagePath.startsWith("/")) {
                    pagePath = pagePath.substring(1);
                }
                return pagePath;
            }
        }
        return "";
    }

    protected Resource getLanguageSiblingsParent() {
        if (languageSiblingsParent == null) {
            Resource resource = getResource();
            if (Site.isSite(resource)) {
                languageSiblingsParent = resource; // no explicit page split found - use site as parent
            } else {
                Resource parent = resource.getParent();
                languageSiblingsParent = parent != null ? parent : resource;
            }
        }
        return languageSiblingsParent;
    }

    protected PageManager getPageManager() {
        if (pageManager == null) {
            pageManager = getContext().getService(PageManager.class);
        }
        return pageManager;
    }

    protected SiteManager getSiteManager() {
        if (siteManager == null) {
            siteManager = getContext().getService(SiteManager.class);
        }
        return siteManager;
    }
}
