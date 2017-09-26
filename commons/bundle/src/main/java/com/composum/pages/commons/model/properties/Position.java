package com.composum.pages.commons.model.properties;

import com.composum.pages.commons.model.Model;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Position extends FloatTuple {

    private static final Logger LOG = LoggerFactory.getLogger(Position.class);

    public static final String PROP_PFX_WIDTH = ".x";
    public static final String PROP_PFX_HEIGHT = ".y";

    @Override
    protected String getKeyOne() {
        return PROP_PFX_WIDTH;
    }

    @Override
    protected String getKeyTwo() {
        return PROP_PFX_HEIGHT;
    }

    public Position(Model model, String name) {
        super(model, name);
    }

    public Position(Resource resource, String name) {
        super(resource, name);
    }

    public Position(String x, String y) {
        super(x, y);
    }

    public String getX() {
        return getOne();
    }

    public String getY() {
        return getTwo();
    }

    public float getXval() {
        return getOneVal();
    }

    public float getYval() {
        return getTwoVal();
    }
}
