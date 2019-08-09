<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="model" type="com.composum.pages.commons.model.GenericModel" selector="create"
                title="Create a new Component" languageContext="false"
                submit="" successEvent="component:created">
    <cpp:editDialogTab tabId="properties" label="Properties">
        <div class="row">
            <div class="col col-xs-8">
                <cpp:widget label="Path" name="path" type="pathfield" value="${model.path}" required="true"/>
            </div>
            <div class="col col-xs-4">
                <cpp:widget label="Name" name=":name" type="textfield" required="true"/>
            </div>
        </div>
        <cpp:include resourceType="composum/pages/stage/edit/default/develop/component/properties"
                     replaceSelectors="create"/>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="elements" label="Elements">
        <cpp:include resourceType="composum/pages/stage/edit/default/develop/component/manage"
                     replaceSelectors="create"/>
    </cpp:editDialogTab>
</cpp:editDialog>
