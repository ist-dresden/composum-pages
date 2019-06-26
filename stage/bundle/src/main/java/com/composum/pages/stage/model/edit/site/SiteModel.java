package com.composum.pages.stage.model.edit.site;

import com.composum.pages.commons.model.Element;
import com.composum.pages.commons.model.Model;
import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.Site;
import com.composum.pages.stage.model.edit.FrameModel;

public class SiteModel extends FrameModel {

    private transient Site site;

    public Site getSite() {
        if (site == null) {
            Model delegate = getDelegate();
            if (delegate instanceof Site) {
                site = (Site) delegate;
            } else if (delegate instanceof Page) {
                site = ((Page) delegate).getSite();
            } else if (delegate instanceof Element) {
                Page containingPage = delegate.getContainingPage();
                site = containingPage != null ? containingPage.getSite() : null;
            }
        }
        return site;
    }
}
