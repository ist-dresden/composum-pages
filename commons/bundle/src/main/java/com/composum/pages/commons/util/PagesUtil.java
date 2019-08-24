/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import static com.composum.pages.commons.PagesConstants.TIMESTAMP_FORMAT;

public class PagesUtil {

    /**
     * @param resource the resource for referencing; the parent is used if this resource is a content resource
     * @return a JSON object which is representing a reference to the resource
     */
    public static JsonObject getReference(@Nonnull Resource resource, @Nullable final String typeHint) {
        if (JcrConstants.JCR_CONTENT.equals(resource.getName())) {
            resource = Objects.requireNonNull(resource.getParent());
        }
        String type = StringUtils.isNotBlank(typeHint) ? typeHint : ResourceTypeUtil.getResourceType(resource);
        JsonObject data = new JsonObject();
        data.addProperty("name", resource.getName());
        data.addProperty("path", resource.getPath());
        data.addProperty("type", type);
        data.addProperty("prim", ResourceTypeUtil.getPrimaryType(resource));
        return data;
    }

    /**
     * @param resource the resource for referencing; the parent is used if this resource is a content resource
     * @return the Base64 encoded string of a JSON object which is representing a reference to the resource
     */
    public static String getEncodedReference(@Nonnull  Resource resource, @Nullable final String typeHint) {
        return Base64.encodeBase64String(getReference(resource, typeHint).toString().getBytes(StandardCharsets.UTF_8));
    }

    public static String getTimestampString(Calendar timestamp) {
        if (timestamp != null) {
            return new SimpleDateFormat(TIMESTAMP_FORMAT).format(timestamp.getTime());
        } else {
            return "";
        }
    }

    // JSON...

    /**
     * write some object as JSON
     *
     * @param writer the target JSON writer
     * @param object the object tor write
     */
    public static void write(@Nonnull final JsonWriter writer, final Object object)
            throws IOException {
        if (object == null) {
            writer.nullValue();
            return;
        }
        if (object instanceof String) {
            writer.value((String) object);
        } else if (object instanceof JsonElement) {
            JsonElement element = (JsonElement) object;
            if (element.isJsonNull()) {
                writer.nullValue();
            } else if (element.isJsonArray()) {
                writer.beginArray();
                for (JsonElement jsonElement : (JsonArray) element) {
                    write(writer, jsonElement);
                }
                writer.endArray();
            } else if (element.isJsonObject()) {
                writer.beginObject();
                for (Map.Entry<String, JsonElement> entry : ((JsonObject) element).entrySet()) {
                    writer.name(entry.getKey());
                    write(writer, entry.getValue());
                }
                writer.endObject();
            } else if (element.isJsonPrimitive()) {
                JsonPrimitive primitive = (JsonPrimitive) element;
                if (primitive.isBoolean()) {
                    writer.value(primitive.getAsBoolean());
                } else if (primitive.isNumber()) {
                    writer.value(primitive.getAsNumber());
                } else if (primitive.isString()) {
                    writer.value(primitive.getAsString());
                } else {
                    writer.value(primitive.toString());
                }
            } else {
                writer.value(element.toString());
            }
        } else if (object instanceof Object[]) {
            writer.beginArray();
            for (Object element : (Object[]) object) {
                write(writer, element);
            }
            writer.endArray();
        } else if (object instanceof Collection) {
            writer.beginArray();
            for (Object element : (Collection) object) {
                write(writer, element);
            }
            writer.endArray();
        } else if (object instanceof Map) {
            writer.beginObject();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) object).entrySet()) {
                writer.name(entry.getKey().toString());
                write(writer, entry.getValue());
            }
            writer.endObject();
        } else if (object instanceof Number) {
            writer.value((Number) object);
        } else if (object instanceof Boolean) {
            writer.value((Boolean) object);
        } else {
            writer.value(object.toString());
        }
    }
}
