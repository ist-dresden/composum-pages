<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="model" type="com.composum.pages.commons.model.Folder" selector="delete" languageContext="false"
                title="Delete Folder"
                alert-danger="Do you really want to delete the selected folder?">
    <cpp:include resource="${model.resource}" subtype="edit/tile" replaceSelectors="wide"/>
</cpp:editDialog>
