package com.composum.pages.commons.taglib;

import com.composum.sling.cpnl.ComponentTagTEI;
import org.apache.commons.lang.StringUtils;

import javax.servlet.jsp.jstl.core.LoopTagStatus;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.VariableInfo;
import java.util.List;

public class EditWidgetTagTEI extends ComponentTagTEI {

    protected void collectVariables(TagData data, List<VariableInfo> variables) {
        String multi = data.getAttributeString("multi");
        if ("true".equalsIgnoreCase(multi)) {
            String status = data.getAttributeString("status");
            if (StringUtils.isNotBlank(status)) {
                variables.add(new VariableInfo(status, LoopTagStatus.class.getName(), true, VariableInfo.NESTED));
            }
        }
        variables.add(new VariableInfo(EditWidgetTag.WIDGET_VAR, EditWidgetTag.class.getName(), true, VariableInfo.NESTED));
        variables.add(new VariableInfo(EditWidgetTag.WIDGET_CSS_VAR, "java.lang.String", true, VariableInfo.NESTED));
    }
}
