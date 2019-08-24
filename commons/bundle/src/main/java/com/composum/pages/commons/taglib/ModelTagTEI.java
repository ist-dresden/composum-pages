package com.composum.pages.commons.taglib;

import com.composum.sling.cpnl.ComponentTagTEI;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.VariableInfo;
import java.util.List;

public class ModelTagTEI extends ComponentTagTEI {

    @Override
    protected void collectVariables(TagData data, List<VariableInfo> variables) {
        super.collectVariables(data, variables);
        String var = getVar(data);
        if (var != null) {
            variables.add(new VariableInfo(var + "CSS", "java.lang.String", true, VariableInfo.NESTED));
            variables.add(new VariableInfo(var + "CssBase", "java.lang.String", true, VariableInfo.NESTED));
        }
    }
}
