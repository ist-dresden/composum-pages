package com.composum.pages.commons.model;

import com.composum.sling.clientlibs.handle.FileHandle;
import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.LinkUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.jcr.RepositoryException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class Image extends Element {

    private static final Logger LOG = LoggerFactory.getLogger(Image.class);

    public static final String PROP_IMAGE_REF = "imageRef";
    public static final String PROP_ALT = "alt";

    private transient String src;
    private transient String alt;

    private transient String imageRef;
    private transient String imageUrl;

    private transient Boolean valid;
    private transient FileHandle file;
    private transient BufferedImage image;
    private transient Integer width;
    private transient Integer height;

    public Image() {
    }

    public Image(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public boolean isValid() {
        if (valid == null) {
            getImage();
        }
        return valid;
    }

    protected String getPlaceholder() {
        return getProperty(PROP_PLACEHOLDER, "");
    }

    public String getImageRef() {
        if (imageRef == null) {
            imageRef = getProperty(PROP_IMAGE_REF, "");
        }
        return imageRef;
    }

    public String getImageUrl() {
        if (imageUrl == null) {
            imageUrl = getImageRef();
            if (StringUtils.isNotBlank(imageUrl)) {
                imageUrl = LinkUtil.getUrl(context.getRequest(), imageUrl);
            }
        }
        return imageUrl;
    }

    public String getSrc() {
        if (src == null) {
            src = getImageUrl();
            if (StringUtils.isBlank(src)) {
                src = LinkUtil.getUrl(context.getRequest(), getPlaceholder());
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
        if (valid == null) {
            valid = StringUtils.isNotBlank(getImageRef());
            if (valid && image == null) {
                try {
                    valid = false;
                    InputStream stream = getStream();
                    if (stream != null) {
                        image = ImageIO.read(stream);
                        valid = true;
                    }
                } catch (IOException | RepositoryException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
        }
        return image;
    }

    public InputStream getStream() throws RepositoryException {
        FileHandle file = getFile();
        if (file != null && file.isValid()) {
            return file.getStream();
        }
        return null;
    }

    public FileHandle getFile() {
        if (file == null) {
            String imageRef = getImageRef();
            if (StringUtils.isNotBlank(imageRef)) {
                Resource imageRes = resolver.getResource(imageRef);
                if (imageRes != null) {
                    file = new FileHandle(imageRes);
                }
            }
        }
        return file;
    }
}
