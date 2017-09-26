package com.composum.pages.stage.model.edit.page;

import com.composum.pages.commons.model.Page;
import com.composum.pages.stage.model.edit.FrameElement;

public class PageElement extends FrameElement {

    private transient Page page;

    public Page getPage() {
        if (page == null) {
            page = getPageManager().createBean(context, getElementResource());
        }
        return page;
    }
}
