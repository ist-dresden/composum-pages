<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<div class="row">
    <div class="col col-xs-4">
        <cpp:widget label="Level" property="shape/level" type="select" options="default,primary,success,info,warning,danger"/>
    </div>
    <div class="col col-xs-4">
        <cpp:widget label="Icon" property="shape/icon" type="select"
                    options="number,plus,circle:dot,bullseye,dot-circle-o:dot circle,comment,wrench,none"/>
    </div>
    <div class="col col-xs-4">
        <cpp:widget label="Type" property="shape/type" type="select" options="circle,roundrect,rectangle"/>
    </div>
</div>
<div class="row">
    <div class="col col-xs-8">
        <cpp:widget label="Position" property="shape/position" type="position"/>
    </div>
    <div class="col col-xs-4">
        <cpp:widget label="Placement" property="shape/placement" type="select" options="top,left,right,bottom"/>
    </div>
</div>
