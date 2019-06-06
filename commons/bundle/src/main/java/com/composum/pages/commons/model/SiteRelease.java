package com.composum.pages.commons.model;

import com.composum.pages.commons.request.DisplayMode;
import com.composum.pages.commons.util.PagesUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.I18N;
import com.composum.sling.platform.staging.StagingReleaseManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import static com.composum.pages.commons.PagesConstants.KEY_CURRENT_RELEASE;
import static com.composum.pages.commons.PagesConstants.PROP_LAST_MODIFIED;

/**
 * Created by rw on 22.01.17.
 */
public class SiteRelease extends AbstractModel implements Comparable<SiteRelease> {

    protected StagingReleaseManager.Release stagingRelease;
    private transient Calendar creationDate;
    private transient Calendar lastModified;

    public SiteRelease() {
        // empty default constructor
    }

    public SiteRelease(BeanContext context, StagingReleaseManager.Release release) {
        this.stagingRelease = release;
        initialize(context, release.getMetaDataNode());
    }

    public SiteRelease(BeanContext context, Resource resource) {
        initialize(context, resource);
    }

    @Override
    protected Resource determineResource(Resource initialResource) {
        if (stagingRelease == null) {
            StagingReleaseManager releaseManager = context.getService(StagingReleaseManager.class);
            stagingRelease = releaseManager.findReleaseByReleaseResource(initialResource);
        }
        return Objects.requireNonNull(stagingRelease).getMetaDataNode();
    }

    @Override
    protected void initializeWithResource(Resource releaseMetadataNode) {
        super.initializeWithResource(releaseMetadataNode);
        creationDate = getProperty("jcr:created", Calendar.class);
    }

    /**
     * use requested edit mode as mode for the component rendering;
     * for the site the mode is set to 'none' in the page template to avoid container / element edit behavior
     */
    @Override
    public boolean isEditMode() {
        return DisplayMode.isEditMode(DisplayMode.requested(context));
    }

    public boolean isCurrent() {
        return KEY_CURRENT_RELEASE.equals(getKey());
    }

    @Override
    public String getTitle() {
        String title = super.getTitle();
        return StringUtils.isNotBlank(title) ? title
                : isCurrent() ? I18N.get(getContext().getRequest(),
                "the open next release") : "<no title>";
    }

    public String getKey() {
        return stagingRelease.getNumber();
    }

    /** The label that is set on a document version when it is in a release. */
    public String getLabel() {
        return stagingRelease.getReleaseLabel();
    }

    public List<String> getCategories() {
        return stagingRelease.getMarks();
    }

    public Calendar getLastModified() {
        if (lastModified == null) {
            lastModified = getProperty(PROP_LAST_MODIFIED, null, Calendar.class);
        }
        return lastModified;
    }

    public String getLastModifiedString() {
        return PagesUtil.getTimestampString(getLastModified());
    }

    public Calendar getCreationDate() {
        return creationDate;
    }

    public String getCreationDateString() {
        return PagesUtil.getTimestampString(getCreationDate());
    }

    @Override
    public int compareTo(@Nonnull SiteRelease o) {
        CompareToBuilder builder = new CompareToBuilder();
        builder.append(creationDate, o.creationDate);
        builder.append(getPath(), o.getPath());
        return builder.toComparison();
    }
}
