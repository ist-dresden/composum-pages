package com.composum.pages.commons.taglib;

import com.composum.sling.cpnl.AbstractTagTEI;
import org.apache.commons.lang.StringUtils;

import javax.servlet.jsp.jstl.core.LoopTagStatus;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.VariableInfo;
import java.util.List;

public class EditMultiWidgetTagTEI extends AbstractTagTEI {

    protected void collectVariables(TagData data, List<VariableInfo> variables) {
        String var = data.getAttributeString("var");
        if (StringUtils.isNotBlank(var)) {
            variables.add(new VariableInfo(var, "java.lang.Object", true, VariableInfo.NESTED));
        }
        String status = data.getAttributeString("status");
        if (StringUtils.isNotBlank(status)) {
            variables.add(new VariableInfo(status, LoopTagStatus.class.getName(), true, VariableInfo.NESTED));
        }
        variables.add(new VariableInfo(EditMultiWidgetTag.MULTIWIDGET_VAR, EditMultiWidgetTag.class.getName(), true, VariableInfo.NESTED));
        variables.add(new VariableInfo(EditMultiWidgetTag.MULTIWIDGET_CSS_VAR, "java.lang.String", true, VariableInfo.NESTED));
    }
}
