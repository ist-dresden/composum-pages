package com.composum.pages.commons.model;

import com.composum.sling.core.BeanContext;
import com.composum.sling.core.SlingBean;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;

public class GenericModel extends ModelWrapper implements SlingBean {

    /**
     * For instantiation with {@link BeanContext#adaptTo(Class)}.
     */
    public GenericModel() {
        // empty
    }

    public GenericModel(BeanContext context, Resource resource) {
        initialize(context, resource);
    }

    /**
     * @deprecated the normal instantiation mechanism is by using the constructor.
     */
    @Override
    @Deprecated
    public void initialize(BeanContext context, Resource resource) {
        resource = determineDelegateResource(context, resource);
        delegate = createDelegate(context, resource);
    }

    @Nonnull
    protected Model createDelegate(BeanContext context, Resource resource) {
        if (Site.isSite(resource)) {
            return new Site(context, resource);
        } else if (Page.isPage(resource)) {
            return new Page(context, resource);
        } else if (Page.isPageContent(resource)) {
            return new PageContent(context, resource);
        } else if (Folder.isFolder(resource)) {
            return new Folder(context, resource);
        } else if (File.isFile(resource)) {
            return new File(context, resource);
        } else if (Component.isComponent(resource)) {
            return new Component(context, resource);
        } else if (Container.isContainer(context.getResource().getResourceResolver(), resource, null)) {
            return new Container(context, resource);
        } else {
            return new Element(context, resource);
        }
    }

    /**
     * @deprecated the normal instantiation mechanism is by using the constructor.
     */
    @Override
    @Deprecated
    public void initialize(BeanContext context) {
        initialize(context, context.getResource());
    }

    protected Resource determineDelegateResource(BeanContext context, Resource resource) {
        return resource;
    }
}
