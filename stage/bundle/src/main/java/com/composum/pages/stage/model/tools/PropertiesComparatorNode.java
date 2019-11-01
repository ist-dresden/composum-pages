package com.composum.pages.stage.model.tools;

import com.composum.pages.commons.model.Component;
import com.composum.pages.commons.model.GenericModel;
import com.composum.sling.core.AbstractServletBean;
import com.google.gson.stream.JsonWriter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.TreeMap;

/**
 * a model implemetation for a comparator node which can be used in templates
 */
public class PropertiesComparatorNode extends AbstractServletBean {

    public class Property {

        protected final String name;
        protected final Object left;
        protected final Component.Property leftProp;
        protected final Object right;
        protected final Component.Property rightProp;

        public Property(@Nonnull final String name,
                        @Nullable final Object left, @Nullable final Object right) {
            this.name = name;
            this.left = left;
            this.right = right;
            this.leftProp = getProperty(PropertiesComparatorNode.this.getLeft());
            this.rightProp = getProperty(PropertiesComparatorNode.this.getRight());
        }

        @Nullable
        public Component.Property getProperty(@Nullable final GenericModel model) {
            Component component;
            return model != null && (component = model.getComponent()) != null
                    ? component.getComponentProperties().get(name) : null;
        }

        public String getName() {
            return name;
        }

        public String getLeft() {
            return toString(leftProp, left);
        }

        public String getRight() {
            return toString(rightProp, right);
        }

        public boolean isRichText() {
            return (leftProp != null && "rich".equals(leftProp.getTextType())) ||
                    (rightProp != null && "rich".equals(rightProp.getTextType()));
        }

        @Nonnull
        public String toString(@Nullable final Component.Property property, @Nullable final Object value) {
            if (value == null) {
                return "";
            } else {
                switch (property != null ? property.getPropertyType() : "String") {
                    case "Date":
                        return formatDate(value);
                    default:
                        return value.toString();
                }
            }
        }

        protected String formatDate(Object value) {
            Date date = value instanceof Calendar ? ((Calendar) value).getTime()
                    : value instanceof Date ? (Date) value : null;
            return date != null
                    ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z").format(date)
                    : value.toString();
        }

        public void toJson(@Nonnull final JsonWriter writer) throws IOException {
            writer.beginObject();
            writer.name("name").value(name);
            writer.name("left");
            jsonValue(writer, leftProp, left);
            writer.name("right");
            jsonValue(writer, rightProp, right);
            writer.endObject();
        }

        public void jsonValue(@Nonnull final JsonWriter writer, @Nullable final Component.Property property,
                              @Nullable final Object value) throws IOException {
            if (value == null) {
                writer.nullValue();
            } else {
                switch (property != null ? property.getPropertyType() : "String") {
                    case "Boolean":
                        writer.value(value instanceof Boolean ? (Boolean) value : Boolean.parseBoolean(value.toString()));
                        break;
                    case "Long":
                        writer.value(value instanceof Long ? (Long) value : Long.parseLong(value.toString()));
                        break;
                    case "Date":
                        writer.value(formatDate(value));
                        break;
                    default:
                        writer.value(value.toString());
                        break;
                }
            }
        }
    }

    protected final GenericModel left;
    protected final GenericModel right;

    protected final TreeMap<String, Property> properties = new TreeMap<>();
    protected final ArrayList<PropertiesComparatorNode> children = new ArrayList<>();

    public PropertiesComparatorNode() {
        this(null, null);
    }

    public PropertiesComparatorNode(@Nullable final GenericModel left, @Nullable final GenericModel right) {
        this.left = left;
        this.right = right;
    }

    @Nullable
    public GenericModel getLeft() {
        return left;
    }

    @Nullable
    public GenericModel getRight() {
        return right;
    }

    @Nullable
    public Property getProperty(String name) {
        return properties.get(name);
    }

    @Nonnull
    public Collection<Property> getProperties() {
        return properties.values();
    }

    public void setProperty(@Nonnull final String name,
                            @Nullable final Object left, @Nullable final Object right) {
        if (left != null || right != null) {
            Property property = new Property(name, left, right);
            properties.putIfAbsent(property.name, property);
        }
    }

    @Nonnull
    public Collection<PropertiesComparatorNode> getNodes() {
        return children;
    }

    public void addChild(@Nonnull final PropertiesComparatorNode child) {
        children.add(child);
    }

    public void toJson(@Nonnull final JsonWriter writer) throws IOException {
        writer.beginObject();
        writer.name("left");
        jsonModel(writer, getLeft());
        writer.name("right");
        jsonModel(writer, getRight());
        writer.name("properties").beginArray();
        for (Property property : properties.values()) {
            property.toJson(writer);
        }
        writer.endArray();
        writer.name("children").beginArray();
        for (PropertiesComparatorNode node : children) {
            node.toJson(writer);
        }
        writer.endArray();
        writer.endObject();
    }

    public void jsonModel(@Nonnull final JsonWriter writer, @Nullable final GenericModel model) throws IOException {
        if (model == null) {
            writer.nullValue();
        } else {
            writer.beginObject();
            writer.name("name").value(model.getName());
            writer.name("path").value(model.getPath());
            writer.name("type").value(model.getType());
            writer.name("short").value(model.getTypeHint());
            writer.endObject();
        }
    }
}
