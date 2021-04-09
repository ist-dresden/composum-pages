<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="pageProps" type="com.composum.pages.commons.model.Page"
                title="Edit Page Properties" successEvent="page:reload">
    <cpp:editDialogTab tabId="page" label="Page">
        <sling:call script="general.jsp"/>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="navigation" label="Navigation">
        <sling:call script="navigation.jsp"/>
        <sling:call script="language.jsp"/>
    </cpp:editDialogTab>
    <sling:call script="page-presets.jsp"/>
    <sling:call script="seo.jsp"/>
</cpp:editDialog>
