package com.composum.pages.commons.model;

import com.composum.pages.commons.service.PageManager;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Homepage extends Page {

    private static final Logger LOG = LoggerFactory.getLogger(Homepage.class);

    public static final String LOGO_PATH = "logo";

    private transient Image logo;
    private transient String logoUrl;

    public Homepage() {
    }

    protected Homepage(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public Homepage(PageManager manager, BeanContext context, Resource resource) {
        this.pageManager = manager;
        initialize(context, resource);
    }

    public String getLogoUrl() {
        if (logoUrl == null) {
            Image logo = getLogo();
            logoUrl = logo.getImageUrl();
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
