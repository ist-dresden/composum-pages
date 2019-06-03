package com.composum.pages.commons.replication;

import com.composum.pages.commons.model.Site;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.platform.security.AccessMode;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.HashSet;
import java.util.Set;

public class ReplicationContext extends BeanContext.Wrapper {

    public final Site site;
    public final AccessMode accessMode;
    /** Matches the subtree containing the site to copy. */
    public final ResourceFilter releaseFilter;

    public final Set<String> done = new HashSet<>();
    public final Set<String> references = new HashSet<>();
    /** Resolver for resolving released resources we copy. */
    public final ResourceResolver releaseResolver;

    public ReplicationContext(BeanContext beanContext, Site site, AccessMode accessMode, ResourceFilter releaseFilter,
                              ResourceResolver releaseResolver) {
        super(beanContext);
        this.site = site;
        this.accessMode = accessMode;
        this.releaseFilter = releaseFilter;
        this.releaseResolver = releaseResolver;
    }
}
