package com.composum.pages.commons.service;

import com.composum.sling.core.util.ResourceUtil;
import com.composum.sling.platform.testing.testutil.ErrorCollectorAlwaysPrintingFailures;
import com.composum.sling.platform.testing.testutil.JcrTestUtils;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.resourcebuilder.api.ResourceBuilder;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.version.Version;
import javax.jcr.version.VersionManager;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class PagesVersionsServiceTest {

    @Rule
    public final SlingContext context = new SlingContext(ResourceResolverType.JCR_OAK);

    @Rule
    public final ErrorCollectorAlwaysPrintingFailures ec = new ErrorCollectorAlwaysPrintingFailures();

    protected ResourceResolver resolver;
    protected PagesVersionsService service;

    @Before
    public void setup() {
        resolver = context.resourceResolver();
        service = new PagesVersionsService(); // nothing injected so far since not needed for the current tests
    }

    @Test
    public void testHistoricalVersion() throws RepositoryException, PersistenceException {
        ResourceBuilder builder = context.build().resource("/content/something/versionable/jcr:content",
                ResourceUtil.PROP_PRIMARY_TYPE, ResourceUtil.TYPE_UNSTRUCTURED,
                ResourceUtil.PROP_MIXINTYPES, new String[]{ResourceUtil.MIX_VERSIONABLE}).commit();
        Resource versionable = builder.getCurrentParent();
        // ec.onFailure(() -> JcrTestUtils.printResourceRecursivelyAsJson(versionable));
        String inFirstVersionPath = builder.resource("in/firstversion").commit().getCurrentParent().getPath();
        String inFirstVersionThenRemovedPath = builder.resource("in/firstversionButNotWorkspace").commit().getCurrentParent().getPath();
        JcrTestUtils.printResourceRecursivelyAsJson(versionable);

        Workspace workspace = resolver.adaptTo(Session.class).getWorkspace();
        VersionManager versionManager = workspace.getVersionManager();
        Version version = versionManager.checkpoint(versionable.getPath());
        String versionUuid = version.getIdentifier();

        String onlyInWorkspacePath = builder.resource("in/workspace").commit().getCurrentParent().getPath();
        resolver.delete(resolver.getResource(inFirstVersionThenRemovedPath));
        resolver.commit();

        ec.checkThat(resolver.getResource(versionable.getPath()), notNullValue());
        ec.checkThat(resolver.getResource(inFirstVersionPath), notNullValue());
        ec.checkThat(resolver.getResource(inFirstVersionThenRemovedPath), nullValue());
        ec.checkThat(resolver.getResource(onlyInWorkspacePath), notNullValue());

        ec.checkThat(service.historicalVersion(resolver, versionable.getPath(), versionUuid), notNullValue());
        ec.checkThat(service.historicalVersion(resolver, inFirstVersionPath, versionUuid), notNullValue());
        ec.checkThat(service.historicalVersion(resolver, inFirstVersionThenRemovedPath, versionUuid), notNullValue());
        ec.checkThat(service.historicalVersion(resolver, onlyInWorkspacePath, versionUuid), nullValue());

        String otherversionid;
        {
            String otherpath = context.build().resource("/content/other/versionable/jcr:content",
                    ResourceUtil.PROP_PRIMARY_TYPE, ResourceUtil.TYPE_UNSTRUCTURED,
                    ResourceUtil.PROP_MIXINTYPES, new String[]{ResourceUtil.MIX_VERSIONABLE}).commit().getCurrentParent().getPath();
            Version version2 = versionManager.checkpoint(otherpath);
            otherversionid = version2.getIdentifier();
        }

        // outside of the scope of the version: always null
        ec.checkThat(service.historicalVersion(resolver, versionable.getPath(), otherversionid), nullValue());
        ec.checkThat(service.historicalVersion(resolver, inFirstVersionPath, otherversionid), nullValue());
        ec.checkThat(service.historicalVersion(resolver, inFirstVersionThenRemovedPath, otherversionid), nullValue());
        ec.checkThat(service.historicalVersion(resolver, onlyInWorkspacePath, otherversionid), nullValue());

        ec.checkThat(service.historicalVersion(resolver, versionable.getParent().getPath(), otherversionid), nullValue());
    }
}
