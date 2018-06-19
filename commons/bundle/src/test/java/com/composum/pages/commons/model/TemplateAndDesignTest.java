package com.composum.pages.commons.model;

import com.composum.sling.core.BeanContext;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.apache.sling.testing.mock.sling.servlet.MockServletContext;
import org.junit.Rule;
import org.junit.Test;

import javax.servlet.ServletContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TemplateAndDesignTest {

    @Rule
    public final SlingContext context = new SlingContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

    protected ServletContext servletContext = new MockServletContext();
    protected BeanContext bc = new BeanContext.Servlet(servletContext, context.bundleContext(), context.request(),
            context.response());

    @Test
    public void findDesign() {
        context.load().json("/test/template/findDesign.json", "/content/test");
        ResourceResolver resolver = context.resourceResolver();
        Resource templateResource = resolver.getResource("/content/test/template");
        Template template = new Template(templateResource);
        Resource pageResource = resolver.getResource("/content/test/page");
        Resource pageContent = pageResource.getChild(JcrConstants.JCR_CONTENT);
        findDesign(template, pageContent, "main/row-1/column-0",
                "/content/test/template/jcr:content/main/cpp:design/column");
        findDesign(template, pageContent, "main/row-1/column-0/textimage",
                "/content/test/template/jcr:content/main/cpp:design/column/text");
        findDesign(template, pageContent, "main/pages",
                "/content/test/template/jcr:content/main/cpp:design/text");
        findDesign(template, pageContent, "other/row-2/column-1",
                "/content/test/template/jcr:content/cpp:design/column");
        findDesign(template, pageContent, "other/row-2/column-1/textimage",
                "/content/test/template/jcr:content/cpp:design/column/text");
    }

    protected void findDesign(Template template, Resource pageContent, String elementPath, String expected) {
        Resource element = pageContent.getChild(elementPath);
        assertNotNull(element);
        context.currentResource(element);
        Design design = template.findDesign(template.getContentResource(), pageContent, elementPath);
        assertNotNull(design);
        assertEquals(expected, design.resource.getPath());
    }
}
