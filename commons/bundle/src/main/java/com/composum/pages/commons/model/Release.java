package com.composum.pages.commons.model;

import com.composum.pages.commons.request.DisplayMode;
import com.composum.sling.core.BeanContext;
import com.composum.sling.platform.staging.StagingReleaseManager;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by rw on 22.01.17.
 */
public class Release extends AbstractModel implements Comparable<Release> {

    protected StagingReleaseManager.Release stagingRelease;
    protected Calendar created;

    public Release() {
        // empty default constructor
    }

    public Release(BeanContext context, StagingReleaseManager.Release release) {
        this.stagingRelease = release;
        initialize(context, release.getMetaDataNode());
    }

    public Release(BeanContext context, Resource resource) {
        initialize(context, resource);
    }

    @Override
    protected Resource determineResource(Resource initialResource) {
        if (stagingRelease == null) {
            StagingReleaseManager releaseManager = context.getService(StagingReleaseManager.class);
            stagingRelease = releaseManager.findReleaseByReleaseResource(initialResource);
        }
        return stagingRelease.getMetaDataNode();
    }

    @Override
    protected void initializeWithResource(Resource releaseMetadataNode) {
        super.initializeWithResource(releaseMetadataNode);
        created = getProperty("jcr:created", Calendar.class);
    }

    /**
     * use requested edit mode as mode for the component rendering;
     * for the site the mode is set to 'none' in the page template to avoid container / element edit behavior
     */
    @Override
    public boolean isEditMode() {
        return DisplayMode.isEditMode(DisplayMode.requested(context));
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

    @Override
    public int compareTo(@Nonnull Release o) {
        return created != null
                ? (o.created != null ? created.compareTo(o.created) : -1)
                : (o.created != null ? 1 : 0);
    }
}
