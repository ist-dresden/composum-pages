/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.taglib;

import com.composum.sling.cpnl.AbstractTagTEI;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.VariableInfo;
import java.util.List;

import static com.composum.pages.commons.taglib.DropZoneTag.DROP_ZONE_CSS_VAR;
import static com.composum.pages.commons.taglib.DropZoneTag.DROP_ZONE_VAR;

public class DropZoneTagTEI extends AbstractTagTEI {

    @Override
    protected void collectVariables(TagData data, List<VariableInfo> variables) {
        variables.add(new VariableInfo(DROP_ZONE_VAR, DropZoneTag.class.getName(), true, VariableInfo.NESTED));
        variables.add(new VariableInfo(DROP_ZONE_CSS_VAR, "java.lang.String", true, VariableInfo.NESTED));
    }
}
