package com.composum.pages.commons.model;

import com.composum.pages.commons.service.PagesResourceManager;
import com.composum.pages.commons.service.PagesThemeManager;
import com.composum.pages.commons.service.ResourceManager;
import com.composum.sling.core.BeanContext;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.apache.sling.testing.mock.sling.servlet.MockServletContext;
import org.junit.Rule;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.ServletContext;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TemplateAndDesignTest {

    @Rule
    public final SlingContext context = new SlingContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

    protected ServletContext servletContext = new MockServletContext();
    protected BeanContext bc = new BeanContext.Servlet(servletContext, context.bundleContext(), context.request(),
            context.response());


    protected class TestThemeManager extends PagesThemeManager {
    }

    protected class TestResourceManager extends PagesResourceManager {

        protected Map<Serializable, Template> cache = new HashMap<>();

        public TestResourceManager() {
            themeManager = new TestThemeManager();
        }

        @Override
        @Nullable
        public Template get(@Nonnull Serializable key) {
            return cache.get(key);
        }

        @Override
        public void put(@Nonnull Serializable key, @Nullable Template value) {
            if (value == null) {
                cache.remove(key);
            } else {
                cache.put(key, value);
            }
        }
    }

    protected ResourceManager resourceManager = new TestResourceManager();

    @Test
    public void findDesign() {
        context.load().json("/test/template/emptyFolder.json", "/apps");
        context.load().json("/test/template/emptyFolder.json", "/libs");
        context.load().json("/test/template/findDesign.json", "/content/test");
        ResourceResolver resolver = context.resourceResolver();
        Resource templateResource = resolver.getResource("/content/test/template");
        ResourceManager.Template template = resourceManager.toTemplate(templateResource);
        Resource pageResource = resolver.getResource("/content/test/page");
        Resource pageContent = pageResource.getChild(JcrConstants.JCR_CONTENT);
        findDesign(template, pageContent, "main/row-1/column-0", null,
                "/content/test/template/jcr:content/main/cpp:design/column");
        findDesign(template, pageContent, "main/row-1/column-0/textimage", null,
                "/content/test/template/jcr:content/main/cpp:design/column/text");
        findDesign(template, pageContent, "main/pages", null,
                "/content/test/template/jcr:content/main/cpp:design/text");
        findDesign(template, pageContent, "main/nonexisting",
                "composum/pages/components/element/title",
                "/content/test/template/jcr:content/main/cpp:design/text");
        findDesign(template, pageContent, "other/row-2/column-1", null,
                "/content/test/template/jcr:content/cpp:design/column");
        findDesign(template, pageContent, "other/row-2/column-1/textimage", null,
                "/content/test/template/jcr:content/cpp:design/column/text");
        findDesign(template, pageContent, "other/row-2/column-1/nonexisting",
                "composum/pages/components/element/textimage",
                "/content/test/template/jcr:content/cpp:design/column/text");
    }

    protected void findDesign(ResourceManager.Template template, Resource pageContent,
                              String elementPath, String resourceType, String expected) {
        ResourceManager.Design design = template.getDesign(pageContent, elementPath, resourceType);
        assertNotNull(design);
        assertEquals(expected, design.getPath());
    }

    @Test
    public void useDesignProperty() {
        context.load().json("/test/template/emptyFolder.json", "/apps");
        context.load().json("/test/template/emptyFolder.json", "/libs");
        context.load().json("/test/template/findDesign.json", "/content/test");
        ResourceResolver resolver = context.resourceResolver();
        useDesignProperty(resolver.getResource("/content/test/page/jcr:content/main/row-1/column-1/textimage"),
                "main/cpp:design/column/text");
        useDesignProperty(resolver.getResource("/content/test/page/jcr:content/main/row-1/column-0/textimage"),
                "page/main/row-1/column.0/textimage");
        useDesignProperty(resolver.getResource("/content/test/page/jcr:content/other/row-2/column-0/textimage"),
                "cpp:design/column/text");
        useDesignProperty(resolver.getResource("/content/test/page/jcr:content/main/pages"),
                "main/cpp:design/text");
        useDesignProperty(resolver.getResource("/content/test/page/jcr:content/other/pages"),
                "cpp:design/text");
        useDesignProperty("/content/test/page/jcr:content/other/nonexisting",
                "composum/pages/components/element/title",
                "cpp:design/text");
    }

    protected void useDesignProperty(Resource resource, String expected) {
        assertNotNull(resource);
        ResourceManager.ResourceReference reference = resourceManager.getReference(resource, null);
        String property = reference.getProperty("property", "");
        assertEquals(expected, property);
    }

    protected void useDesignProperty(String path, String resourceType, String expected) {
        ResourceManager.ResourceReference reference = resourceManager
                .getReference(context.resourceResolver(), path, resourceType);
        String property = reference.getProperty("property", "");
        assertEquals(expected, property);
    }
}
