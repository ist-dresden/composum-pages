<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editDialog var="site" type="com.composum.pages.commons.model.Site"
                title="Create a new Site" selector="generic" submitLabel="Create" languageContext="false">
    <cpp:editDialogTab tabId="template" label="Template">
        <label class="control-label composum-pages-site-create_template"><cpn:text tagClass="label-text" value="Select a template" i18n="true"/></label>
        <sling:include resourceType="composum/pages/stage/edit/site/templates"/>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="widget" label="Widget">
        <cpp:widget label="Select a template" name="template" type="site-template"/>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="site" label="Site">
        <cpp:widget label="Tenant" name="tenant" value="sites" type="text" disabled="true"/>
        <cpp:widget label="Site Name" name="name" placeholder="the repository name (resource name)" type="text" rules="mandatory"/>
        <cpp:widget label="Site Title" name="title" placeholder="the more readable title of the site" type="text"/>
    </cpp:editDialogTab>
</cpp:editDialog>
