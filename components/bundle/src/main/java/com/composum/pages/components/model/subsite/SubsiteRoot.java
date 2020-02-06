package com.composum.pages.components.model.subsite;

import com.composum.pages.commons.model.Image;
import com.composum.pages.commons.model.Page;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nullable;

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
    @Nullable
    protected Resource determineResource(@Nullable Resource initialResource) {
        Page page = getCurrentPage();
        while (page != null && !page.getContent().getResource().isResourceType(SUBSITE_ROOT_RESOURCE_TYPE)) {
            page = page.getParentPage();
        }
        return page != null ? page.getResource() : initialResource;
    }

    // properties

    @Override
    @Nullable
    public Image getLogo() {
        if (logo == null) {
            Resource logoRes = getContent().getResource().getChild(LOGO_PATH);
            if (logoRes != null) {
                logo = new Image(context, logoRes);
            }
        }
        return logo;
    }
}
