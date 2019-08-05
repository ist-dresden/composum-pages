package com.composum.pages.components.model.page;

import com.composum.pages.commons.model.Image;
import com.composum.pages.commons.model.Page;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

import static com.composum.pages.commons.model.Homepage.LOGO_PATH;

public class SinglePage extends Page {

    private transient Image logoImage;
    private transient String logoImageUrl;
    private transient String logoLinkUrl;

    public SinglePage() {
    }

    public SinglePage(BeanContext context) {
        super(context, null);
    }

    // initializer extensions

    @Override
    protected Resource determineResource(Resource initialResource) {
        return initialResource == null ? getCurrentPage().getResource() : initialResource;
    }

    public String getLogoLinkUrl() {
        if (logoLinkUrl == null) {
            logoLinkUrl = getProperty("logoLink", "#");
        }
        return logoLinkUrl;
    }

    public String getLogoImageUrl() {
        if (logoImageUrl == null) {
            Image logo = getLogoImage();
            logoImageUrl = logo.getAssetUrl();
        }
        return logoImageUrl;
    }

    public Image getLogoImage() {
        if (logoImage == null) {
            logoImage = new Image(context, getContent().getResource().getChild(LOGO_PATH));
        }
        return logoImage;
    }
}
