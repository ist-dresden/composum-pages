package com.composum.pages.components.model.page;

import com.composum.pages.commons.model.Image;
import com.composum.pages.commons.model.Page;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nullable;

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
    @Nullable
    protected Resource determineResource(@Nullable Resource initialResource) {
        Resource resource = initialResource;
        if (resource == null) {
            Page currentPage = getCurrentPage();
            if (currentPage != null) {
                resource = currentPage.getResource();
            }
        }
        return resource;
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
