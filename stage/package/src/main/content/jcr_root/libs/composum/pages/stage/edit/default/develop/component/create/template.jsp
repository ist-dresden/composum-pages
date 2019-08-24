<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="model" type="com.composum.pages.commons.model.GenericModel" selector="generic"
                title="Create a new Component" languageContext="false" submitLabel="Create"
                submit="/bin/cpm/pages/develop.createComponent.json@{model.path}" successEvent="content:inserted">
    <cpp:editDialogTab tabId="properties" label="Properties">
        <cpp:widget label="Template Path" name="templatePath" type="pathfield" value="${model.path}"/>
        <cpp:include replaceSelectors="properties"/>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="elements" label="Elements">
        <cpp:include resourceType="composum/pages/stage/edit/default/develop/component/manage"
                     replaceSelectors="embedded"/>
    </cpp:editDialogTab>
</cpp:editDialog>
