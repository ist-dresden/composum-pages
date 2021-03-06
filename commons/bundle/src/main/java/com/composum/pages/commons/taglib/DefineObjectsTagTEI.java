package com.composum.pages.commons.taglib;

import com.composum.pages.commons.model.Page;
import com.composum.pages.commons.model.Site;
import org.apache.sling.api.resource.Resource;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.VariableInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.composum.pages.commons.PagesConstants.RA_CONTEXT_PATH;
import static com.composum.pages.commons.PagesConstants.RA_CURRENT_PAGE;
import static com.composum.pages.commons.PagesConstants.RA_CURRENT_SITE;
import static com.composum.pages.commons.PagesConstants.RA_RESOURCE_REF;
import static com.composum.pages.commons.taglib.DefineObjectsTag.PAGES_ACCESS_AUTHOR;
import static com.composum.pages.commons.taglib.DefineObjectsTag.PAGES_MODE_DEVELOP;
import static com.composum.pages.commons.taglib.DefineObjectsTag.PAGES_MODE_EDIT;
import static com.composum.pages.commons.taglib.DefineObjectsTag.PAGES_MODE_NONE;
import static com.composum.pages.commons.taglib.DefineObjectsTag.PAGES_MODE_PREVIEW;
import static com.composum.pages.commons.taglib.DefineObjectsTag.PAGES_ACCESS_PREVIEW;
import static com.composum.pages.commons.taglib.DefineObjectsTag.PAGES_ACCESS_PUBLIC;

public class DefineObjectsTagTEI extends org.apache.sling.scripting.jsp.taglib.DefineObjectsTEI {

    public VariableInfo[] getVariableInfo(TagData data) {
        List<VariableInfo> variables = new ArrayList<>(Arrays.asList(super.getVariableInfo(data)));
        collectVariables(data, variables);
        return variables.toArray(new VariableInfo[0]);
    }

    protected void collectVariables(TagData data, List<VariableInfo> variables) {
        variables.add(new VariableInfo(RA_CONTEXT_PATH, String.class.getName(), true, VariableInfo.AT_END));
        variables.add(new VariableInfo(RA_RESOURCE_REF, Resource.class.getName(), true, VariableInfo.AT_END));
        variables.add(new VariableInfo(RA_CURRENT_PAGE, Page.class.getName(), true, VariableInfo.AT_END));
        variables.add(new VariableInfo(RA_CURRENT_SITE, Site.class.getName(), true, VariableInfo.AT_END));
        variables.add(new VariableInfo(PAGES_ACCESS_AUTHOR, Boolean.class.getName(), true, VariableInfo.AT_END));
        variables.add(new VariableInfo(PAGES_ACCESS_PREVIEW, Boolean.class.getName(), true, VariableInfo.AT_END));
        variables.add(new VariableInfo(PAGES_ACCESS_PUBLIC, Boolean.class.getName(), true, VariableInfo.AT_END));
        variables.add(new VariableInfo(PAGES_MODE_NONE, Boolean.class.getName(), true, VariableInfo.AT_END));
        variables.add(new VariableInfo(PAGES_MODE_PREVIEW, Boolean.class.getName(), true, VariableInfo.AT_END));
        variables.add(new VariableInfo(PAGES_MODE_EDIT, Boolean.class.getName(), true, VariableInfo.AT_END));
        variables.add(new VariableInfo(PAGES_MODE_DEVELOP, Boolean.class.getName(), true, VariableInfo.AT_END));
    }
}
