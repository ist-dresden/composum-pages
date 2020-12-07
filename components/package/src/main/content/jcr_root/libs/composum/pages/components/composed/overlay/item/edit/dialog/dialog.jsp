<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog title="@{dialog.selector=='create'?'Create an Item':'Overlay Item'}">
    <div class="row">
        <div class="col col-xs-1">
        </div>
        <div class="col col-xs-3">
            <cpp:widget type="checkbox" label="Disabled" property="disabled"/>
        </div>
        <div class="col col-xs-5">
            <cpp:widget type="datetimefield" label="Start Date" property="startDate"/>
        </div>
        <div class="col col-xs-3">
            <cpp:widget type="select" label="Horizontal" property="alignHorizontal"
                        options="left,center,right,full" default="full"/>
        </div>
    </div>
    <div class="row">
        <div class="col col-xs-1">
        </div>
        <div class="col col-xs-3">
            <cpp:widget type="checkbox" label="Hide Content" property="hideContent"/>
        </div>
        <div class="col col-xs-5">
            <cpp:widget type="datetimefield" label="End Date" property="endDate"/>
        </div>
        <div class="col col-xs-3">
            <cpp:widget type="select" label="Vertical" property="alignVertical"
                        options="top,center,bottom,full" default="full"/>
        </div>
    </div>
    <cpp:widget type="textfield" label="CSS Style" property="cssStyle"/>
</cpp:editDialog>
