package com.composum.pages.commons.taglib;

import static com.composum.pages.commons.taglib.AbstractPageTag.STAGE_COMPONENT_BASE;

/**
 * the action tag an action of a tree toolbar
 */
public class TreeActionTag extends EditActionTag {

    public static final String ACTION_TAG_PATH = STAGE_COMPONENT_BASE + "edit/actions/tree";

    protected String getSnippetResourceType() {
        return ACTION_TAG_PATH;
    }
}
