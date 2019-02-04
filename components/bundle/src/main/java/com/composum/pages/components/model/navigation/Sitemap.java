package com.composum.pages.components.model.navigation;

import com.composum.pages.commons.model.AbstractModel;
import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.service.search.SearchPageFilter;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.util.LinkUtil;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    protected List<SitemapEntry> entries;

    protected SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD'T'hh:mm:ssXXX");

    private transient ResourceFilter navigationFilter;

    private transient ResourceFilter searchFilter;

    public Sitemap() {
    }

    public Sitemap(BeanContext context, Resource resource) {
        initialize(context, resource);
    }

    /**
     * Gets entries.
     *
     * @return the entries
     */
    public List<SitemapEntry> getEntries() {
        if (entries == null) {
            Stream<Page> roots;
            Resource parent = resource.getParent();
            if (Page.isPage(parent)) {
                roots = Stream.of(toPage(parent));
            } else {
                roots = StreamSupport.stream(parent.getChildren().spliterator(), false)
                        .filter(Page::isPage)
                        .map(this::toPage);
            }
            entries = roots
                    .filter((page) -> getNavigationFilter().accept(page.getResource()))
                    .flatMap(this::collectNavigableDescendants)
                    .filter((page) -> getSearchFilter().accept(page.getResource()))
                    .map(SitemapEntry::new)
                    .collect(Collectors.toList());
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

        private final String loc;
        private final String lastMod;

        protected SitemapEntry(@Nonnull Page page) {
            loc = LinkUtil.getAbsoluteUrl(context.getRequest(), page.getUrl());

            Calendar lastModified = page.getProperty(ResourceUtil.PROP_LAST_MODIFIED, Calendar.class);
            lastMod = lastModified != null ? dateFormat.format(lastModified.getTime()) : null;
        }

        public String getLoc() {
            return loc;
        }

        public String getLastMod() {
            return lastMod;
        }
    }

}
