package com.composum.pages.stage.model.edit.site;

import com.composum.pages.commons.model.Container;
import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.Site;
import org.apache.sling.api.resource.Resource;

import java.util.ArrayList;
import java.util.List;

public class Languages extends Container {

    public static final String LANGUAGES_TYPE = "composum/pages/stage/edit/site/languages";

    private transient List<Resource> languageList;

    public List<Resource> getLanguageList() {
        if (languageList == null) {
            languageList = new ArrayList<>();
            final Page currentPage = getCurrentPage();
            final Site site = currentPage.getSite();
            final Resource siteResource = site.getResource();
            final Resource languagesResource = siteResource.getChild("jcr:content/languages");
            for (Resource language : languagesResource.getChildren()) {
                languageList.add(language);
            }
        }
        return languageList;
    }
}
