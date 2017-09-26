package com.composum.pages.commons.taglib;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.VariableInfo;
import java.util.List;

public class ElementTagTEI extends ModelTagTEI {

    @Override
    protected void collectVariables(TagData data, List<VariableInfo> variables) {
        super.collectVariables(data, variables);
        String var = getVar(data);
        variables.add(new VariableInfo(var + "Id", "java.lang.String", true, VariableInfo.NESTED));
    }
}
