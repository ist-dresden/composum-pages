<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<div class="row">
    <div class="col col-xs-7">
        <cpp:widget label="Link Text" property="linkText" type="textfield" i18n="true"/>
    </div>
    <div class="col col-xs-3">
        <cpp:widget label="Placement" property="placement" type="select"
                    options="top,left,right,bottom" default="top"/>
    </div>
    <div class="col col-xs-2">
        <cpp:widget label="Button" property="isButton" type="checkbox"/>
    </div>
</div>
