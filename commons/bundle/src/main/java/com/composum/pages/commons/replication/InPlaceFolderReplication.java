package com.composum.pages.commons.replication;

import com.composum.pages.commons.model.Folder;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

@Component(
        service = {ReplicationStrategy.class},
        property = {
                Constants.SERVICE_DESCRIPTION + "=In Place Folder Replication Strategy"
        },
        immediate = true
)
public class InPlaceFolderReplication extends InPlaceReplicationStrategy {

    @Override
    public boolean canReplicate(ReplicationContext context, Resource resource, boolean isReferenced) {
        return canReplicateSite(context, resource) && Folder.isFolder(resource);
    }

    @Override
    protected ResourceResolver getReleaseResolver(ReplicationContext context, Resource resource) {
        return context.getResolver();
    }

    /**
     * traversal replication triggered for folders only;
     * copied only as parent path on demand if folder contains resources to replicate
     */
    @Override
    protected void replicate(ReplicationContext context, Resource targetRoot,
                             Resource released, String relativePath, boolean recursive, boolean merge)
            throws Exception {
        Resource replicate = targetRoot.getChild(relativePath);
        if (replicate != null) {
            // if a folder replicate exists remove it and wait for a request of a child
            replicate.getResourceResolver().delete(replicate);
        }
        if (recursive) {
            replicateChildren(context, released, true);
        }
    }
}
