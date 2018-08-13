package com.composum.pages.stage.model.edit.page;

import com.composum.pages.commons.model.Page;
import com.composum.pages.stage.model.edit.FrameModel;

public class PageModel extends FrameModel {

    private transient Page page;

    public Page getPage() {
        if (page == null) {
            page = (Page) getDelegate();
        }
        return page;
    }
}
