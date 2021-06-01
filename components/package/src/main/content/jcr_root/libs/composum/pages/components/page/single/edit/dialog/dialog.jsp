<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog title="Single Page Properties" successEvent="page:reload">
    <cpp:editDialogTab tabId="page" label="Page">
        <sling:call script="general.jsp"/>
        <cpp:widget label="Logo Link" property="logoLink" type="linkfield"/>
        <sling:call script="logo.jsp"/>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="navigation" label="Navigation">
        <sling:call script="navigation.jsp"/>
        <sling:call script="language.jsp"/>
    </cpp:editDialogTab>
    <sling:call script="page-presets.jsp"/>
</cpp:editDialog>
