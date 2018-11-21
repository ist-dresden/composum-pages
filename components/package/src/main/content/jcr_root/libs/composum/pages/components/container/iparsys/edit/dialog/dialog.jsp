<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:editDialog var="container" type="com.composum.pages.commons.model.Container"
                title="@{dialog.selector=='create'?'Create an IParSys':'Edit IParSys'}">
    <cpp:widget label="Parent inheritance cancelled" property="parentInheritanceCancelled" type="checkbox"/>
    <cpp:widget label="Child inheritance cancelled" property="childInheritanceCancelled" type="checkbox"/>
    <cpp:widget label="Policy" property="parsysPolicy" type="select"
                options=":default (inherited last),inheritedFirst:inherited first"/>
</cpp:editDialog>
