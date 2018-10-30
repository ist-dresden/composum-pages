/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.model.properties;

import com.composum.pages.commons.filter.DropZoneFilter;
import com.composum.sling.core.BeanContext;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;

/**
 * the handler class for a drop zone in the edit view; such a drop zone is a describing reference for a
 * property to change on dropping an appropriate object (resource) into the drop zone
 */
public class DropZone {

    public static final String ID = "id";               // the DOM id for the drop zone itself
    public static final String PATH = "path";           // the element reference data
    public static final String TYPE = "type";           // type hints if target element is synthetic
    public static final String PRIM = "prim";
    public static final String PROPERTY = "property";   // the property name (path, probably an i18n path)
    public static final String FILTER = "filter";       // the filter rule to restrict the drop objects
    public static final String EVENT = "event";         // the optional (special) event to trigger after change

    protected String id;
    protected String path;
    protected String type;
    protected String prim;
    protected String property;
    protected DropZoneFilter filter;
    protected String event;

    protected final BeanContext context;

    /**
     * create a DropZone object using a JSON data object send from the client layer (from the editing view)
     */
    public DropZone(@Nonnull BeanContext context, @Nonnull JsonReader reader) throws IOException {
        this.context = context;
        fromJson(reader);
    }

    /**
     * @return 'true' if the resource is accepted by the filter of the drop zone
     */
    public boolean matches(@Nonnull Resource resource) {
        return filter.accept(resource);
    }

    public void fromJson(@Nonnull JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.peek() != JsonToken.END_OBJECT) {
            String name = reader.nextName();
            switch (name) {
                case ID:
                    id = reader.nextString();
                    break;
                case PATH:
                    path = reader.nextString();
                    break;
                case TYPE:
                    type = reader.nextString();
                    break;
                case PRIM:
                    prim = reader.nextString();
                    break;
                case PROPERTY:
                    property = reader.nextString();
                    break;
                case FILTER:
                    filter = new DropZoneFilter(context, reader.nextString());
                    break;
                case EVENT:
                    event = reader.nextString();
                    break;
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();
    }

    public void toJson(@Nonnull JsonWriter writer) throws IOException {
        writer.beginObject();
        writer.name(ID).value(id);
        writer.name(PATH).value(path);
        if (StringUtils.isNotBlank(type)) {
            writer.name(TYPE).value(type);
        }
        if (StringUtils.isNotBlank(prim)) {
            writer.name(PRIM).value(prim);
        }
        writer.name(PROPERTY).value(property);
        writer.name(FILTER).value(filter.getRule());
        if (StringUtils.isNotBlank(event)) {
            writer.name(EVENT).value(event);
        }
        writer.endObject();
    }

    /**
     * a list of drop zone; used for transferring the list of drop zones of a page in edit mode
     */
    public static class List extends ArrayList<DropZone> {

        protected List() {
        }

        public List(@Nonnull BeanContext context, @Nonnull JsonReader reader) throws IOException {
            fromJson(context, reader);
        }

        /**
         * @return a sublist of all drop zones from this list which are matching to the resource
         */
        public List getMatchingList(@Nonnull Resource resource) {
            List matching = new List();
            for (DropZone dropZone : this) {
                if (dropZone.matches(resource)) {
                    matching.add(dropZone);
                }
            }
            return matching;
        }

        public void fromJson(@Nonnull BeanContext context, @Nonnull JsonReader reader) throws IOException {
            reader.beginArray();
            while (reader.peek() != JsonToken.END_ARRAY) {
                add(new DropZone(context, reader));
            }
            reader.endArray();
        }

        public void toJson(@Nonnull JsonWriter writer) throws IOException {
            writer.beginArray();
            for (DropZone dropZone : this) {
                dropZone.toJson(writer);
            }
            writer.endArray();
        }
    }
}
