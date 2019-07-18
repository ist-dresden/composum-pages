package com.composum.pages.stage.model.edit.site;

import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.model.SiteRelease;
import org.apache.sling.api.resource.Resource;

/**
 * A Site model to view a release resource or a resource referenced by the url suffix.
 * The resource this is created from is the metadata resource of a release below
 * {@value StagingConstants#RELEASE_ROOT_PATH}, e.g. /var/composum/content/ist/composum/cpl:releases/r1.0/metaData .
 */
public class ReleaseModel extends SiteModel {

    private transient SiteRelease release;

    public SiteRelease getRelease() {
        if (release == null) {
            Resource resource = getFrameResource(); // try to use the 'frame'
            if (SiteRelease.isSiteRelease(resource)) {
                release = new SiteRelease(getContext(), resource);
            } else { // try to use the delegate (reference from suffix)
                String path = getPath();
                Site site = getSite();
                for (SiteRelease item : site.getReleases()) {
                    if (path.equals(item.getPath())) {
                        release = item;
                        break;
                    }
                }
            }
        }
        return release;
    }
}
