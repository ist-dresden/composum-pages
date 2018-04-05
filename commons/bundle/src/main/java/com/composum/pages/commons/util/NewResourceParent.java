package com.composum.pages.commons.util;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceWrapper;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;

import javax.annotation.Nonnull;
import java.util.Iterator;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.EMPTY_MAP;

/**
 * a resource wrapper wrapping the parent of a resource to create to simulate an empty resource
 * (avoid using properties of the parent in a create dialog)
 */
public class NewResourceParent extends ResourceWrapper {

    ValueMap values = new ValueMapDecorator(EMPTY_MAP);

    public NewResourceParent(@Nonnull Resource resource) {
        super(resource);
    }

    @Override
    public Resource getChild(String relPath) {
        return null;
    }

    @Override
    @Nonnull
    public Iterator<Resource> listChildren() {
        return EMPTY_LIST.iterator();
    }

    @Override
    @Nonnull
    public Iterable<Resource> getChildren() {
        return EMPTY_LIST;
    }

    @Override
    public boolean hasChildren() {
        return false;
    }

    @Override
    public <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
        return ValueMap.class.isAssignableFrom(type) ? (AdapterType) values : getResource().adaptTo(type);
    }

    @Override
    @Nonnull
    public ValueMap getValueMap() {
        return values;
    }
}
