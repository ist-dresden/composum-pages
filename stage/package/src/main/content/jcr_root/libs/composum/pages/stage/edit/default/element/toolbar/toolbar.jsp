<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editToolbar var="frame" type="com.composum.pages.stage.model.edit.FramePage">
    <cpp:editAction icon="edit" label="Edit" title="Edit the selected Element"
                    action="window.composum.pages.actions.element.edit"/>
    <cpp:editAction icon="trash" label="Delete" title="Delete the selected Element"
                    action="window.composum.pages.actions.element.delete"/>
</cpp:editToolbar>
