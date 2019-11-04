package com.composum.pages.commons.replication;

import com.composum.pages.commons.model.File;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

@Component(
        service = {ReplicationStrategy.class},
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages In Place Asset Replication Strategy",
                Constants.SERVICE_RANKING + ":Integer=300"
        },
        immediate = true
)
public class InPlaceAssetReplication extends InPlaceReplicationStrategy {

    @Override
    public boolean canReplicate(ReplicationContext context, Resource resource, boolean isReferenced) {
        return File.isFile(resource) || File.isFile(resource.getParent());
    }

}
