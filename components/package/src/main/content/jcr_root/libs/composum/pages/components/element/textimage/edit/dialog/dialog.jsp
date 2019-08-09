<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog title="Edit Text and Image">
    <cpp:editDialogTab tabId="text" label="Text">
        <sling:call script="embedded.jsp"/>
    </cpp:editDialogTab>
    <cpp:editDialogTab tabId="image" label="Image">
        <cpp:include path="image" resourceType="composum/pages/components/element/image" subtype="edit/dialog"
                     replaceSelectors="embedded"/>
    </cpp:editDialogTab>
</cpp:editDialog>
