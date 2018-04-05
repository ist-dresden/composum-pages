<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="model" type="com.composum.pages.commons.model.GenericModel" selector="generic"
                title="Copy Content" submitLabel="Copy" languageContext="false">
    <div class="panel panel-info tile-panel">
        <div class="panel-body">
            <cpp:include resource="${model.resource}" subtype="edit/tile" replaceSelectors="wide"/>
        </div>
    </div>
    <cpp:widget name="path" value="${model.path}" type="hidden"/>
    <cpp:widget label="From" name="oldPath" value="${model.resource.parent.path}" disabled="true" type="pathfield"/>
    <cpp:widget label="To" name="newPath" type="pathfield" value="${model.resource.parent.path}" mandatory="true"/>
    <div class="row">
        <div class="col-xs-6">
            <cpp:widget label="Name" name="name" value="${model.name}" type="textfield" mandatory="true"/>
        </div>
        <div class="col-xs-4">
            <cpp:widget label="Order Before" name="before" hint="or ..." type="textfield"/>
        </div>
        <div class="col-xs-2">
            <cpp:widget label="Position" name="index" type="numberfield" options="-1"/>
        </div>
    </div>
</cpp:editDialog>
