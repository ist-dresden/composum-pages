<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="text" type="com.composum.pages.components.model.composed.table.Cell"
                title="@{dialog.selector=='create'?'Create Cell':'Edit Cell'}">
    <cpp:widget label="Text" property="text" type="richtext" i18n="true"/>
    <div class="row">
        <div class="col col-xs-4">
            <cpp:widget label="Vertical Align" property="verticalAlign" type="select"
                        options="top,bottom,middle,baseline" default="top"/>
        </div>
        <div class="col col-xs-3">
            <cpp:widget label="Table Head" name="head" type="checkbox"/>
        </div>
        <div class="col col-xs-3">
            <cpp:widget label="Width" property="width" type="textfield"/>
        </div>
        <div class="col col-xs-2">
            <cpp:widget label="Rowspan" property="rowspan" type="numberfield" options="0"/>
        </div>
    </div>
    <div class="row">
        <div class="col col-xs-4">
            <cpp:widget label="Text Alignment" property="textAlignment" type="select"
                        options="left,right,center,justify" default="left"/>
        </div>
        <div class="col col-xs-6">
            <cpp:widget label="Warning Level" property="level" type="select"
                        hint="<a href='https://getbootstrap.com/docs/3.3/css/#tables-contextual-classes' target='_blank'>'Bootstrap' background</a>"
                        options=",active,info,success,warning,danger"/>
        </div>
        <div class="col col-xs-2">
            <cpp:widget label="Colspan" property="colspan" type="numberfield" options="0"/>
        </div>
    </div>
</cpp:editDialog>
