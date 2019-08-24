package com.composum.pages.commons.replication;

import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.StringFilter;
import com.composum.sling.platform.security.AccessMode;
import com.composum.sling.platform.staging.ReleaseMapper;
import org.apache.sling.api.resource.Resource;

public interface ReplicationManager extends ReleaseMapper {

    /**
     * the blacklist to filter out mixin types during replication
     */
    StringFilter REPLICATE_MIXIN_TYPES_FILTER = new StringFilter.BlackList();

    /**
     * only properties of a released resource accepted by this filter are copied
     */
    StringFilter REPLICATE_PROPERTY_FILTER = new StringFilter.BlackList(
            "^jcr:(created.*|uuid)$",
            "^jcr:(baseVersion|predecessors|versionHistory|isCheckedOut)$"
    );

    /**
     * properties of a target accepted by this filter cannot be deleted
     */
    StringFilter REPLICATE_PROPERTY_KEEP = new StringFilter.WhiteList(
            "^jcr:(primaryType|mixinTypes|created.*|uuid)$",
            "^jcr:(baseVersion|predecessors|versionHistory|isCheckedOut)$"
    );

    PagesReplicationConfig getConfig();

    void replicateResource(ReplicationContext context, Resource resource, boolean recursive)
            throws Exception;

    void replicateReferences(ReplicationContext context)
            throws Exception;

    Resource getOrigin(BeanContext context, Resource replicate, AccessMode accessMode);
}
