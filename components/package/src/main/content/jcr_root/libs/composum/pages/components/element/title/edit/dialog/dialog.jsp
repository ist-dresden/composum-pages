<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="text" type="com.composum.pages.components.model.title.Title"
                title="@{dialog.selector=='create'?'Create a Title':'Edit Title'}">
    <cpp:widget label="Title" property="title" type="textfield" i18n="true"/>
    <cpp:widget label="Subtitle" property="subtitle" type="textfield" i18n="true"/>
    <cpp:widget label="Image" property="image/imageRef" type="imagefield" i18n="true"/>
</cpp:editDialog>
