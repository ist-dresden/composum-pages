<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editDialog var="site" type="com.composum.pages.commons.model.Site"
                title="Create a new Site" selector="generic" submitLabel="Create" languageContext="false">
    <cpp:editDialogTab tabId="site" label="Site">
        <cpp:widget label="Tenant" name="tenant" value="sites" type="text"/>
        <cpp:widget label="Site Name" name="name" placeholder="the repository name (resource name)" type="text"/>
        <cpp:widget label="Site Title" name="title" placeholder="the more readable title of the site" type="text"/>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="language" label="Default Language">
        <cpp:multiwidget label="xxx">
            <div class="row">
                <div class="col-lg-9 col-md-9 col-sm-8 col-xs-8">
                    <cpp:widget label="Name" name="language.name" value="english" type="text"/>
                </div>
                <div class="col-lg-3 col-md-3 col-sm-4 col-xs-4">
                    <cpp:widget label="Key" property="language.key" value="en" type="text"/>
                </div>
            </div>
            <div class="row">
                <div class="col-lg-9 col-md-9 col-sm-8 col-xs-8">
                    <cpp:widget label="Label" property="language.label" type="text"/>
                </div>
                <div class="col-lg-3 col-md-3 col-sm-4 col-xs-4">
                    <cpp:widget label="Dir" property="language.direction" value="" type="select" options=",ltr,rtl"/>
                </div>
            </div>
        </cpp:multiwidget>
    </cpp:editDialogTab>
</cpp:editDialog>
