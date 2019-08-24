package com.composum.pages.stage.model.edit.site;

import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.model.SiteRelease;
import com.composum.sling.core.util.SlingResourceUtil;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Site model to view a release resource or a resource referenced by the url suffix.
 * The resource this is created from is the metadata resource of a release below
 * {@value StagingConstants#RELEASE_ROOT_PATH}, e.g. /var/composum/content/ist/composum/cpl:releases/r1.0/metaData .
 */
public class ReleaseModel extends SiteModel {

    private static final Logger LOG = LoggerFactory.getLogger(ReleaseModel.class);

    private transient SiteRelease release;

    public SiteRelease getRelease() {
        if (release == null) {
            Resource resource = getFrameResource(); // try to use the 'frame'
            if (SiteRelease.isSiteRelease(resource)) {
                release = new SiteRelease(getContext(), resource);
            } else { // try to use the delegate (reference from suffix)
                String path = getPath();
                Site site = getSite();
                if (site != null) {
                    for (SiteRelease item : site.getReleases()) {
                        if (path.equals(item.getPath())) {
                            release = item;
                            break;
                        }
                    }
                } else {
                    LOG.warn("No site for path {} resource {}", path, SlingResourceUtil.getPath(resource));
                    getSite();
                }
            }
        }
        return release;
    }
}
