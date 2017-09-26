package com.composum.pages.commons.taglib;

import com.composum.sling.cpnl.AbstractTagTEI;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.VariableInfo;
import java.util.List;

public class EditDialogGroupTagTEI extends AbstractTagTEI {

    protected void collectVariables(TagData data, List<VariableInfo> variables) {
        variables.add(new VariableInfo(EditDialogTabTag.DIALOG_TAB_VAR, EditDialogTabTag.class.getName(), true, VariableInfo.NESTED));
    }
}
