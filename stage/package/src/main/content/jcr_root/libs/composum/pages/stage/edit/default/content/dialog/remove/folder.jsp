<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="folder" type="com.composum.pages.commons.model.Folder" selector="delete" languageContext="false"
                title="Delete Folder" successEvent="content:deleted"
                alert-danger="Do you really want to delete the seleted folder?">
    <cpp:include resource="${folder.resource}" subtype="edit/tile" replaceSelectors="wide"/>
</cpp:editDialog>
