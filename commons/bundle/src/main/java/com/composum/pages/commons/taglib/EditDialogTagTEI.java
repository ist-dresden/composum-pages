package com.composum.pages.commons.taglib;

import com.composum.sling.cpnl.AbstractTagTEI;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.VariableInfo;
import java.util.List;

public class EditDialogTagTEI extends AbstractTagTEI {

    protected void collectVariables(TagData data, List<VariableInfo> variables) {
        variables.add(new VariableInfo(EditDialogTag.DIALOG_VAR, EditDialogTag.class.getName(), true, VariableInfo.NESTED));
        variables.add(new VariableInfo(EditDialogTag.DIALOG_CSS_VAR, "java.lang.String", true, VariableInfo.NESTED));
    }
}
