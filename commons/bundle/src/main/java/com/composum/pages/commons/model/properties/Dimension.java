package com.composum.pages.commons.model.properties;

import com.composum.pages.commons.model.Model;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dimension extends FloatTuple {

    private static final Logger LOG = LoggerFactory.getLogger(Dimension.class);

    public static final String PROP_PFX_WIDTH = ".width";
    public static final String PROP_PFX_HEIGHT = ".height";

    @Override
    protected String getKeyOne() {
        return PROP_PFX_WIDTH;
    }

    @Override
    protected String getKeyTwo() {
        return PROP_PFX_HEIGHT;
    }

    public Dimension(Model model, String name) {
        super(model, name);
    }

    public Dimension(Resource resource, String name) {
        super(resource, name);
    }

    public Dimension(String width, String height) {
        super(width, height);
    }

    public String getWidth() {
        return getOne();
    }

    public String getHeight() {
        return getTwo();
    }

    public float getWidthVal() {
        return getOneVal();
    }

    public float getHeightVal() {
        return getTwoVal();
    }
}
