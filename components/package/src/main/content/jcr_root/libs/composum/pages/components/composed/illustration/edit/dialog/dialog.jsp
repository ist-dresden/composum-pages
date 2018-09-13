<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="illustration" type="com.composum.pages.components.model.illustration.Illustration"
                title="@{dialog.selector=='create'?'Create an Illustration':'Edit Illustration'}">
    <cpp:editDialogTab tabId="content" label="Image">
        <cpp:include path="image" resourceType="composum/pages/components/element/image/edit/dialog"
                     replaceSelectors="embedded"/>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="shape" label="Shape">
        <cpp:widget label="Behavior" property="shape/behavior" type="select" options="accordion,independent"/>
    </cpp:editDialogTab>
</cpp:editDialog>
