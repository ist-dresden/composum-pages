<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:model var="title" type="com.composum.pages.components.model.text.Title">
    <cpp:widget label="Title" property="title" type="textfield" i18n="true"/>
    <cpp:widget label="Subtitle" property="subtitle" type="textfield" i18n="true"/>
    <cpp:widget label="Image" property="image/imageRef" type="imagefield" i18n="true"/>
</cpp:model>
