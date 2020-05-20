<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="model" type="com.composum.pages.commons.model.GenericModel" selector="generic"
                title="Move Content" submitLabel="Move" languageContext="false">
    <div class="panel panel-info tile-panel">
        <div class="panel-body">
            <cpp:include resource="${model.resource}" subtype="edit/tile" replaceSelectors="wide"/>
        </div>
    </div>
    <cpp:widget name="path" value="${model.path}" type="hidden"/>
    <cpp:widget label="From" name="oldPath" value="${model.resource.parent.path}" disabled="true" type="pathfield"/>
    <cpp:widget label="To" name="newPath" type="pathfield" value="${model.resource.parent.path}" required="true"/>
    <div class="row">
        <div class="col col-xs-5">
            <cpp:widget label="Name" name="name" value="${model.name}" type="textfield"
                        required="true" pattern="^[\\w][\\w -]*$"/>
        </div>
        <div class="col col-xs-4">
            <cpp:widget label="Order Before" name="before" hint="or ..." type="textfield"/>
        </div>
        <div class="col col-xs-3">
            <cpp:widget label="Position" name="index" type="numberfield" options="-1"/>
        </div>
    </div>
</cpp:editDialog>
