<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="model" type="com.composum.pages.commons.model.GenericModel" selector="generic"
                title="Move Content" submitLabel="Move" languageContext="false">
    <cpp:include resource="${model.resource}" subtype="edit/tile" replaceSelectors="wide"/>
    <cpp:widget name="path" value="${model.path}" type="hidden"/>
    <cpp:widget label="From" name="oldPath" value="${model.resource.parent.path}" disabled="true" type="path"/>
    <cpp:widget label="To" name="newPath" type="path"/>
    <div class="row">
        <div class="col-xs-6">
            <cpp:widget label="Name" name="name" value="${model.name}" type="textfield"/>
        </div>
        <div class="col-xs-4">
            <cpp:widget label="Order Before" name="before" type="textfield"/>
        </div>
        <div class="col-xs-2">
            <cpp:widget label="Position" name="index" type="numberfield" options="-1"/>
        </div>
    </div>
</cpp:editDialog>
