<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog title="Edit Component Properties" successEvent="component:changed">
    <cpp:editDialogTab tabId="properties" label="Properties">
        <sling:call script="embedded.jsp"/>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="thumbnail" label="Thumbnail Image">
        <sling:call script="thumbnail.jsp"/>
    </cpp:editDialogTab>
</cpp:editDialog>
