package com.composum.pages.commons.model;

import com.composum.pages.commons.PagesConfiguration;
import com.composum.pages.commons.model.properties.Language;
import com.composum.pages.commons.request.DisplayMode;
import com.composum.pages.commons.service.PageManager;
import com.composum.pages.commons.service.SiteManager;
import com.composum.pages.commons.service.VersionsService;
import com.composum.platform.models.annotations.DetermineResourceStategy;
import com.composum.platform.models.annotations.PropertyDetermineResourceStrategy;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.platform.security.AccessMode;
import com.composum.sling.platform.staging.*;
import com.composum.sling.platform.staging.replication.ReplicationConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.RepositoryException;
import java.util.*;
import java.util.stream.Collectors;

import static com.composum.pages.commons.PagesConstants.*;

@PropertyDetermineResourceStrategy(Site.ContainingSiteResourceStrategy.class)
public class Site extends ContentDriven<SiteConfiguration> implements Comparable<Site> {

    private static final Logger LOG = LoggerFactory.getLogger(Site.class);

    public static final String PUBLIC_MODE_IN_PLACE = ReplicationConstants.PUBLIC_MODE_IN_PLACE;
    public static final String PUBLIC_MODE_VERSIONS = "versions";
    public static final String PUBLIC_MODE_LIVE = "live";

    public static final String PROP_PUBLIC_MODE = ReplicationConstants.PROP_PUBLIC_MODE;
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
    private transient Map<String, String> publicModeOptions;
    private transient Homepage homepage;

    private transient List<ContentVersion> modifiedContent;
    private transient Collection<ContentVersion> releaseChanges;

    private transient String templateType;
    private transient String componentSettingsEditType;

    private transient SiteRelease currentRelease;
    private transient List<SiteRelease> releases;
    private transient String releaseNumber;
    private transient boolean[] publishSuggestion;

    public Site() {
    }

    protected Site(BeanContext context, Resource resource) {
        initialize(context, resource);
    }

    public Site(SiteManager manager, BeanContext context, Resource resource) {
        setSiteManager(manager);
        initialize(context, resource);
    }

    public void setSiteManager(SiteManager siteManager) {
        this.siteManager = siteManager;
    }

    @Override
    public int compareTo(@Nonnull Site site) {
        CompareToBuilder builder = new CompareToBuilder();
        builder.append(getName(), site.getName());
        builder.append(getPath(), site.getPath());
        return builder.toComparison();
    }

    // initializer extensions

    /**
     * Compatible to {@link AbstractModel#determineResource(Resource)}.
     */
    public static class ContainingSiteResourceStrategy implements DetermineResourceStategy {
        @Override
        public Resource determineResource(BeanContext beanContext, Resource requestResource) {
            return beanContext.getService(SiteManager.class).getContainingSiteResource(requestResource);
        }
    }

    @Override
    @Nullable
    protected Resource determineResource(@Nullable final Resource initialResource) {
        return getSiteManager().getContainingSiteResource(initialResource);
    }

    @Override
    @Nonnull
    protected SiteConfiguration createContentModel(BeanContext context, Resource contentResource) {
        return new SiteConfiguration(context, contentResource);
    }

    // Site properties

    public boolean isTemplate() {
        return getResourceManager().isTemplate(getContext(), this.getResource());
    }

    @Nonnull // but maybe ''
    public String getTemplateType() {
        if (templateType == null) {
            templateType = isTemplate() ? getPath() : getTemplatePath();
            if (StringUtils.isNotBlank(templateType)) {
                ResourceResolver resolver = getContext().getResolver();
                for (String root : resolver.getSearchPath()) {
                    if (templateType.startsWith(root)) {
                        templateType = templateType.substring(root.length());
                        break;
                    }
                }
            }
            if (templateType == null) {
                templateType = "";
            }
        }
        return templateType;
    }

    @Nullable
    public String getComponentSettingsEditType() {
        if (componentSettingsEditType == null) {
            Resource template = getTemplate();
            if (template != null) {
                componentSettingsEditType = template.getValueMap().get(JcrConstants.JCR_CONTENT + "/siteComponentSettings", "");
            }
        }
        return componentSettingsEditType;
    }

    @Override
    @Nonnull
    public String getTitle() {
        String title = super.getTitle();
        return StringUtils.isNotBlank(title) ? title : getName();
    }

    @Override
    @Nonnull
    public Language getLanguage() {
        return getLanguages().getDefaultLanguage();
    }

    /**
     * @return the path; supports the use of a site as is (maybe null) in a JSP expression
     */
    @Override
    public String toString() {
        return getPath();
    }

    // site hierarchy

    @Nonnull
    public Homepage getHomepage(Locale locale) {
        if (homepage == null) {
            PageManager pageManager = getPageManager();
            String homepagePath = getConfiguredHome();
            Resource homepageRes = resource.getChild(homepagePath);
            if (homepageRes != null) {
                LanguageRoot languageRoot = new LanguageRoot(context, homepageRes);
                Page languageHome = languageRoot.getLanguageRoot(locale);
                if (languageHome != null) {
                    homepage = new Homepage(pageManager, context, languageHome.getResource());
                } else {
                    homepage = new Homepage(pageManager, context, homepageRes);
                }
            } else {
                homepage = new Homepage(pageManager, context, resource); // use site itself as homepage
            }
        }
        return homepage;
    }

    public String getConfiguredHome() {
        return getProperty(PROP_HOMEPAGE, null, DEFAULT_HOMEPAGE_PATH);
    }

    /**
     * use requested edit mode as mode for the component rendering;
     * for the site the mode is set to 'none' in the page template to avoid container / element edit behavior
     */
    @Override
    public boolean isEditMode() {
        return DisplayMode.isEditMode(DisplayMode.requested(context));
    }

    @Nonnull
    public String getOpenUri() {
        String path = getPath();
        Homepage homepage = getHomepage(getLocale());
        if (homepage.isTheSiteItself()) {
            // assuming insufficient permissions (no content access) if homepage is the site itself
            return getSiteManager().getPreviewUrl(this);
        } else {
            return "/bin/pages.html" + getPath();
        }
    }

    // releases

    /**
     * returns the 'publicMode' property value of this site
     */
    @Nonnull
    public String getPublicMode() {
        if (publicMode == null) {
            publicMode = getProperty(PROP_PUBLIC_MODE, null, DEFAULT_PUBLIC_MODE);
        }
        return publicMode;
    }

    @Nonnull
    public Map<String, String> getPublicModeOptions() {
        if (publicModeOptions == null) {
            publicModeOptions = context.getService(PagesConfiguration.class).getPublicModeOptions(context.getRequest());
        }
        return publicModeOptions;
    }

    @Nonnull
    public String getStagePath(AccessMode accessMode) {
        switch (getPublicMode()) {
            case PUBLIC_MODE_IN_PLACE:
                ReleaseChangeEventPublisher releaseChangeEventPublisher = getContext().getService(ReleaseChangeEventPublisher.class);
                String stagePath = releaseChangeEventPublisher.getStagePath(getResource(), accessMode != null ? accessMode.name().toLowerCase() : null);
                return StringUtils.defaultIfBlank(stagePath, getPath());
            default:
                return getPath();
        }
    }

    /**
     * retrieves the release label for a release category ('public', 'preview')
     * the content of this release is delivered if a public request in the category is performed
     */
    @Nullable
    public String getReleaseNumber(String category) {
        if (releaseNumber == null) {
            StagingReleaseManager releaseManager = context.getService(StagingReleaseManager.class);
            Release release = releaseManager.findReleaseByMark(resource, StringUtils.lowerCase(category));
            releaseNumber = release != null ? release.getNumber() : null;
        }
        return releaseNumber;
    }

    /**
     * @return [release.public, release.preview, current.public, current.preview]
     */
    public boolean[] getPublishSuggestion() {
        if (publishSuggestion == null) {
            SiteRelease current = getCurrentRelease();
            SiteRelease previous = null;
            if (current != null) {
                previous = current.getPreviousRelease();
            }
            publishSuggestion = new boolean[]{
                    previous != null && previous.isPublic(),
                    previous != null && previous.isPreview(),
                    current != null && current.isPublic(),
                    current != null && current.isPreview()
            };
        }
        return publishSuggestion;
    }

    /**
     * @return the list of content releases of this site
     */
    @Nonnull
    public List<SiteRelease> getReleases() {
        if (releases == null) {
            StagingReleaseManager releaseManager = context.getService(StagingReleaseManager.class);
            List<Release> stagingReleases = releaseManager.getReleases(resource);
            releases = stagingReleases.stream()
                    .map(r -> new SiteRelease(context, r))
                    .sorted(Comparator.comparing(SiteRelease::getKey, ReleaseNumberCreator.COMPARATOR_RELEASES.reversed()))
                    .collect(Collectors.toList());
        }
        return releases;
    }

    @Nullable
    public SiteRelease getCurrentRelease() {
        if (currentRelease == null) {
            final List<SiteRelease> releases = getReleases();
            currentRelease = releases.stream()
                    .filter((r) -> r.getKey().equals(StagingConstants.CURRENT_RELEASE))
                    .findAny().orElse(null);
        }
        return currentRelease;
    }

    /**
     * @return the list of pages changed after last activation
     */
    @Nonnull
    public List<ContentVersion> getModifiedContent() {
        if (modifiedContent == null) {
            try {
                modifiedContent = getVersionsService().findModifiedContent(getContext(), getCurrentRelease(), null);
            } catch (RepositoryException e) {
                LOG.error("Retrieving modified pages for " + getResource().getPath(), e);
                modifiedContent = new ArrayList<>();
            }
        }
        return modifiedContent;
    }

    /**
     * @return the list of pages changed (modified and activated) for the current release
     */
    @Nonnull
    public Collection<ContentVersion> getReleaseChanges() {
        if (releaseChanges == null) {
            releaseChanges = getReleaseChanges(getCurrentRelease(), null);
        }
        return releaseChanges;
    }

    @Nonnull
    public Collection<ContentVersion> getReleaseChanges(@Nullable final SiteRelease releaseToCheck,
                                                        @Nullable final VersionsService.ContentVersionFilter filter) {
        return releaseToCheck != null ? releaseToCheck.getChanges(filter) : Collections.emptyList();
    }
}
