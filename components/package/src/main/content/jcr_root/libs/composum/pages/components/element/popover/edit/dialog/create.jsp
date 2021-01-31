<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog title="Create Popup Content" selector="create">
    <cpp:editDialogTab tabId="text" label="Popup">
        <sling:call script="link.jsp"/>
        <sling:call script="embedded.jsp"/>
    </cpp:editDialogTab>
</cpp:editDialog>
