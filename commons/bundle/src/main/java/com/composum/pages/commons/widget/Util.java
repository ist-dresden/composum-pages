package com.composum.pages.commons.widget;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

public class Util {

    public static BigDecimal getDecimalOption(String[] options, int index, BigDecimal defaultValue) {
        if (options.length <= index) {
            return defaultValue;
        }
        String string = options[index].trim();
        if (StringUtils.isBlank(string)) {
            return defaultValue;
        }
        try {
            return new BigDecimal(string);
        } catch (NumberFormatException nfex) {
            return defaultValue;
        }
    }

    public static Integer getIntegerOption(String[] options, int index, Integer defaultValue) {
        if (options.length <= index) {
            return defaultValue;
        }
        String string = options[index].trim();
        if (StringUtils.isBlank(string)) {
            return defaultValue;
        }
        try {
            return new Integer(string);
        } catch (NumberFormatException nfex) {
            return defaultValue;
        }
    }
}
