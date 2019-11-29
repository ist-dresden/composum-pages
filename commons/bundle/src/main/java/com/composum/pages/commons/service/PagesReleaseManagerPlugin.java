package com.composum.pages.commons.service;

import com.composum.pages.commons.PagesConstants;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.core.util.SlingResourceUtil;
import com.composum.sling.platform.staging.ReleaseChangeEventListener;
import com.composum.sling.platform.staging.StagingConstants;
import com.composum.sling.platform.staging.StagingReleaseManager;
import com.composum.sling.platform.staging.StagingReleaseManagerPlugin;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import javax.annotation.Nonnull;
import javax.jcr.RepositoryException;
import java.util.Objects;
import java.util.Set;

import static com.composum.sling.core.util.CoreConstants.TYPE_SLING_ORDERED_FOLDER;
import static org.apache.jackrabbit.JcrConstants.JCR_FROZENPRIMARYTYPE;

/**
 * Fixes the Release Worktree for inconsistencies due to deactivated pages. In that case, there is a cpp:Page in the worktree
 * that has a deactivated (and thus in the release invisible) cpp:PageContent, which violates the constraint that
 * a cpp:Page has a mandatory cpp:PageContent. We take two measures to fix this:
 * <ol>
 * <li>cpp:Page without any active children are set to {@link com.composum.sling.platform.staging.StagingConstants#PROP_DEACTIVATED}=true to hide them.</li>
 * <li>If a cpp:Page has active children, but a deactivated cpp:PageContent, we set it's {@link com.composum.sling.core.util.ResourceUtil#JCR_PRIMARYTYPE} to {@link com.composum.sling.core.util.ResourceUtil#TYPE_SLING_ORDERED_FOLDER}. </li>
 * </ol>
 */
@Component(
        service = StagingReleaseManagerPlugin.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Release Manager Plugin"
        }
)
public class PagesReleaseManagerPlugin implements StagingReleaseManagerPlugin {

    /**
     * Traverse all parents of the changed paths and check whether they need to be fixed. The ReleaseManager made them
     * attribute-wise equal to the workspace, but we might set them to {@link com.composum.sling.platform.staging.StagingConstants#PROP_DEACTIVATED} or
     * change the resource type to {@link com.composum.sling.core.util.ResourceUtil#TYPE_SLING_ORDERED_FOLDER}.
     */
    @Override
    public void fixupReleaseForChanges(@Nonnull StagingReleaseManager.Release release, Resource workspaceCopyNode, @Nonnull Set<String> changedPaths, ReleaseChangeEventListener.ReleaseChangeEvent event) throws RepositoryException {
        fixup(release, workspaceCopyNode.getPath(), workspaceCopyNode, event);
    }

    /**
     * Traverses its subnodes to see whether they need to be fixed and whether there are active pages in there.
     * Checks for the resource whether it is a page with deactivated / non existing content. If so it is set to deactivated if it has no active subnodes,
     * or is turned into a folder if it has active subnodes. (In case of turning into a folder: if the page's content is reactivated later,
     * it's turned into a page again by the ReleaseManager.)
     *
     * @return true if the resource is an activated versionable or has active subnodes, and thus needs to be kept in the release.
     */
    private boolean fixup(StagingReleaseManager.Release release, String workspaceCopyPath, Resource rawResource, ReleaseChangeEventListener.ReleaseChangeEvent event) throws RepositoryException {
        ResourceHandle resource = ResourceHandle.use(rawResource);
        if (resource.isOfType(StagingConstants.TYPE_VERSIONREFERENCE)) {
            Boolean disabled = resource.getProperty(StagingConstants.PROP_DEACTIVATED, false);
            return !disabled;
        }
        boolean isPage = PagesConstants.NODE_TYPE_PAGE.equals(resource.getProperty(JCR_FROZENPRIMARYTYPE));
        boolean hasActiveChildren = false;
        boolean hasActivePageContent = false;
        for (Resource child : resource.getChildren()) {
            boolean childActive = fixup(release, workspaceCopyPath, child, event);
            hasActiveChildren = hasActiveChildren || childActive;
            if (isPage && child.getName().equals(ResourceUtil.CONTENT_NODE)) {
                hasActivePageContent = childActive;
            }
        }
        if (isPage && !hasActivePageContent) {
            String relativePath = SlingResourceUtil.relativePath(workspaceCopyPath, resource.getPath());
            String workspacePath = release.absolutePath(relativePath);
            if (hasActiveChildren) {
                changeValueTo(resource, JCR_FROZENPRIMARYTYPE, "", TYPE_SLING_ORDERED_FOLDER, event, workspacePath);
                changeValueTo(resource, StagingConstants.PROP_DEACTIVATED, Boolean.FALSE, Boolean.FALSE, event,
                        workspacePath);
            } else {
                resource.setProperty(StagingConstants.PROP_DEACTIVATED, true);
                changeValueTo(resource, StagingConstants.PROP_DEACTIVATED, Boolean.FALSE, Boolean.TRUE, event,
                        workspacePath);
            }
        }
        return hasActiveChildren;
    }

    /** Changes a property to wantedValue if neccesary, possibly adding change to event. */
    protected <T> void changeValueTo(ResourceHandle handle, String propertyName, T defaultValue, T wantedValue,
                                     ReleaseChangeEventListener.ReleaseChangeEvent event, String workspacePath) {
        if (!Objects.equals(handle.getProperty(propertyName, defaultValue), wantedValue)) {
            event.addMoveOrUpdate(workspacePath, workspacePath);
            handle.adaptTo(ModifiableValueMap.class).put(propertyName, wantedValue);
        }
    }

}
