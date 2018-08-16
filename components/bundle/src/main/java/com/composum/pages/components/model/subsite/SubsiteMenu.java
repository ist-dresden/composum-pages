package com.composum.pages.components.model.subsite;

import com.composum.pages.components.model.navigation.Menu;

public class SubsiteMenu extends Menu {

    private transient SubsiteRoot rootPage;

    public SubsiteRoot getRootPage() {
        if (rootPage == null) {
            rootPage = new SubsiteRoot(context);
        }
        return rootPage;
    }

    public boolean isRoot() {
        return getCurrentPage().getPath().equals(getRootPage().getPath());
    }
}
