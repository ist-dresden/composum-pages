package com.composum.pages.commons.widget;

import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.taglib.PropertyEditHandle;

import javax.annotation.Nonnull;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.composum.pages.commons.PagesConstants.DEF_DATETIME_FMT;
import static com.composum.pages.commons.PagesConstants.DEF_DATE_FMT;
import static com.composum.pages.commons.PagesConstants.DEF_TIME_FMT;
import static com.composum.pages.commons.PagesConstants.SP_DATETIME_FMT;
import static com.composum.pages.commons.PagesConstants.SP_DATE_FMT;
import static com.composum.pages.commons.PagesConstants.SP_TIME_FMT;

public class DateTimeField extends PropertyEditHandle<Calendar> implements WidgetModel {

    public enum FieldType {date, time, datetime}

    public static final String TYPE_ATTR = "type";
    public static final String DEFAULT_TYPE = FieldType.datetime.name();

    public static final String ICON_ATTR = "icon";
    public static final String ICON_TIME = "clock-o";
    public static final String ICON_DATE = "calendar";

    private transient FieldType fieldType;
    private transient String icon;
    private transient String format;

    public DateTimeField() {
        super(Calendar.class);
    }

    @Nonnull
    public String getDateValue() {
        Calendar value = getValue();
        return value != null ? new SimpleDateFormat(getFormat()).format(value.getTime()) : "";
    }

    @Override
    public String getWidgetAttributeKey(String attributeKey) {
        return attributeKey;
    }

    @Nonnull
    public FieldType getFieldType() {
        if (fieldType == null) {
            try {
                fieldType = FieldType.valueOf(widget.getWidgetType().replace("field", ""));
            } catch (IllegalArgumentException ex) {
                fieldType = FieldType.datetime;
            }
        }
        return fieldType;
    }

    @Nonnull
    public String getIcon() {
        if (icon == null) {
            icon = widget.consumeDynamicAttribute(ICON_ATTR, getFieldType() == FieldType.time ? ICON_TIME : ICON_DATE);
        }
        return icon;
    }

    @Nonnull
    public String getFormat() {
        if (format == null) {
            switch (getFieldType()) {
                case date:
                    format = getDateFormat();
                    break;
                case time:
                    format = getTimeFormat();
                    break;
                case datetime:
                default:
                    format = getDateTimeFormat();
                    break;
            }
        }
        return format;
    }

    @Nonnull
    public String getMomentFormat() {
        return toMomentFormat(getFormat());
    }

    @Nonnull
    public String getDateFormat() {
        Page page = getCurrentPage();
        return page != null ? page.getSettingsProperty(SP_DATE_FMT, DEF_DATE_FMT) : DEF_DATE_FMT;
    }

    @Nonnull
    public String getTimeFormat() {
        Page page = getCurrentPage();
        return page != null ? page.getSettingsProperty(SP_TIME_FMT, DEF_TIME_FMT) : DEF_TIME_FMT;
    }

    @Nonnull
    public String getDateTimeFormat() {
        Page page = getCurrentPage();
        return page != null ? page.getSettingsProperty(SP_DATETIME_FMT, DEF_DATETIME_FMT) : DEF_DATETIME_FMT;
    }

    @Nonnull
    public String toMomentFormat(String format) {
        return format.replaceAll("yy", "YY").replaceAll("dd", "DD");
    }
}
