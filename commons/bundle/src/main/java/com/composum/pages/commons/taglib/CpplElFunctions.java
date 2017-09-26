package com.composum.pages.commons.taglib;

import com.composum.pages.commons.model.Model;

/**
 * the set of taglib JSP EL functions
 */
public class CpplElFunctions {

    public static String string(Model model, String key) {
        return model.getProperty(key, "");
    }

    public static String string(Model model, String key, String defaultValue) {
        return model.getProperty(key, model.getProperty(defaultValue, defaultValue));
    }
}
