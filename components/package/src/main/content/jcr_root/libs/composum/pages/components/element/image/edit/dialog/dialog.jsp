<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="element" type="com.composum.pages.commons.model.Image"
                title="@{dialog.selector=='create'?'Create an Image':'Edit Image'}">
    <sling:call script="embedded.jsp"/>
</cpp:editDialog>
