package com.composum.pages.components.model.asset;

import com.composum.pages.commons.model.AssetRelated;
import com.composum.pages.commons.util.LinkUtil;
import com.composum.sling.core.BeanContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

public class Video extends AssetRelated {

    public static final String PN_VIDEO_REF = "videoRef";
    public static final String PN_CONTROLS = "controls";
    public static final String PN_AUTOPLAY = "autoplay";
    public static final String PN_MUTED = "muted";
    public static final String PN_LOOP = "loop";
    public static final String PN_POSTER_REF = "posterRef";

    private transient String src;
    private transient String poster;

    public Video() {
    }

    public Video(BeanContext context, Resource resource) {
        super(context, resource);
    }

    @Override
    protected String getRefPropName() {
        return PN_VIDEO_REF;
    }

    public String getSrc() {
        if (src == null) {
            src = getAssetUrl();
            if (StringUtils.isBlank(src)) {
                src = LinkUtil.getUrl(context.getRequest(), getPlaceholder());
            }
        }
        return src;
    }

    public boolean getControls() {
        return getProperty(PN_CONTROLS, Boolean.FALSE);
    }

    public boolean getAutoplay() {
        return getProperty(PN_AUTOPLAY, Boolean.FALSE);
    }

    public boolean getMuted() {
        return getProperty(PN_MUTED, Boolean.FALSE);
    }

    public boolean getLoop() {
        return getProperty(PN_LOOP, Boolean.FALSE);
    }

    public String getPoster() {
        if (poster == null) {
            String uri = getProperty(PN_POSTER_REF, "");
            poster = StringUtils.isNotBlank(uri) ? LinkUtil.getUrl(context.getRequest(), uri) : "";
        }
        return poster;
    }

    public String getPosterAttr() {
        String poster = getPoster();
        return StringUtils.isNotBlank(poster) ? " poster=\"" + poster + "\"" : "";
    }
}
