<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog title="Edit Event Properties">
    <cpp:editDialogTab tabId="page" label="Event">
        <sling:call script="event.jsp"/>
        <sling:call script="general.jsp"/>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="navigation" label="Navigation">
        <sling:call script="navigation.jsp"/>
        <sling:call script="language.jsp"/>
    </cpp:editDialogTab>
</cpp:editDialog>
