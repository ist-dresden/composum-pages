<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="sites" type="com.composum.pages.commons.model.Sites" selector="wizard" languageContext="false"
                title="Create a new Site" resourcePath="*" submitLabel="Create"
                submit="/bin/cpm/pages/edit.createSite.json">
    <cpp:editDialogTab tabId="template" label="Template">
        <cpp:widget label="Select a template" name="template" type="site-template"/>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="site" label="Site">
        <div class="row">
            <cpn:div test="${sites.tenantSupport}" class="col col-xs-5">
                <cpp:widget label="Tenant" name="tenant" type="select" required="true"
                            options="${sites.tenantOptions}"/>
            </cpn:div>
            <div class="col col-xs-${sites.tenantSupport?'7':'12'}">
                <cpp:widget label="Name" name="name" placeholder="the repository name" type="textfield"
                            required="true" pattern="^[\\w][\\w -]*(/[\\w][\\w -]*)*$"/>
            </div>
        </div>
        <cpp:widget label="Title" name="jcr:title" placeholder="the more readable title" type="textfield"/>
        <cpp:widget label="Description" name="jcr:description" type="richtext"/>
    </cpp:editDialogTab>
</cpp:editDialog>
