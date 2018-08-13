package com.composum.pages.stage.model.edit.site;

import com.composum.pages.commons.model.Site;
import com.composum.pages.stage.model.edit.FrameModel;

public class SiteModel extends FrameModel {

    private transient Site site;

    public Site getSite() {
        if (site == null) {
            site = (Site) getDelegate();
        }
        return site;
    }
}
