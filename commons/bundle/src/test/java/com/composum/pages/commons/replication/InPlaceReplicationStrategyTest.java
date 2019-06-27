package com.composum.pages.commons.replication;

import com.composum.pages.commons.model.Site;
import com.composum.pages.commons.service.SiteManager;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.ResourceHandle;
import com.composum.sling.core.filter.ResourceFilter;
import com.composum.sling.platform.security.AccessMode;
import com.composum.sling.platform.testing.testutil.ErrorCollectorAlwaysPrintingFailures;
import com.composum.sling.platform.testing.testutil.JcrTestUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.resourcebuilder.api.ResourceBuilder;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.composum.pages.commons.model.Site.DEFAULT_PUBLIC_MODE;
import static com.composum.pages.commons.model.Site.PROP_PUBLIC_MODE;
import static com.composum.sling.core.util.CoreConstants.CONTENT_NODE;
import static com.composum.sling.core.util.CoreConstants.PROP_PRIMARY_TYPE;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** Some tests for {@link InPlaceReplicationStrategy}. */
public class InPlaceReplicationStrategyTest {

    @Rule
    public final SlingContext context = new SlingContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

    private Resource pageContent;
    private BeanContext beanContext;
    private Site site;
    private ReplicationStrategy service;

    @Rule
    public final ErrorCollectorAlwaysPrintingFailures ec = new ErrorCollectorAlwaysPrintingFailures().onFailure(
            () -> JcrTestUtils.printResourceRecursivelyAsJson(context.resourceResolver().getResource("/preview"))
    );

    @Before
    public void setUp() {
        ResourceBuilder parentBuilder = context.build().resource("/content/ist/composum", PROP_PRIMARY_TYPE, "cpp:Site");
        parentBuilder.resource(CONTENT_NODE, PROP_PRIMARY_TYPE, "cpp:SiteConfiguration", PROP_PUBLIC_MODE, DEFAULT_PUBLIC_MODE);
        ResourceBuilder resourceBuilder = parentBuilder.resource("something", PROP_PRIMARY_TYPE, "cpp:Page")
                .resource(CONTENT_NODE, PROP_PRIMARY_TYPE, "cpp:PageContent",
                        "text", "&lt;p>&lt;a href=&quot;/content/ist/composum/home/pages&quot; style=&quot;background-color: rgb(255, 255, 255);&quot;>more&lt;/a>...&lt;/p>",
                        "link", "/content/ist/composum/meta/search",
                        "imageRef", "/content/ist/composum/assets/background/gravitational_waves.jpg",
                        "codeRef", "/content/ist/composum/home/pages/development/components/_jcr_content/main/row_844558834/column-0/teaser.html",
                        "nonexists", "/content/ist/composum/nixgibs"
                ).commit();
        for (String path : new String[]{"/content/ist/composum/home/pages", "/content/ist/composum/meta/search",
                "/content/ist/composum/assets/background/gravitational_waves.jpg",
                "/content/ist/composum/home/pages/development/components/_jcr_content/main/row_844558834/column-0/teaser"}) {
            context.build().resource(path).commit();
        }
        pageContent = resourceBuilder.getCurrentParent();
        beanContext = new BeanContext.Service(context.resourceResolver());
        site = new Site(mock(SiteManager.class), beanContext, parentBuilder.getCurrentParent()) {
            @Override
            protected Resource determineResource(Resource initialResource) {
                return initialResource;
            }
        };

        service = new InPlacePageReplication();

        ReplicationManager replicationManager = mock(ReplicationManager.class);
        PagesReplicationConfig config = mock(PagesReplicationConfig.class);
        when(replicationManager.getConfig()).thenReturn(config);
        when(config.inPlacePreviewPath()).thenReturn("/preview");
        when(config.contentPath()).thenReturn("/content");
        when(config.inPlaceEnabled()).thenReturn(true);
        service.activate(replicationManager);

        context.build().resource(config.inPlacePreviewPath()).commit();
    }

    @Test
    public void testReplacement() throws Exception {
        ReplicationContext ctx = new ReplicationContext(beanContext, site, AccessMode.PREVIEW, ResourceFilter.ALL, context.resourceResolver());
        ec.checkThat(service.canReplicate(ctx, pageContent, true), is(true));
        service.replicate(ctx, pageContent, true);

        ResourceHandle target = ResourceHandle.use(context.resourceResolver().getResource("/preview/ist/composum/something/jcr:content"));
        ec.checkThat(target.isValid(), is(true));
        ec.checkThat(target.getProperty("link"), is("/preview/ist/composum/meta/search"));
        ec.checkThat(target.getProperty("imageRef"), is("/preview/ist/composum/assets/background/gravitational_waves.jpg"));
        ec.checkThat(target.getProperty("codeRef"), is("/preview/ist/composum/home/pages/development/components/_jcr_content/main/row_844558834/column-0/teaser.html"));
        ec.checkThat(target.getProperty("text"), Matchers.containsString("/preview/ist/composum/home/pages"));
        // not existent -> not replaced
        ec.checkThat(target.getProperty("nonexists"), is("/content/ist/composum/nixgibs"));

    }

}
