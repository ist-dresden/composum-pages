package com.composum.pages.stage.model.edit.site;

import com.composum.pages.commons.model.Element;
import com.composum.pages.commons.model.Model;
import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.Site;
import com.composum.pages.stage.model.edit.FrameModel;
import org.apache.sling.api.resource.Resource;

public class SiteModel extends FrameModel {

    private transient Site site;

    public Site getSite() {
        if (site == null) {
            Resource resource = getFrameResource(); // try to use the 'frame'
            if (Site.isSite(resource) || Site.isSiteConfiguration(resource)) {
                site = getSiteManager().createBean(getContext(), resource);
            } else { // try to use the delegate (reference from suffix)
                Model delegate = getDelegate();
                if (delegate instanceof Site) {
                    site = (Site) delegate;
                } else if (delegate instanceof Page) {
                    site = ((Page) delegate).getSite();
                } else if (delegate instanceof Element) {
                    Page page = delegate.getContainingPage();
                    if (page != null) {
                        site = page.getSite();
                    } else {
                        site = getSiteManager().getContainingSite(delegate);
                    }
                }
            }
        }
        return site;
    }
}
