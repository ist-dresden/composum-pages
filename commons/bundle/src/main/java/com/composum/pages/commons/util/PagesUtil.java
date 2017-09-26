package com.composum.pages.commons.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.composum.pages.commons.PagesConstants.TIMESTAMP_FORMAT;

public class PagesUtil {

    public static String getTimestampString(Calendar timestamp) {
        if (timestamp != null) {
            return new SimpleDateFormat(TIMESTAMP_FORMAT).format(timestamp.getTime());
        } else {
            return "";
        }
    }
}
