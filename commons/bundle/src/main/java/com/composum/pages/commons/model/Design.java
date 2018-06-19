package com.composum.pages.commons.model;

import org.apache.sling.api.resource.Resource;

public class Design {

    public static final Design EMPTY = new Design(null, 0);

    protected final Resource resource;
    protected final int weight;

    protected Design(Resource resource, int weight) {
        this.resource = resource;
        this.weight = weight;
    }
}
