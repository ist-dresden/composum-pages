<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editDialog var="site" type="com.composum.pages.commons.model.Site" selector="generic" languageContext="false"
                title="Delete Site" submitLabel="Delete" submit="/bin/cpm/pages/edit.deleteSite.json@{site.path}"
                alert-danger="Do you really want to delete the entire site?">
    <cpp:include resource="${site.resource}" subtype="edit/tile" replaceSelectors="wide"/>
</cpp:editDialog>
