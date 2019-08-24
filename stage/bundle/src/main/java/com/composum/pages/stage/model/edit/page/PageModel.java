package com.composum.pages.stage.model.edit.page;

import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.service.PageManager;
import com.composum.pages.stage.model.edit.FrameModel;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

public class PageModel extends FrameModel {

    private transient Page page;

    public Page getPage() {
        if (page == null) {
            page = (Page) getDelegate();
        }
        return page;
    }

    /**
     * retrieves the path of the element to handle by the frame element using the suffix of the request
     */
    public String getDelegatePath(BeanContext context) {
        String delegatePath = super.getDelegatePath(context);
        PageManager pageManager = context.getService(PageManager.class);
        Resource resource = context.getResolver().getResource(delegatePath);
        if (resource != null) {
            Page page = pageManager.getContainingPage(context, resource);
            if (page != null) {
                return page.getPath();
            }
        }
        return delegatePath;
    }
}
