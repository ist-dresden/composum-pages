package com.composum.pages.components.model;

import com.composum.pages.commons.model.Image;
import com.composum.pages.commons.model.Link;

public abstract class ImageRelatedElement extends Link {

    public static final String IMAGE_PATH = "image";

    private transient Image image;
    private transient String imageUrl;

    public Image getImage() {
        if (image == null) {
            image = new Image(context, resource.getChild(IMAGE_PATH));
        }
        return image;
    }

    public String getImageUrl() {
        if (imageUrl == null) {
            Image image = getImage();
            imageUrl = image.getSrc();
        }
        return imageUrl;
    }
}
