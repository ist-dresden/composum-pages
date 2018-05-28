package com.composum.pages.stage.model.edit;

import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.properties.Language;
import com.composum.pages.commons.request.DisplayMode;
import com.composum.sling.core.util.LinkUtil;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import java.io.IOException;

public class FramePage extends Page {

    private transient Page page;
    private transient Resource pageResource;
    private transient String pagePath;

    public FramePage() {
        super();
    }

    public Page getPage() {
        if (page == null) {
            page = getPageManager().createBean(context, getPageResource());
        }
        return page;
    }

    public Resource getPageResource() {
        if (pageResource == null) {
            String path = getPagePath();
            if (pageResource == null) {
                pageResource = resolver.resolve(path);
            }
        }
        return pageResource;
    }

    public String getPagePath() {
        if (pagePath == null) {
            pagePath = context.getRequest().getRequestPathInfo().getSuffix();
            if (StringUtils.isBlank(pagePath)) {
                pagePath = "/";
            } else {
                pageResource = resolver.resolve(pagePath);
                if (!ResourceUtil.isNonExistingResource(pageResource)) {
                    pagePath = pageResource.getPath();
                } else {
                    if (pagePath.endsWith(".html")) {
                        pagePath = pagePath.substring(0, pagePath.length() - 5);
                    }
                }
            }
        }
        return pagePath;
    }

    public String getPageUrl() {
        return LinkUtil.getUrl(context.getRequest(), getPagePath());
    }

    public void redirectToPage() throws IOException {
        context.getResponse().sendRedirect(getPageUrl());
    }

    public String getLanguageHint() {
        Language language = getPage().getLanguage();
        return language != null ? language.getKey().toLowerCase().replace('_', '.') : "";
    }

    public String getDisplayModeHint() {
        DisplayMode.Value mode = getDisplayMode();
        switch (mode) {
            case DEVELOP:
                mode = DisplayMode.Value.EDIT;
            default:
                break;
        }
        return mode.name();
    }

    public String getDisplayModeSelector() {
        return getDisplayModeHint().toLowerCase();
    }

    @Override
    public DisplayMode.Value getDisplayMode() {
        return DisplayMode.requested(context);
    }
}
