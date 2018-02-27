<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editDialog var="site" type="com.composum.pages.commons.model.Site" selector="wizard" languageContext="false"
                title="Create a new Site" submitLabel="Create"
                submit="@{contextPath}/bin/cpm/pages/edit.createSite.json">
    <cpp:editDialogTab tabId="template" label="Template">
        <cpp:widget label="Select a template" name="template" type="site-template"/>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="site" label="Site">
        <cpp:widget label="Tenant" name="tenant" value="sites" type="textfield" disabled="true"/>
        <cpp:widget label="Site Name" name="name" placeholder="the repository name (resource name)" type="textfield" rules="mandatory"/>
        <cpp:widget label="Site Title" name="title" placeholder="the more readable title of the site" type="textfield"/>
        <cpp:widget label="Description" name="description" type="textarea"/>
    </cpp:editDialogTab>
</cpp:editDialog>
