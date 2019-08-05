package com.composum.pages.components.model.asset;

import com.composum.pages.commons.model.AssetRelated;
import com.composum.pages.commons.util.LinkUtil;
import com.composum.sling.core.BeanContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

public class Video extends AssetRelated {

    public static final String PROP_VIDEO_REF = "videoRef";

    private transient String src;

    public Video() {
    }

    public Video(BeanContext context, Resource resource) {
        super(context, resource);
    }

    @Override
    protected String getRefPropName() {
        return PROP_VIDEO_REF;
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
}
