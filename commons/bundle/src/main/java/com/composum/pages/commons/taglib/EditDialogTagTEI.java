package com.composum.pages.commons.taglib;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.VariableInfo;
import java.util.List;

public class EditDialogTagTEI extends ModelTagTEI {

    protected void collectVariables(TagData data, List<VariableInfo> variables) {
        super.collectVariables(data, variables);
        variables.add(new VariableInfo(EditDialogTag.DIALOG_VAR, EditDialogTag.class.getName(), true, VariableInfo.NESTED));
        variables.add(new VariableInfo(EditDialogTag.DIALOG_CSSBASE_VAR, "java.lang.String", true, VariableInfo.NESTED));
    }
}
