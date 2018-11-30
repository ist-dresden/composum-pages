<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="text" type="com.composum.pages.components.model.text.Text"
                title="@{dialog.selector=='create'?'Create a Text':'Edit Text'}">
    <sling:call script="embedded.jsp"/>
</cpp:editDialog>
