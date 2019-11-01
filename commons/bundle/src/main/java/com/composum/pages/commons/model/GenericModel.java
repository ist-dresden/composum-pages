package com.composum.pages.commons.model;

import com.composum.sling.core.BeanContext;
import com.composum.sling.core.SlingBean;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

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
        resource = determineDelegateResource(context, resource);
        if (Site.isSite(resource)) {
            delegate = new Site(context, resource);
        } else if (Page.isPage(resource)) {
            delegate = new Page(context, resource);
        } else if (Folder.isFolder(resource)) {
            delegate = new Folder(context, resource);
        } else if (File.isFile(resource)) {
            delegate = new File(context, resource);
        } else if (Component.isComponent(resource)) {
            delegate = new Component(context, resource);
        } else if (Container.isContainer(context.getResource().getResourceResolver(), resource, null)) {
            delegate = new Container(context, resource);
        } else {
            delegate = new Element(context, resource);
        }
    }

    /** @deprecated the normal instantiation mechanism is by using the constructor. */
    @Override
    @Deprecated
    public void initialize(BeanContext context) {
        initialize(context, context.getResource());
    }

    protected Resource determineDelegateResource (BeanContext context, Resource resource) {
        return resource;
    }
}
