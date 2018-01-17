package com.composum.pages.commons.model;

import com.composum.sling.core.BeanContext;
import com.composum.sling.core.SlingBean;
import org.apache.sling.api.resource.Resource;

public class GenericModel extends ModelWrapper implements SlingBean {

    /** For instantiation with {@link BeanContext#adaptTo(Class)}. */
    public GenericModel() {
        // empty
    }

    public GenericModel(BeanContext context, Resource resource) {
        initialize(context, resource);
    }

    /** @deprecated the normal instantiation mechanism is by using the constructor. */
    @Override
    @Deprecated
    public void initialize(BeanContext context, Resource resource) {
        if (Site.isSite(resource)) {
            model = new Site(context, resource);
        } else if (Page.isPage(resource)) {
            model = new Page(context, resource);
        } else if (Container.isContainer(context.getResource().getResourceResolver(), resource, null)) {
            model = new Container(context, resource);
        } else {
            model = new Element(context, resource);
        }
    }

    /** @deprecated the normal instantiation mechanism is by using the constructor. */
    @Override
    @Deprecated
    public void initialize(BeanContext context) {
        initialize(context, context.getResource());
    }
}
