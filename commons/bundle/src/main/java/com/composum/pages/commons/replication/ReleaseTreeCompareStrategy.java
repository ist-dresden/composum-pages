package com.composum.pages.commons.replication;

import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.core.filter.StringFilter;
import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.core.util.SlingResourceUtil;
import com.composum.sling.platform.staging.query.Query;
import com.composum.sling.platform.staging.query.QueryBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.composum.pages.commons.replication.ReplicationManager.REPLICATE_PROPERTY_FILTER;
import static com.composum.sling.platform.staging.StagingConstants.PROP_REPLICATED_VERSION;

/**
 * Some algorithms to compare two release trees. This is a separate class mostly to avoid passing around many
 * parameters.
 */
class ReleaseTreeCompareStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(ReleaseTreeCompareStrategy.class);

    /** {@link ResourceFilter} that returns true for versionables. */
    protected final ResourceFilter VERSIONABLE_FILTER =
            new ResourceFilter.NodeTypeFilter(new StringFilter.WhiteList(ResourceUtil.MIX_VERSIONABLE));

    @Nonnull
    protected final ResourceResolver resolver1;
    @Nonnull
    protected final Resource root1;
    @Nonnull
    protected final ResourceResolver resolver2;
    @Nonnull
    protected final Resource root2;
    @Nonnull
    protected final List<String> paths1;

    /**
     * @param checkPaths1 the paths we focus our comparison on. Must be root1 or below. If null / empty we compare the
     *                    whole tree.
     */
    public ReleaseTreeCompareStrategy(@Nonnull ResourceResolver resolver1, @Nonnull Resource root1,
                                      @Nonnull ResourceResolver resolver2, @Nonnull Resource root2,
                                      @Nullable Collection<String> checkPaths1) {
        this.resolver1 = resolver1;
        this.root1 = root1;
        this.resolver2 = resolver2;
        this.root2 = root2;
        paths1 = new ArrayList<>();
        Collection<String> checkPaths = checkPaths1 != null || checkPaths1.isEmpty() ? checkPaths1 : Collections.singleton(root1.getPath());
        for (String path : checkPaths) {
            if (!StringUtils.startsWith(path, root1.getPath())) {
                throw new IllegalArgumentException("Path to compare " +
                        "doesn't start with release root: " + path + " vs. " + root1.getPath());
            }
            paths1.removeIf(p -> SlingResourceUtil.isSameOrDescendant(path, p));
            if (paths1.stream().noneMatch(p -> SlingResourceUtil.isSameOrDescendant(p, path))) {
                paths1.add(path);
            }
        }
    }

    /**
     * Returns list of paths to versionables that are different below root1 and and root2 - one of them does not
     * exist or they have a different
     * {@link com.composum.sling.platform.staging.StagingConstants#PROP_REPLICATED_VERSION}.
     */
    public List<String> compareVersionables() {
        // These versionables do not exist below root2 or have different version number:
        List<String> differences = paths1.stream()
                .map(resolver1::getResource)
                .flatMap(p -> SlingResourceUtil.descendantsStream(p, VERSIONABLE_FILTER::accept))
                .filter(VERSIONABLE_FILTER::accept)
                .filter((r1) -> {
                    Resource r2 = corresponding2Resource(r1);
                    return !StringUtils.equals(getAttribute(r1, PROP_REPLICATED_VERSION),
                            getAttribute(r2, PROP_REPLICATED_VERSION));
                })
                .map(Resource::getPath)
                .collect(Collectors.toList());
        // These do not exist below root1 and therefore were missed by differences
        List<String> missingResource1 = paths1.stream()
                .map(p -> SlingResourceUtil.appendPaths(root2.getPath(),
                        SlingResourceUtil.relativePath(root1.getPath(), p)))
                .map(resolver2::getResource)
                .flatMap(p -> SlingResourceUtil.descendantsStream(p, VERSIONABLE_FILTER::accept))
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

    protected boolean hasOrderableChildren(@Nullable Resource resource) {
        if (resource == null) { return false; }
        Node node = resource.adaptTo(Node.class);
        if (node == null) {
            throw new IllegalArgumentException("Not a JCR-based resource: " + resource);
        }
        try {
            return node.getPrimaryNodeType().hasOrderableChildNodes();
        } catch (RepositoryException e) {
            LOG.error("Trouble determining child orderability for {}", SlingResourceUtil.getPath(resource), e);
            return true; // play safe.
        }
    }

    protected List<String> childOrder(@Nullable Resource resource) {
        List<String> result = new ArrayList<>();
        if (resource != null && hasOrderableChildren(resource)) {
            for (Resource child : resource.getChildren()) {
                result.add(child.getName());
            }
        }
        return result;
    }

    /** The parent nodes of all versionables below any of {@link #paths1} which are below or at root1. */
    @Nonnull
    protected Stream<Resource> relevantParents1() {
        Stream<Resource> belowPaths1 = paths1.stream()
                .map(resolver1::getResource)
                .flatMap(p -> SlingResourceUtil.descendantsStream(p, VERSIONABLE_FILTER::accept))
                .filter((r) -> !VERSIONABLE_FILTER.accept(r));
        Stream<Resource> paths1Parents = paths1.stream()
                .flatMap(path ->
                        Stream.iterate(ResourceUtil.getParent(path), p -> StringUtils.startsWith(p, root1.getPath()),
                                ResourceUtil::getParent)
                ).distinct()
                .map(resolver1::getResource);
        return Stream.concat(paths1Parents, belowPaths1);
    }

    /**
     * Returns a list of paths to resources that exist on both sides, have orderable children and have a different
     * child ordering.
     */
    public List<String> compareChildrenOrderings() {
        List<String> result = relevantParents1()
                .filter((r1) -> {
                    Resource r2 = corresponding2Resource(r1);
                    return r2 != null && !Objects.equals(childOrder(r1), childOrder(r2));
                })
                .map(Resource::getPath)
                .collect(Collectors.toList());
        return result;
    }

    /**
     * Returns a list of paths to parent nodes of versionables (but below the roots) which have different attributes.
     */
    public List<String> compareParentNodes() {
        List<String> result = relevantParents1()
                .filter((r1) -> {
                    Resource r2 = corresponding2Resource(r1);
                    return r2 != null && !attributesEqual(r1, r2);
                })
                .map(Resource::getPath)
                .collect(Collectors.toList());
        return result;
    }

    protected boolean attributesEqual(@Nonnull Resource resource1, @Nonnull Resource resource2) {
        ValueMap vm1 = resource1.getValueMap();
        ValueMap vm2 = resource2.getValueMap();
        Set<String> props1 = vm1.keySet().stream()
                .filter(REPLICATE_PROPERTY_FILTER::accept)
                .collect(Collectors.toSet());
        Set<String> props2 =
                vm2.keySet().stream()
                        .filter(REPLICATE_PROPERTY_FILTER::accept)
                        .collect(Collectors.toSet());
        boolean result = props1.equals(props2);
        if (result) { // if keys are the same, compare details
            for (String key : props1) {
                result = result && attributeEqual(vm1.get(key), vm2.get(key), key, resource1.getPath());
            }
        }
        return result;
    }

    protected boolean attributeEqual(@Nonnull Object val1, @Nonnull Object val2, String name, String path) {
        boolean equal;
        if (val1 instanceof String) {
            if (val2 instanceof String) {
                equal = Objects.equals(val1, val2);
                if (!equal && StringUtils.startsWith((String) val1, root1.getPath())) {
                    // check whether this is a translated path
                    String relpath = SlingResourceUtil.relativePath(root1.getPath(), (String) val1);
                    String path2 = SlingResourceUtil.appendPaths(root2.getPath(), relpath);
                    equal = StringUtils.equals(path2, (String) val2);
                }
            } else {
                equal = false;
            }
        } else if (val1.getClass().isArray()) {
            if (val2.getClass().isArray()) {
                equal = Arrays.equals((Object[]) val1, (Object[]) val2);
            } else {
                equal = false;
            }
        } else {
            equal = Objects.equals(val1, val2);
        }
        if (!equal) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Attributes not equal at {}/{} : {} vs. {}", path, name, val1, val2);
            }
        }
        return equal;
    }


    /**
     * Searches via a query in {@link #root2} for paths that refer to {@link #root1} but are present in
     * {@link #root2}, too - which looks like something that wasn't replaced propertly during replication.
     * Just a quick check to find bugs.
     */
    @Nullable
    public List<String> suspiciousAttributes() {
        List<String> result = new ArrayList<>();
        try {
            Pattern pathPattern = Pattern.compile(Pattern.quote(root1.getPath()) + "(/[^:|\"'*]*)*");

            Query query = resolver2.adaptTo(QueryBuilder.class).createQuery();
            query.path(root2.getPath()).condition(
                    query.conditionBuilder().contains(root1.getPath() + "/%")
            );
            for (Resource resource : query.execute()) {
                for (Map.Entry<String, Object> entry : resource.getValueMap().entrySet()) {
                    if (entry.getValue() instanceof String) {
                        String pathCandidate = (String) entry.getValue();
                        if (StringUtils.startsWith(pathCandidate, root1.getPath())) {
                            String path2 = SlingResourceUtil.appendPaths(root2.getPath(),
                                    SlingResourceUtil.relativePath(root1.getPath(), pathCandidate));
                            Resource resource2candidate = path2 != null ? resolver2.getResource(path2) : null;
                            if (resource2candidate != null) {
                                // This is an attribute that references something in root1 which is also present in root2.
                                // Very suspicous - should normally be replaced by the in-place replication.
                                result.add(resource.getPath() + "/" + entry.getKey());
                            }
                        } else { // search something looking like a path in rich text
                            Matcher matcher = pathPattern.matcher(pathCandidate);
                            while (matcher.find()) {
                                String possiblePath = matcher.group();
                                String path2 = SlingResourceUtil.appendPaths(root2.getPath(),
                                        SlingResourceUtil.relativePath(root1.getPath(), possiblePath));
                                Resource resource2candidate = path2 != null ? resolver2.getResource(path2) : null;
                                if (resource2candidate != null) {
                                    result.add(resource.getPath() + "/" + entry.getKey());
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // this is rather unimportant as it is just warnings, and
            // it's quite possible that we exceed the limit of traversed nodes for the query.
            LOG.warn("Trouble looking for suspicious attributes in {}", root2.getPath());
            result = null;
        }
        return result;
    }

}
