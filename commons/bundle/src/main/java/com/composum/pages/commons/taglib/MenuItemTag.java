package com.composum.pages.commons.taglib;

import static com.composum.pages.commons.taglib.AbstractPageTag.STAGE_COMPONENT_BASE;

/**
 * the action tag of a dropdown menu item action of a toolbar
 */
public class MenuItemTag extends EditActionTag {

    public static final String ACTION_TAG_PATH = STAGE_COMPONENT_BASE + "edit/actions/menu/item";

    protected String getSnippetResourceType() {
        return ACTION_TAG_PATH;
    }
}
