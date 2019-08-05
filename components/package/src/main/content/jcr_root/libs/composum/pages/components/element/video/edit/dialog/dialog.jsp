<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="element" type="com.composum.pages.components.model.asset.Video"
                title="@{dialog.selector=='create'?'Create a Video':'Edit Video'}">
    <sling:call script="embedded.jsp"/>
</cpp:editDialog>
