package com.composum.pages.components.model.illustration;

import com.composum.pages.commons.model.properties.Dimension;
import com.composum.pages.commons.model.properties.Position;
import com.composum.pages.components.model.text.Text;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.commons.lang3.StringUtils;

public class Annotation extends Text {

    public static final Float DEFAULT_OFFSET = -15.0f;

    private transient String type;
    private transient String iconType;
    private transient String iconClasses;
    private transient String shapeText;
    private transient String shapeStyle;
    private transient String annotationClasses;
    private transient String annotationArrowStyle;
    private transient String annotationContentStyle;
    private transient String align;

    private transient Position shapePosition;
    private transient Dimension annotationSize;

    public String getShapeType() {
        if (type == null) {
            type = getProperty("shape/type", "circle");
        }
        return type;
    }

    public String getIconType() {
        if (iconType == null) {
            iconType = getProperty("shape/icon", "number");
        }
        return iconType;
    }

    public String getIconClasses() {
        if (iconClasses == null) {
            String icon = getIconType();
            if (StringUtils.isNotBlank(icon)
                    && !"none".equalsIgnoreCase(icon)) {
                if ("number".equalsIgnoreCase(icon)) {
                    iconClasses = "number";
                } else {
                    iconClasses = "fa fa-" + icon;
                }
            } else {
                iconClasses = "";
            }
        }
        return iconClasses;
    }

    public String getShapeText() {
        if (shapeText == null) {
            String icon = getIconType();
            if ("number".equalsIgnoreCase(icon)) {
                int index = ResourceUtil.getIndexOfSameType(resource);
                if (index >= 0) {
                    shapeText = Integer.toString(index + 1);
                } else {
                    shapeText = "";
                }
            } else {
                shapeText = "";
            }
        }
        return shapeText;
    }

    public Position getShapePosition() {
        if (shapePosition == null) {
            shapePosition = new Position(this, "shape/position");
        }
        return shapePosition;
    }

    public String getShapeStyle() {
        if (shapeStyle == null) {
            StringBuilder style = new StringBuilder();
            String value;
            Position shapePosition = getShapePosition();
            if (StringUtils.isNotBlank(value = shapePosition.getX())) {
                style.append("left:").append(value).append("%;");
            }
            if (StringUtils.isNotBlank(value = shapePosition.getY())) {
                style.append("top:").append(value).append("%;");
            }
            shapeStyle = style.toString();
        }
        return shapeStyle;
    }

    public String getAlign() {
        if (align == null) {
            align = getProperty("shape/align", "");
        }
        return align;
    }
}
