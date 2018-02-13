<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editToolbar var="frame" type="com.composum.pages.stage.model.edit.FramePage">
    <cpp:editAction icon="edit" label="Edit" title="Edit the microsite properties"
                    action="window.composum.pages.actions.element.edit"/>
    <cpp:editAction icon="upload" label="Upload" title="Upload the microsite content"
                    action="window.composum.pages.actions.dialog.open" selectors="upload"/>
    <cpp:editAction icon="trash" label="Delete" title="Delete the entire microsite"
                    action="window.composum.pages.actions.element.delete"/>
</cpp:editToolbar>
