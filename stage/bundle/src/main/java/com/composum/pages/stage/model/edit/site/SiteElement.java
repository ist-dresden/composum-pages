package com.composum.pages.stage.model.edit.site;

import com.composum.pages.commons.model.Site;
import com.composum.pages.stage.model.edit.FrameElement;

public class SiteElement extends FrameElement {

    private transient Site site;

    public Site getSite() {
        if (site == null) {
            site = getSiteManager().createBean(context, getElementResource());
        }
        return site;
    }
}
