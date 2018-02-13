package com.composum.pages.options.microsite.model;

import com.composum.pages.commons.model.Page;
import com.composum.sling.core.util.LinkUtil;

public class MicrositePage extends Page {

    /**
     * @return the URL for the POST action to upload a ZIP file as content of the Microsite
     */
    public String getContentUploadUrl() {
        return LinkUtil.getUrl(getContext().getRequest(), getContent().getPath(), "upload", null);
    }
}
