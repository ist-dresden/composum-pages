package com.composum.pages.commons.replication;

import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.filter.StringFilter;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.core.util.SlingResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.composum.sling.platform.staging.StagingConstants.PROP_REPLICATED_VERSION;

/**
 * Some algorithms to compare two release trees. This is a separate class mostly to avoid passing around many
 * parameters.
 */
class ReleaseTreeCompareStrategy {

    /** {@link ResourceFilter} that returns true for versionables. */
    protected final ResourceFilter VERSIONABLE_FILTER =
            new ResourceFilter.NodeTypeFilter(new StringFilter.WhiteList(ResourceUtil.MIX_VERSIONABLE));

    protected final ResourceResolver resolver1;
    protected final Resource root1;
    protected final ResourceResolver resolver2;
    protected final Resource root2;

    public ReleaseTreeCompareStrategy(ResourceResolver resolver1, Resource root1, ResourceResolver resolver2, Resource root2) {
        this.resolver1 = resolver1;
        this.root1 = root1;
        this.resolver2 = resolver2;
        this.root2 = root2;
    }

    /**
     * Returns list of paths to versionables that are different below root1 and and root2 - one of them does not
     * exist or they have a different
     * {@link com.composum.sling.platform.staging.StagingConstants#PROP_REPLICATED_VERSION}.
     */
    public List<String> compareVersionables() {
        // These versionables do not exist below root2 or have different version number:
        List<String> differences = SlingResourceUtil.descendantsStream(root1, VERSIONABLE_FILTER::accept)
                .filter(VERSIONABLE_FILTER::accept)
                .filter((r1) -> {
                    Resource r2 = corresponding2Resource(r1);
                    return !StringUtils.equals(getAttribute(r1, PROP_REPLICATED_VERSION),
                            getAttribute(r2, PROP_REPLICATED_VERSION));
                })
                .map(Resource::getPath)
                .collect(Collectors.toList());
        // These do not exist below root1 and therefore were missed by differences
        List<String> missingResource1 = SlingResourceUtil.descendantsStream(root2, VERSIONABLE_FILTER::accept)
                .filter(VERSIONABLE_FILTER::accept)
                .filter((r2) -> corresponding1Resource(r2) == null)
                .map(Resource::getPath)
                .map((p2) -> SlingResourceUtil.appendPaths(
                        root1.getPath(),
                        SlingResourceUtil.relativePath(root2.getPath(), p2)))
                .collect(Collectors.toList());
        differences.addAll(missingResource1);
        return differences;
    }

    @Nullable
    protected String getAttribute(@Nullable Resource resource, @Nonnull String attribute) {
        return resource != null ? resource.getValueMap().get(attribute, String.class) : null;
    }

    @Nullable
    protected Resource corresponding2Resource(@Nonnull Resource resource1) {
        String relpath = SlingResourceUtil.relativePath(root1.getPath(), resource1.getPath());
        String path2 = SlingResourceUtil.appendPaths(root2.getPath(), relpath);
        return resolver2.getResource(path2);
    }

    @Nullable
    protected Resource corresponding1Resource(@Nonnull Resource resource2) {
        String relpath = SlingResourceUtil.relativePath(root2.getPath(), resource2.getPath());
        String path1 = SlingResourceUtil.appendPaths(root1.getPath(), relpath);
        return resolver1.getResource(path1);
    }

    public List<String> compareChildrenOrderings() {
        // FIXME(hps,30.01.20) implement this
        return Collections.emptyList();
    }

    public List<String> compareParentNodes() {
        // FIXME(hps,30.01.20) implement this
        return Collections.emptyList();
    }

}
