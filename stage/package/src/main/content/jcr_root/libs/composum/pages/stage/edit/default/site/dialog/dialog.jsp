<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editDialog var="site" type="com.composum.pages.commons.model.Site" selector="generic" languageContext="false"
                title="Site Properties">
    <cpp:editDialogTab tabId="site" label="Site">
        <div class="row">
            <div class="col-xs-8">
                <cpp:widget label="Title" property="jcr:title" type="text"/>
            </div>
            <div class="col-xs-4">
                <cpp:widget label="Publish Policy" name="publicMode" type="select" options=",PUBLIC,PREVIEW,LIVE"/>
            </div>
        </div>
        <div class="row">
            <div class="col-xs-12">
                <cpp:widget label="Description" property="jcr:description" type="textarea"/>
            </div>
        </div>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="languages" label="Languages">
        <sling:call script="/libs/composum/pages/stage/edit/site/languages/edit/dialog/languages.jsp"/>
    </cpp:editDialogTab>
</cpp:editDialog>
