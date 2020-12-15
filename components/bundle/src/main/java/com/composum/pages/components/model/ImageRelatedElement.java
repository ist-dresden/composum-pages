package com.composum.pages.components.model;

import com.composum.pages.commons.model.Image;
import com.composum.pages.commons.model.Link;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class ImageRelatedElement extends Link {

    public static final String IMAGE_PATH = "image";

    private transient Image image;
    private transient String imageUrl;

    public ImageRelatedElement(BeanContext context, Resource resource) {
        super(context, resource);
    }

    public ImageRelatedElement() {
    }

    @Nullable
    public Image getImage() {
        if (image == null) {
            Resource imageRes = resource.getChild(IMAGE_PATH);
            if (imageRes != null) {
                image = new Image(context, imageRes);
            }
        }
        return image;
    }

    @Nonnull
    public String getImageUrl() {
        if (imageUrl == null) {
            Image image = getImage();
            if (image != null) {
                imageUrl = image.getSrc();
            } else {
                imageUrl = "";
            }
        }
        return imageUrl;
    }
}
