<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="container" type="com.composum.pages.commons.model.Container"
                title="@{dialog.selector=='create'?'Create a Container':'Edit Container'}">
    <cpp:widget label="with spacing" property="withSpacing" type="checkbox"/>
</cpp:editDialog>
