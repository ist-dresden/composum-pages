package com.composum.pages.commons.model;

import com.composum.sling.core.AbstractSlingBean;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.SlingBean;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.resourcebuilder.api.ResourceBuilder;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.apache.sling.testing.mock.sling.servlet.MockServletContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;

import static org.apache.jackrabbit.JcrConstants.JCR_PRIMARYTYPE;
import static org.apache.jackrabbit.JcrConstants.NT_UNSTRUCTURED;
import static org.junit.Assert.*;

/**
 * Verifies that Sling Models and Composum {@link Model} work together.
 *
 * @author Hans-Peter Stoerr
 */
public class SlingAndComposumModelsTest {

    @Rule
    public final SlingContext context = new SlingContext(ResourceResolverType.RESOURCERESOLVER_MOCK);

    protected ServletContext servletContext = new MockServletContext();
    protected BeanContext bc = new BeanContext.Servlet(servletContext, context.bundleContext(), context.request(),
            context.response());

    @Before
    public void setup() {
        context.addModelsForPackage(getClass().getPackage().getName());
    }

    @Test
    public void normalModelWithAdaptTo() {
        context.build().siblingsMode().resource("/libs").resource("/apps");
        Resource resource = context.build().resource("/whatever", JCR_PRIMARYTYPE, NT_UNSTRUCTURED).commit()
                .getCurrentParent();
        context.request().setResource(resource);

        GenericModel model = bc.adaptTo(GenericModel.class);
        assertNotNull(model);
        assertSame(resource, model.getResource());
        assertTrue(model.model instanceof Element);
    }

    @Test
    public void instantiateNonSlingModelsSlingBean() {
        assertNull(bc.adaptTo(SlingBean.class));
        assertNull(bc.adaptTo(AbstractSlingBean.class));
        TestingSlingBean testBean = bc.adaptTo(TestingSlingBean.class);
        assertNotNull(testBean);
        assertTrue(testBean.initialized);
    }

    public static class TestingSlingBean extends AbstractSlingBean {
        boolean initialized;

        @Override
        public void initialize(BeanContext context) {
            initialized = true;
        }
    }

    /**
     * BeanContext initializes SlingBeans itself if sling-models doesn't, but should not when they are actually
     * Sling-Models and adaptTo returns null because of errors.
     */
    @Test
    public void doNotInstantiateBrokenSlingBean() {
        assertNull(bc.adaptTo(TestingBrokenSlingModelsBean.class));
    }

    @org.apache.sling.models.annotations.Model(adaptables = BeanContext.class)
    public static class TestingBrokenSlingModelsBean extends AbstractSlingBean {
        @Inject
        boolean cannotBeInitialized;
    }


    @Test
    public void generateComposumModelAndSlingModel() {
        ResourceBuilder resourceBuilder = context.build().resource("/whatever", "anint", 88);
        Resource resource = resourceBuilder.commit().getCurrentParent();
        resourceBuilder.resource("subpath", "avalue", "21");
        context.request().setResource(resource);
        TestBothModel model = bc.adaptTo(TestBothModel.class);
        assertEquals(88, model.anint);
        assertEquals(21, model.avalue);
        assertSame(resource, model.getResource());
    }

    @org.apache.sling.models.annotations.Model(adaptables = BeanContext.class)
    public static class TestBothModel extends AbstractModel {
        @Inject
        int anint;

        @Inject
        @Named("subpath/avalue")
        int avalue;
    }

}
