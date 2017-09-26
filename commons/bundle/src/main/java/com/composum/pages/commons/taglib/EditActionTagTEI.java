package com.composum.pages.commons.taglib;

import com.composum.sling.cpnl.ComponentTagTEI;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.VariableInfo;
import java.util.List;

public class EditActionTagTEI extends ComponentTagTEI {

    protected void collectVariables(TagData data, List<VariableInfo> variables) {
        variables.add(new VariableInfo(EditActionTag.ACTION_VAR, EditActionTag.class.getName(), true, VariableInfo.NESTED));
    }
}
