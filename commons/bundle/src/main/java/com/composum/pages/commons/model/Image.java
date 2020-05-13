package com.composum.pages.commons.model;

import com.composum.pages.commons.util.LinkUtil;
import com.composum.sling.core.BeanContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class Image extends AssetRelated {

    private static final Logger LOG = LoggerFactory.getLogger(Image.class);

    public static final String PROP_IMAGE_REF = "imageRef";
    public static final String PROP_ALT = "alt";

    private transient String src;
    private transient String alt;

    private transient BufferedImage image;
    private transient Integer width;
    private transient Integer height;

    public Image() {
    }

    public Image(BeanContext context, Resource resource) {
        super(context, resource);
    }

    protected String getRefPropName() {
        return PROP_IMAGE_REF;
    }

    public boolean isValid() {

        if (valid == null) {
            File file = getAssetFile();
            valid = file != null && file.isValid();
        }
        return valid;
    }

    public String getSrc() {
        if (src == null) {
            src = getAssetUrl();
            if (StringUtils.isBlank(src)) {
                src = LinkUtil.getUrl(context.getRequest(), getPlaceholder(), "");
            }
        }
        return src;
    }

    public String getAlt() {
        if (alt == null) {
            alt = getProperty(PROP_ALT, "");
        }
        return alt;
    }

    public int getWidth() {
        if (width == null) {
            BufferedImage image = getImage();
            if (image != null) {
                width = image.getWidth();
            }
        }
        return width != null ? width : 0;
    }

    public int getHeight() {
        if (height == null) {
            BufferedImage image = getImage();
            if (image != null) {
                height = image.getHeight();
            }
        }
        return height != null ? height : 0;
    }

    public BufferedImage getImage() {
        if (image == null) {
                try (InputStream stream = openInputStream()){
                    if (stream != null) {
                        image = ImageIO.read(stream);
                    } else {
                        valid = false;
                    }
                } catch (IOException ex) {
                    LOG.error(ex.getMessage(), ex);
                    valid = false;
                }
        }
        return image;
    }
}
