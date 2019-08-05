<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="model" type="com.composum.pages.commons.model.Component"
                title="Edit Component Properties">
    <cpp:editDialogTab tabId="properties" label="Properties">
        <cpp:include resourceType="composum/pages/stage/edit/default/develop/component/properties"
                     replaceSelectors="embedded"/>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="elements" label="Elements">
        <cpp:include resourceType="composum/pages/stage/edit/default/develop/component/manage"
                     replaceSelectors="embedded"/>
    </cpp:editDialogTab>
</cpp:editDialog>
