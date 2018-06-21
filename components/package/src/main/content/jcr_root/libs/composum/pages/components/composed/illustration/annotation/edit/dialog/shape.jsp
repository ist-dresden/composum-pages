<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editDialogGroup label="Handle" expanded="true">
    <div class="row">
        <div class="col-xs-6">
            <cpp:widget label="Type" property="shape/type" type="select" options="circle,roundrect,rectangle"/>
        </div>
        <div class="col-xs-6">
            <cpp:widget label="Icon" property="shape/icon" type="select" options="number,plus,circle:dot,bullseye,dot-circle-o:dot circle,comment,wrench,none"/>
        </div>
    </div>
    <div class="row">
        <div class="col-xs-6">
            <cpp:widget label="Position" property="shape/position" type="position"/>
        </div>
        <div class="col-xs-6">
        </div>
    </div>
</cpp:editDialogGroup>
<cpp:editDialogGroup label="Annotation" expanded="true">
    <div class="row">
        <div class="col-xs-6">
            <cpp:widget label="Alignment" property="shape/align" type="select" options="top,left,right,bottom"/>
        </div>
        <div class="col-xs-6">
            <cpp:widget label="Offset" property="shape/offset" type="textfield"/>
        </div>
    </div>
    <div class="row">
        <div class="col-xs-6">
            <cpp:widget label="Size" property="shape/size" type="dimension"/>
        </div>
        <div class="col-xs-6">
            <cpp:widget label="Open" property="visible" type="checkbox"/>
        </div>
    </div>
</cpp:editDialogGroup>
