<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="dlgPage" type="com.composum.pages.commons.model.Page" selector="delete" languageContext="false"
                title="Delete Page" successEvent="content:deleted"
                alert-danger="Do you really want to delete the seleted page?">
    <cpp:include resource="${dlgPage.resource}" subtype="edit/tile" replaceSelectors="wide"/>
</cpp:editDialog>
