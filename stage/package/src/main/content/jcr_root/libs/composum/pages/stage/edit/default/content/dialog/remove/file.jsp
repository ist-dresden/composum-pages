<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="file" type="com.composum.pages.commons.model.File" selector="delete" languageContext="false"
                title="Delete File" successEvent="content:deleted"
                alert-danger="Do you really want to delete the seleted file?">
    <cpp:include resource="${file.resource}" subtype="edit/tile" replaceSelectors="wide"/>
</cpp:editDialog>
