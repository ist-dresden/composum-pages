package com.composum.pages.commons.taglib;

import com.composum.sling.cpnl.AbstractTagTEI;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.VariableInfo;
import java.util.List;

public class TreeMenuTagTEI extends AbstractTagTEI {

    protected void collectVariables(TagData data, List<VariableInfo> variables) {
        variables.add(new VariableInfo(TreeMenuTag.MENU_VAR, TreeMenuTag.class.getName(), true, VariableInfo.NESTED));
    }
}
