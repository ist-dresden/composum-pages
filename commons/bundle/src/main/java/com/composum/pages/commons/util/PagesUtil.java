/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.util;

import com.google.gson.JsonObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.composum.pages.commons.PagesConstants.TIMESTAMP_FORMAT;

public class PagesUtil {

    /**
     * @param resource the resource for referencing; the parent is used if this resource is a content resource
     * @return a JSON object which is representing a reference to the resource
     */
    public static JsonObject getReference(Resource resource) {
        if (JcrConstants.JCR_CONTENT.equals(resource.getName())) {
            resource = resource.getParent();
        }
        JsonObject data = new JsonObject();
        if (resource != null) {
            data.addProperty("name", resource.getName());
            data.addProperty("path", resource.getPath());
            data.addProperty("type", ResourceTypeUtil.getResourceType(resource));
            data.addProperty("prim", ResourceTypeUtil.getPrimaryType(resource));
        }
        return data;
    }

    /**
     * @param resource the resource for referencing; the parent is used if this resource is a content resource
     * @return the Base64 encoded string of a JSON object which is representing a reference to the resource
     */
    public static String getEncodedReference(Resource resource) {
        return Base64.encodeBase64String(getReference(resource).toString().getBytes(StandardCharsets.UTF_8));
    }

    public static String getTimestampString(Calendar timestamp) {
        if (timestamp != null) {
            return new SimpleDateFormat(TIMESTAMP_FORMAT).format(timestamp.getTime());
        } else {
            return "";
        }
    }
}
