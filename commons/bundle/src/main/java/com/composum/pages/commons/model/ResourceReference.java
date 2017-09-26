package com.composum.pages.commons.model;

import com.composum.pages.commons.util.ResolverUtil;
import com.composum.pages.commons.util.ValueHashMap;
import com.composum.sling.core.util.PropertyUtil;
import com.composum.sling.core.util.ResourceUtil;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;

/**
 * the reference to a potentially non existing (static included; can exist but mustn't) content resource
 * this is a simple transferable (JSON) resource description without the overhead of a NonExistingResource and
 * with access to the resources properties (if resource exists), the configuration and the resource type properties
 */
public class ResourceReference {

    /** JSON attribute names */
    public static final String PATH = "path";
    public static final String TYPE = "type";

    /** the REFERENCE attributes */
    protected String path;
    protected String type;

    /** the resource determined by the path - can be a NonExistingResource */
    private transient Resource resource;
    /** the properties of the resource - an empty map if resource doesn't exist */
    private transient ValueMap resourceValues;

    public final ResourceResolver resolver;

    // references can be built from various sources...

    public ResourceReference(AbstractModel model) {
        this(model.getResource(), model.getType());
    }

    /** a resource and a probably overlayed type (type can be 'null') */
    public ResourceReference(Resource resource, String type) {
        this.resolver = resource.getResourceResolver();
        this.path = resource.getPath();
        this.type = StringUtils.isNotBlank(type) ? type : resource.getResourceType();
    }

    /** a reference simply created by the values */
    public ResourceReference(ResourceResolver resolver, String path, String type) {
        this.resolver = resolver;
        this.path = path;
        this.type = type;
    }

    /** a reference translated from a JSON object (transferred reference) */
    public ResourceReference(ResourceResolver resolver, JsonReader reader) throws IOException {
        this.resolver = resolver;
        fromJson(reader);
    }

    public String getPath() {
        return path;
    }

    public String getType() {
        return type;
    }

    public boolean isExisting() {
        return !ResourceUtil.isNonExistingResource(getResource());
    }

    /**
     * returns the property value using the cascade: resource - configuration - resource type
     * no 18n support for this property value retrieval
     * TODO: configuration support (theme / design)
     */
    public <T> T getProperty(String name, T defaultValue) {
        Class<T> type = PropertyUtil.getType(defaultValue);
        T value = getResourceValues().get(name, type);
        if (value == null) {
            // TODO: retrieve configuration
            // ...
            // fallback to the last instance: the resource type
            value = ResolverUtil.getTypeProperty(resolver, getType(), name, defaultValue);
        }
        return value != null ? value : defaultValue;
    }

    protected ValueMap getResourceValues() {
        if (resourceValues == null) {
            Resource resource = getResource();
            if (ResourceUtil.isNonExistingResource(resource)) {
                resourceValues = new ValueHashMap();
            } else {
                resourceValues = resource.adaptTo(ValueMap.class);
            }
        }
        return resourceValues;
    }

    public Resource getResource() {
        if (resource == null) {
            resource = resolver.resolve(getPath());
        }
        return resource;
    }

    public void fromJson(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.peek() != JsonToken.END_OBJECT) {
            String name = reader.nextName();
            switch (name) {
                case PATH:
                    path = reader.nextString();
                    break;
                case TYPE:
                    type = reader.nextString();
                    break;
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();
    }

    public void toJson(JsonWriter writer) throws IOException {
        writer.beginObject();
        writer.name(PATH).value(path);
        writer.name(TYPE).value(type);
        writer.endObject();
    }

    public String toString() {
        return path + ":" + type;
    }

    /**
     * a list of references (simple transferable as a JSON array)
     */
    public static class List extends ArrayList<ResourceReference> {

        public List() {
        }

        public List(ResourceReference... references) {
            Collections.addAll(this, references);
        }

        public List(ResourceResolver resolver, String jsonValue) throws IOException {
            if (StringUtils.isNotBlank(jsonValue)) {
                try (StringReader string = new StringReader(jsonValue);
                     JsonReader reader = new JsonReader(string)) {
                    fromJson(resolver, reader);
                }
            }
        }

        public List(ResourceResolver resolver, JsonReader reader) throws IOException {
            fromJson(resolver, reader);
        }

        public void fromJson(ResourceResolver resolver, JsonReader reader) throws IOException {
            reader.beginArray();
            while (reader.peek() != JsonToken.END_ARRAY) {
                add(new ResourceReference(resolver, reader));
            }
            reader.endArray();
        }

        public void toJson(JsonWriter writer) throws IOException {
            writer.beginArray();
            for (ResourceReference reference : this) {
                reference.toJson(writer);
            }
            writer.endArray();
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("[");
            for (ResourceReference reference : this) {
                if (builder.length() > 1) {
                    builder.append(",");
                }
                builder.append(reference);
            }
            builder.append("]");
            return builder.toString();
        }
    }
}
