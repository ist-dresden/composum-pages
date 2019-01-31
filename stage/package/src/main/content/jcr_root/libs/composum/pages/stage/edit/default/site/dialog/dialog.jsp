<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="site" type="com.composum.pages.commons.model.Site" selector="generic" languageContext="false"
                title="Site Properties" submitLabel="Save">
    <cpp:editDialogTab tabId="site" label="Site">
        <sling:call script="site-settings.jsp"/>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="languages" label="Languages">
        <cpp:include path="${dialog.editPath}/languages"
                     resourceType="composum/pages/stage/edit/site/languages/edit/dialog" replaceSelectors="languages"/>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="pages" label="Page Presets">
        <sling:call script="page-presets.jsp"/>
    </cpp:editDialogTab>
</cpp:editDialog>
