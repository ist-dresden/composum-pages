<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog title="Create a new Component" languageContext="false" selector="generic" submitLabel="Create"
                submit="/bin/cpm/pages/develop.createComponent.json@{model.path}" successEvent="content:inserted">
    <cpp:editDialogTab tabId="properties" label="Properties">
        <cpp:include replaceSelectors="properties"/>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="elements" label="Elements">
        <cpp:include resourceType="composum/pages/stage/edit/default/develop/component/manage"
                     replaceSelectors="create"/>
    </cpp:editDialogTab>
</cpp:editDialog>
