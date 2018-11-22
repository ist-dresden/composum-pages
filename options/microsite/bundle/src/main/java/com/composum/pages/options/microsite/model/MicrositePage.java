package com.composum.pages.options.microsite.model;

import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.util.LinkUtil;
import com.composum.sling.core.util.LinkMapper;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MicrositePage extends Page {

    /**
     * @return the URL for the POST action to upload a ZIP file as content of the Microsite
     */
    public String getContentUploadUrl() {
        return LinkUtil.getUrl(getContext().getRequest(), getContent().getPath(), "upload", null);
    }

    public String getEmbeddedPreviewUrl() {
        return LinkUtil.getUrl(getContext().getRequest(), getPath(), "embedded", null, LinkMapper.CONTEXT);
    }

    public String getLastImportTime() {
        Calendar calendar = getProperty("lastImportTime", Calendar.class);
        return calendar != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()) : "";
    }
}
