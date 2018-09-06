<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="text" type="com.composum.pages.components.model.text.Text"
                title="@{dialog.selector=='create'?'Create a Text':'Edit Text'}">
    <div class="row">
        <div class="col col-xs-9">
            <cpp:widget label="Subtitle" property="title" type="textfield" i18n="true"/>
        </div>
        <div class="col col-xs-3">
            <cpp:widget label="Alignment" property="textAlignment" type="select" options="left,right,center,justify"/>
        </div>
    </div>
    <cpp:widget label="Text" property="text" type="richtext" i18n="true"/>
</cpp:editDialog>
