<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:widget label="Title" property="title" type="textfield" i18n="true"/>
<cpp:widget label="Text" property="text" type="richtext" height="260px" i18n="true"/>
<div class="row">
    <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
        <cpp:widget label="Floating Text" property="floatingText" type="checkbox"/>
    </div>
    <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
        <cpp:widget label="Text Alignment" property="textAlignment" type="select" options="left,right,center,justify"/>
    </div>
    <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
        <cpp:widget label="Image Position" property="imagePosition" type="select" options="right,left,top,bottom"/>
    </div>
</div>
