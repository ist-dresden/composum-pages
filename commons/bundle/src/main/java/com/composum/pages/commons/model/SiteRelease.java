package com.composum.pages.commons.model;

import com.composum.pages.commons.request.DisplayMode;
import com.composum.pages.commons.util.PagesUtil;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.I18N;
import com.composum.sling.platform.staging.StagingReleaseManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.composum.pages.commons.PagesConstants.KEY_CURRENT_RELEASE;
import static com.composum.pages.commons.PagesConstants.PROP_LAST_MODIFIED;

/**
 * Created by rw on 22.01.17.
 */
public class SiteRelease extends AbstractModel implements Comparable<SiteRelease> {

    private static final Logger LOG = LoggerFactory.getLogger(SiteRelease.class);

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
    public String getPath() {
        return stagingRelease.getPath();
    }

    @Override
    public String getTitle() {
        String title = super.getTitle();
        return StringUtils.isNotBlank(title) ? title
                : isCurrent() ? I18N.get(getContext().getRequest(),
                "the open next release") : "-- --";
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

    public Collection<Page> getChanges() {
        try {
            return getVersionsService().findReleaseChanges(getContext(), stagingRelease.getReleaseRoot(), this);
        } catch (RepositoryException ex) {
            LOG.error(ex.getMessage(), ex);
            return new ArrayList<>();
        }
    }

    @Override
    public int compareTo(@Nonnull SiteRelease o) {
        CompareToBuilder builder = new CompareToBuilder();
        builder.append(creationDate, o.creationDate);
        builder.append(getPath(), o.getPath());
        return builder.toComparison();
    }
}
