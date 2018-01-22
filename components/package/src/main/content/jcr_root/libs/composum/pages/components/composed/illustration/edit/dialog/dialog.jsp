<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editDialog var="illustration" type="com.composum.pages.components.model.illustration.Illustration"
                title="@{dialog.selector=='create'?'Create an Illustration':'Edit Illustration'}">
    <cpp:editDialogTab tabId="content" label="Content">
        <cpp:widget label="Image" property="image/imageRef" type="image" mandatory="true"/>
        <cpp:widget label="Alt Text" property="image/alt" type="textfield" i18n="true"/>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="shape" label="Shape">
        <cpp:widget label="Behavior" property="shape/behavior" type="select" options="accordion,independent"/>
    </cpp:editDialogTab>
</cpp:editDialog>
