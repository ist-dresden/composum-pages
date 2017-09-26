package com.composum.pages.components.model.page;

import com.composum.pages.components.model.navigation.Menu;

public class SinglePageMenu extends Menu {

    private transient SinglePage singlePage;

    public SinglePage getSinglePage() {
        if (singlePage == null) {
            singlePage = new SinglePage(context);
        }
        return singlePage;
    }
}
