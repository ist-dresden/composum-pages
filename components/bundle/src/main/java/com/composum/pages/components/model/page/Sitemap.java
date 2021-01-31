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
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.composum.sling.core.filter.ResourceFilter.FilterSet.Rule.and;

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

    public static final String SITEMAP_DATE_FORMAT = "YYYY-MM-dd'T'hh:mm:ssXXX";

    private transient String sitemapRootPath;

    private transient List<SitemapEntry> sitemapMenuEntries;
    private transient List<SitemapEntry> sitemapDataEntries;

    private transient ResourceFilter sitemapPageFilter;
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
     * Gets entries for a sitemap based menu; this ignores pages marked with 'hideInNavigation'.
     */
    public List<SitemapEntry> getSitemapMenuEntries() {
        if (sitemapMenuEntries == null) {
            sitemapMenuEntries = getSitemapEntries(getSitemapMenuFilter());
        }
        return sitemapMenuEntries;
    }

    /**
     * Gets entries for the 'sitemap.xml'; this doesn't honor a 'hideInNavigation' to find all searchable pages.
     */
    public List<SitemapEntry> getSitemapDataEntries() {
        if (sitemapMenuEntries == null) {
            sitemapMenuEntries = getSitemapEntries(getSitemapDataFilter());
        }
        return sitemapMenuEntries;
    }

    /**
     * Gets entries using the given filter.
     */
    @Nonnull
    protected List<SitemapEntry> getSitemapEntries(ResourceFilter pageFilter) {
        List<SitemapEntry> sitemapMenuEntries;
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
            sitemapMenuEntries = roots
                    .filter((page) -> pageFilter.accept(page.getResource()))
                    .flatMap(this::collectNavigableDescendants)
                    .filter((page) -> getSearchFilter().accept(page.getResource()))
                    .map(SitemapEntry::new).sorted()
                    .collect(Collectors.toList());
        } else {
            sitemapMenuEntries = Collections.emptyList();
        }
        return sitemapMenuEntries;
    }

    private Stream<Page> collectNavigableDescendants(Page page) {
        return Stream.concat(Stream.of(page),
                page.getChildPages(getSitemapMenuFilter()).stream().flatMap(this::collectNavigableDescendants)
        );
    }

    protected ResourceFilter getSitemapMenuFilter() {
        if (sitemapPageFilter == null) {
            sitemapPageFilter = new ResourceFilter.FilterSet(and, new NavigationPageFilter(context), new PageRedirectFilter());
        }
        return sitemapPageFilter;
    }

    protected ResourceFilter getSitemapDataFilter() {
        if (sitemapPageFilter == null) {
            sitemapPageFilter = new PageRedirectFilter();
        }
        return sitemapPageFilter;
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

    protected class PageRedirectFilter implements ResourceFilter {

        @Override
        public boolean accept(@Nullable Resource resource) {
            if (Page.isPage(resource)) {
                Resource content = resource.getChild(JcrConstants.JCR_CONTENT);
                if (content != null) {
                    ValueMap values = content.getValueMap();
                    return StringUtils.isBlank(values.get("sling:target", ""));
                }
            }
            return false;
        }

        @Override
        public boolean isRestriction() {
            return true;
        }

        @Override
        public void toString(@Nonnull StringBuilder builder) {
        }
    }

    /**
     * Data for one url entry in the sitemap. We currently do not create changefreq and priority, as there is
     * nothing to create that data.
     */
    public class SitemapEntry implements Comparable<SitemapEntry> {

        protected final Page page;
        private final String lastMod;
        private final int depth;

        private transient String loc;
        private transient String label;
        private transient String path;

        protected SitemapEntry(@Nonnull Page page) {
            this.page = page;
            Calendar lastModified = page.getProperty(ResourceUtil.PROP_LAST_MODIFIED, Calendar.class);
            lastMod = lastModified != null ? new SimpleDateFormat(SITEMAP_DATE_FORMAT).format(lastModified.getTime()) : null;
            depth = StringUtils.countMatches(page.getSiteRelativePath(), '/');
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

        /**
         * The hierarchy depth below the site root.
         */
        public int getDepth() {
            return depth;
        }

        @Override
        public int compareTo(@NotNull Sitemap.SitemapEntry other) {
            return getPath().compareTo(other.getPath());
        }
    }

    public String getRobotsTxt() {
        return getProperty(PN_ROBOTS_TXT, null, "");
    }
}
