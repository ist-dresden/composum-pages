package com.composum.pages.commons.model;

import com.composum.pages.commons.request.DisplayMode;
import com.composum.pages.commons.service.PageManager;
import com.composum.pages.commons.service.SiteManager;
import com.composum.platform.models.annotations.DetermineResourceStategy;
import com.composum.platform.models.annotations.PropertyDetermineResourceStrategy;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.platform.staging.StagingReleaseManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.composum.pages.commons.PagesConstants.DEFAULT_HOMEPAGE_PATH;
import static com.composum.pages.commons.PagesConstants.NODE_TYPE_SITE;
import static com.composum.pages.commons.PagesConstants.NODE_TYPE_SITE_CONFIGURATION;
import static com.composum.pages.commons.PagesConstants.PROP_HOMEPAGE;

@PropertyDetermineResourceStrategy(Site.ContainingSiteResourceStrategy.class)
public class Site extends ContentDriven<SiteConfiguration> implements Comparable<Site> {

    private static final Logger LOG = LoggerFactory.getLogger(Site.class);

    public static final String PUBLIC_MODE_IN_PLACE = "inPlace";
    public static final String PUBLIC_MODE_VERSIONS = "versions";
    public static final String PUBLIC_MODE_LIVE = "live";

    public static final String PROP_PUBLIC_MODE = "publicMode";
    public static final String DEFAULT_PUBLIC_MODE = PUBLIC_MODE_IN_PLACE;

    public static final String RELEASE_LABEL_PREFIX = "composum-release-";

    // static resource type determination

    /**
     * check the 'cpp:Site' type for a resource
     */
    public static boolean isSite(Resource resource) {
        return ResourceUtil.isResourceType(resource, NODE_TYPE_SITE);
    }

    /**
     * check the 'cpp:SiteConfiguration' type for a resource
     */
    public static boolean isSiteConfiguration(Resource resource) {
        return ResourceUtil.isResourceType(resource, NODE_TYPE_SITE_CONFIGURATION);
    }

    // site attributes

    private transient String publicMode;
    private transient Homepage homepage;

    private transient Collection<Page> modifiedPages;
    private transient Collection<Page> releaseChanges;

    public Site() {
    }

    protected Site(BeanContext context, Resource resource) {
        initialize(context, resource);
    }

    public Site(SiteManager manager, BeanContext context, Resource resource) {
        this.siteManager = manager;
        initialize(context, resource);
    }

    @Override
    public int compareTo(@Nonnull Site site) {
        CompareToBuilder builder = new CompareToBuilder();
        builder.append(getName(), site.getName());
        builder.append(getPath(), site.getPath());
        return builder.toComparison();
    }

    // initializer extensions

    /** Compatible to {@link Site#determineResource(Resource)}. */
    public static class ContainingSiteResourceStrategy implements DetermineResourceStategy {
        @Override
        public Resource determineResource(BeanContext beanContext, Resource requestResource) {
            return beanContext.getService(SiteManager.class).getContainingSiteResource(requestResource);
        }
    }

    @Override
    protected Resource determineResource(Resource initialResource) {
        return getSiteManager().getContainingSiteResource(initialResource);
    }

    @Override
    protected SiteConfiguration createContentModel(BeanContext context, Resource contentResource) {
        return new SiteConfiguration(context, contentResource);
    }

    // Site properties

    public boolean isTemplate() {
        return getResourceManager().isTemplate(getContext(), this.getResource());
    }

    @Nonnull
    @Override
    public String getTitle() {
        String title = super.getTitle();
        return StringUtils.isNotBlank(title) ? title : getName();
    }

    // site hierarchy

    public Homepage getHomepage() {
        if (homepage == null) {
            PageManager pageManager = getPageManager();
            String homepagePath = getProperty(PROP_HOMEPAGE, null, DEFAULT_HOMEPAGE_PATH);
            Resource homepageRes = resource.getChild(homepagePath);
            if (homepageRes != null) {
                homepage = new Homepage(pageManager, context, homepageRes);
            } else {
                homepage = new Homepage(pageManager, context, resource); // use itself as homepage
            }
        }
        return homepage;
    }

    /**
     * use requested edit mode as mode for the component rendering;
     * for the site the mode is set to 'none' in the page template to avoid container / element edit behavior
     */
    @Override
    public boolean isEditMode() {
        return DisplayMode.isEditMode(DisplayMode.requested(context));
    }

    // releases

    /**
     * returns the 'publicMode' property value of this site
     */
    public String getPublicMode() {
        if (publicMode == null) {
            publicMode = getProperty(PROP_PUBLIC_MODE, null, DEFAULT_PUBLIC_MODE);
        }
        return publicMode;
    }

    /**
     * retrieves the release label for a release category ('public', 'preview')
     * the content of this release is delivered if a public request in the category is performed
     */
    public String getReleaseNumber(String category) {
        StagingReleaseManager releaseManager = context.getService(StagingReleaseManager.class);
        StagingReleaseManager.Release release = releaseManager.findReleaseByMark(resource, category);
        return release != null ? release.getNumber() : null;
    }

    /**
     * @return the list of content releases of this site
     */
    public List<SiteRelease> getReleases() {
        StagingReleaseManager releaseManager = context.getService(StagingReleaseManager.class);
        List<StagingReleaseManager.Release> stagingReleases = releaseManager.getReleases(resource);
        return stagingReleases.stream()
                .map(r -> new SiteRelease(context, r)).sorted((r1, r2) -> {
                    Calendar c1 = r1.getCreationDate();
                    Calendar c2 = r2.getCreationDate();
                    return c1 == null ? 1 : c2 == null ? -1 : c2.compareTo(c1);
                }).collect(Collectors.toList());

    }

    public SiteRelease getCurrentRelease(){
        final List<SiteRelease> releases = getReleases();
        return releases.isEmpty() ? null : releases.get(0);
    }

    /**
     * @return the list of pages changed after last activation
     */
    public Collection<Page> getModifiedPages() {
        if (modifiedPages == null) {
            modifiedPages = getVersionsService().findModifiedPages(getContext(), getResource());
        }
        return modifiedPages;
    }

    /**
     * @return the list of pages changed (modified and activated) for the current release
     */
    public Collection<Page> getReleaseChanges() {
        if (releaseChanges == null) {
            releaseChanges = getReleaseChanges(getCurrentRelease());
        }
        return releaseChanges;
    }

    @Nonnull
    public Collection<Page> getReleaseChanges(@Nullable final SiteRelease releaseToCheck) {
        return releaseToCheck != null ? releaseToCheck.getChanges() : Collections.emptyList();
    }
}
