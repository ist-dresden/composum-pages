package com.composum.pages.components.model.illustration;

import com.composum.pages.commons.model.properties.Dimension;
import com.composum.pages.commons.model.properties.Position;
import com.composum.sling.core.request.DomIdentifiers;
import com.composum.pages.components.model.text.Text;
import com.composum.sling.core.util.ResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

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
    private transient String next;

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

    public Dimension getAnnotationSize() {
        if (annotationSize == null) {
            annotationSize = new Dimension(this, "shape/size");
        }
        return annotationSize;
    }

    public String getAnnotationClasses() {
        if (annotationClasses == null) {
            StringBuilder classes = new StringBuilder();
            classes.append("align-").append(getProperty("shape/align", "left"));
            if (getProperty("visible", Boolean.FALSE)) {
                classes.append(" visible");
            }
            annotationClasses = classes.toString();
        }
        return annotationClasses;
    }

    protected void fillAnnotationStyle(StringBuilder style) {
        String value;
        Position shapePosition = getShapePosition();
        switch (getProperty("shape/align", "")) {
            case "right":
            default:
                if (StringUtils.isNotBlank(value = shapePosition.getX())) {
                    style.append("left:").append(value).append("%;");
                }
                break;
            case "left":
                if (StringUtils.isNotBlank(value = shapePosition.getX())) {
                    float x = shapePosition.getXval();
                    style.append("right:").append(100 - x).append("%;");
                }
                break;
            case "top":
                if (StringUtils.isNotBlank(value = shapePosition.getY())) {
                    float y = shapePosition.getYval();
                    style.append("bottom:").append(100 - y).append("%;");
                }
                break;
            case "bottom":
                if (StringUtils.isNotBlank(value = shapePosition.getY())) {
                    style.append("top:").append(value).append("%;");
                }
                break;
        }
    }

    public String getAnnotationArrowStyle() {
        if (annotationArrowStyle == null) {
            StringBuilder style = new StringBuilder();
            if (!isEditMode()) {
                fillAnnotationStyle(style);
                String value;
                Position shapePosition = getShapePosition();
                switch (getProperty("shape/align", "left")) {
                    case "left":
                    case "right":
                    default:
                        if (StringUtils.isNotBlank(value = shapePosition.getY())) {
                            float y = shapePosition.getYval();
                            style.append("top:").append(value).append("%;");
                        }
                        break;
                    case "top":
                    case "bottom":
                        if (StringUtils.isNotBlank(value = shapePosition.getX())) {
                            float x = shapePosition.getXval();
                            style.append("left:").append(value).append("%;");
                        }
                        break;
                }
            }
            annotationArrowStyle = style.toString();
        }
        return annotationArrowStyle;
    }

    public String getAnnotationContentStyle() {
        if (annotationContentStyle == null) {
            StringBuilder style = new StringBuilder();
            if (!isEditMode()) {
                fillAnnotationStyle(style);
                String value;
                float offset = getProperty("shape/offset", DEFAULT_OFFSET);
                Position shapePosition = getShapePosition();
                switch (getProperty("shape/align", "left")) {
                    case "left":
                    case "right":
                    default:
                        if (StringUtils.isNotBlank(value = shapePosition.getY())) {
                            float y = shapePosition.getYval();
                            style.append("top:").append(y + offset).append("%;");
                        }
                        break;
                    case "top":
                    case "bottom":
                        if (StringUtils.isNotBlank(value = shapePosition.getX())) {
                            float x = shapePosition.getXval();
                            style.append("left:").append(x + offset).append("%;");
                        }
                        break;
                }
                if (StringUtils.isNotBlank(value = getProperty("shape/size.width", ""))) {
                    style.append("width:").append(value).append("%;");
                }
                if (StringUtils.isNotBlank(value = getProperty("shape/size.height", ""))) {
                    style.append("height:").append(value).append("%;");
                }
            }
            annotationContentStyle = style.toString();
        }
        return annotationContentStyle;
    }

    public boolean getHasNext() {
        return StringUtils.isNotBlank(getNext());
    }

    public String getNext() {
        if (next == null) {
            next = getProperty("next", "");
        }
        return next;
    }

    public String getNextId() {
        String key = getNext();
        if ("next".equalsIgnoreCase(key)) {
            Resource nextRes = ResourceUtil.getNextOfSameType(resource, true);
            if (nextRes != null) {
                key = nextRes.getName();
            }
        }
        return DomIdentifiers.getInstance(context).getElementId(
                resource.getParent().getPath() + "/" + key, getType());
    }
}
