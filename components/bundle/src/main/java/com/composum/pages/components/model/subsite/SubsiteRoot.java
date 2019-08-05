package com.composum.pages.components.model.subsite;

import com.composum.pages.commons.model.Image;
import com.composum.pages.commons.model.Page;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

import static com.composum.pages.commons.model.Homepage.LOGO_PATH;

public class SubsiteRoot extends Page {

    public static final String SUBSITE_ROOT_RESOURCE_TYPE = "composum/pages/components/page/subsite/root";

    private transient Image logo;
    private transient String logoUrl;

    public SubsiteRoot() {
    }

    public SubsiteRoot(BeanContext context) {
        super(context, null);
    }

    // initializer extensions

    @Override
    protected Resource determineResource(Resource initialResource) {
        Page page = getCurrentPage();
        while (page != null && !page.getContent().getResource().isResourceType(SUBSITE_ROOT_RESOURCE_TYPE)) {
            page = page.getParentPage();
        }
        return page != null ? page.getResource() : initialResource;
    }

    // properties

    public String getLogoUrl() {
        if (logoUrl == null) {
            Image logo = getLogo();
            logoUrl = logo.getAssetUrl();
        }
        return logoUrl;
    }

    public Image getLogo() {
        if (logo == null) {
            logo = new Image(context, getContent().getResource().getChild(LOGO_PATH));
        }
        return logo;
    }
}
