<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="model" type="com.composum.pages.commons.model.Component" selector="delete" languageContext="false"
                title="Delete Component" successEvent="content:deleted"
                alert-danger="Do you really want to delete the selected component?">
    <div class="panel panel-info tile-panel">
    <div class="panel-body">
    <cpp:include resource="${model.resource}" subtype="edit/tile" replaceSelectors="wide"/>
    </div>
    </div>
</cpp:editDialog>
