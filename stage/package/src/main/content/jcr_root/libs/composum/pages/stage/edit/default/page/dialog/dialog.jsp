<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="pageProps" type="com.composum.pages.commons.model.Page"
                title="Edit Page Properties">
    <cpp:editDialogTab tabId="page" label="Page">
        <sling:call script="general.jsp"/>
        <sling:call script="navigation.jsp"/>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="languages" label="Languages">
        <sling:call script="languages.jsp"/>
    </cpp:editDialogTab>
</cpp:editDialog>
