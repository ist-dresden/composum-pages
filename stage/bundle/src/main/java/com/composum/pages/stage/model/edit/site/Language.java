package com.composum.pages.stage.model.edit.site;

import com.composum.pages.commons.model.Element;

import static com.composum.pages.stage.model.edit.site.Languages.LANGUAGES_TYPE;

public class Language extends Element {

    public boolean isNew() {
        return resource.isResourceType(LANGUAGES_TYPE);
    }

    public String getName() {
        return isNew() ? "" : super.getName();
    }

    public String getKey() {
        return getProperty("key", null, "");
    }

    public String getLabel() {
        return getProperty("label", null, "");
    }

    public String getDirection() {
        return getProperty("direction", null, "");
    }
}
