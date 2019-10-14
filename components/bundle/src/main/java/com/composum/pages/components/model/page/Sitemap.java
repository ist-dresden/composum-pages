package com.composum.pages.components.model.page;

import com.composum.pages.commons.model.AbstractModel;
import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.service.search.SearchPageFilter;
import com.composum.pages.components.model.navigation.NavigationPageFilter;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.util.LinkUtil;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Model for displaying the sitemap. To use it, you need to create a node sitemap with
 * sling:resourceType="composum/pages/components/navigation/sitemap" below the root where you want the sitemap.
 * Calling <code>sitemap.xml</code> will yield all subpages visible in the navigation and also visible in the search.
 * Limitations: there are some optional attributes changefreq and priority in sitemaps - we don't generate
 * those, since there are no settings for these at the pages so far.
 *
 * @see "https://www.sitemaps.org/de/protocol.html"
 */
public class Sitemap extends AbstractModel {

    public static final String PN_SITEMAP_ROOT_PATH = "sitemapRootPath";
    public static final String PN_ROBOTS_TXT = "robotsTxt";

    public static final String SITEMAP_DATE_FORMAT = "YYYY-MM-DD'T'hh:mm:ssXXX";

    private transient String sitemapRootPath;

    private transient List<SitemapEntry> entries;

    private transient ResourceFilter navigationFilter;
    private transient ResourceFilter searchFilter;

    public Sitemap() {
    }

    public Sitemap(BeanContext context, Resource resource) {
        initialize(context, resource);
    }

    @Nonnull
    public String getSitemapRootPath() {
        if (sitemapRootPath == null) {
            sitemapRootPath = getInherited(PN_SITEMAP_ROOT_PATH, "");
            if (StringUtils.isBlank(sitemapRootPath)) {
                Site site = getSiteManager().getContainingSite(getContext(), getResource());
                if (site != null) {
                    sitemapRootPath = site.getPath();
                }
            }
        }
        return sitemapRootPath;
    }

    /**
     * Gets entries.
     *
     * @return the entries
     */
    public List<SitemapEntry> getEntries() {
        if (entries == null) {
            Resource root = null;
            String rootPath = getSitemapRootPath();
            if (StringUtils.isNotBlank(rootPath)) {
                root = getContext().getResolver().getResource(rootPath);
            }
            if (root == null) {
                root = resource.getParent();
            }
            if (root != null) {
                Stream<Page> roots;
                if (Page.isPage(root)) {
                    roots = Stream.of(toPage(root));
                } else {
                    roots = StreamSupport.stream(root.getChildren().spliterator(), false)
                            .filter(Page::isPage)
                            .map(this::toPage);
                }
                entries = roots
                        .filter((page) -> getNavigationFilter().accept(page.getResource()))
                        .flatMap(this::collectNavigableDescendants)
                        .filter((page) -> getSearchFilter().accept(page.getResource()))
                        .map(SitemapEntry::new)
                        .collect(Collectors.toList());
            } else {
                entries = Collections.emptyList();
            }
        }
        return entries;
    }

    private Stream<Page> collectNavigableDescendants(Page page) {
        return Stream.concat(Stream.of(page),
                page.getChildPages(getNavigationFilter()).stream().flatMap(this::collectNavigableDescendants)
        );
    }

    protected ResourceFilter getNavigationFilter() {
        if (navigationFilter == null) {
            navigationFilter = new NavigationPageFilter(context);
        }
        return navigationFilter;
    }

    protected ResourceFilter getSearchFilter() {
        if (searchFilter == null) {
            searchFilter = new SearchPageFilter(context);
        }
        return searchFilter;
    }

    protected Page toPage(Resource aResource) {
        return getPageManager().createBean(context, aResource);
    }

    /**
     * Data for one url entry in the sitemap. We currently do not create changefreq and priority, as there is
     * nothing to create that data.
     */
    public class SitemapEntry {

        protected final Page page;
        private final String lastMod;

        private transient String loc;
        private transient String label;
        private transient String path;

        protected SitemapEntry(@Nonnull Page page) {
            this.page = page;
            Calendar lastModified = page.getProperty(ResourceUtil.PROP_LAST_MODIFIED, Calendar.class);
            lastMod = lastModified != null ? new SimpleDateFormat(SITEMAP_DATE_FORMAT).format(lastModified.getTime()) : null;
        }

        public String getLoc() {
            if (loc == null) {
                loc = LinkUtil.getAbsoluteUrl(context.getRequest(), page.getUrl());
            }
            return loc;
        }

        public String getLabel() {
            if (label == null) {
                label = page.getTitle();
            }
            return label;
        }

        public String getPath() {
            if (path == null) {
                path = page.getSiteRelativePath();
            }
            return path;
        }

        public String getLastMod() {
            return lastMod;
        }
    }

    public String getRobotsTxt() {
        return getProperty(PN_ROBOTS_TXT, null, "");
    }
}
