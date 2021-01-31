package com.composum.pages.commons.util;

import org.apache.jackrabbit.util.ISO8601;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * a simple Date (Calendar) parser compatible to the default parser configuration of the SlingPostServlet
 */
public class DateConverter {

    public interface DateParser {
        @Nullable
        Calendar parse(@Nonnull String value);
    }

    public static class ISO8601parser implements DateParser {

        @Nullable
        @Override
        public Calendar parse(@Nonnull String value) {
            return ISO8601.parse(value);
        }
    }

    public static class DateFormatParser implements DateParser {

        protected final SimpleDateFormat format;

        public DateFormatParser(@Nonnull final String format) {
            this.format = new SimpleDateFormat(format);
        }

        @Nullable
        @Override
        public Calendar parse(@Nonnull String value) {
            try {
                Date date;
                synchronized (format) {
                    date = format.parse(value);
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                return calendar;
            } catch (ParseException ignore) {
            }
            return null;
        }
    }

    public static final DateParser[] DEFAULT_POST_SERVLET_DATE_FORMATS = new DateParser[]{
            new DateFormatParser("EEE MMM dd yyyy HH:mm:ss 'GMT'Z"),
            new ISO8601parser(),
            new DateFormatParser("yyyy-MM-dd'T'HH:mm:ss.SSSZ"),
            new DateFormatParser("yyyy-MM-dd'T'HH:mm:ss"),
            new DateFormatParser("yyyy-MM-dd'T'HH:mm"),
            new DateFormatParser("yyyy-MM-dd"),
            new DateFormatParser("dd.MM.yyyy HH:mm:ss"),
            new DateFormatParser("dd.MM.yyyy HH:mm"),
            new DateFormatParser("dd.MM.yyyy")
    };

    public static Calendar convert(@Nonnull final Object value) {
        if (value instanceof Calendar) {
            return (Calendar) value;
        } else if (value instanceof Date) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime((Date) value);
            return calendar;
        } else if (value instanceof String) {
            Calendar calendar;
            for (DateParser parser : DEFAULT_POST_SERVLET_DATE_FORMATS) {
                if ((calendar = parser.parse((String) value)) != null) {
                    return calendar;
                }
            }
        }
        return null;
    }
}
