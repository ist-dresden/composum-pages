<%@page session="false" pageEncoding="utf-8" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<cpp:editToolbar var="frame" type="com.composum.pages.stage.model.edit.FramePage">
    <cpp:editAction icon="edit" label="Edit" title="Edit the site properties"
                    action="window.composum.pages.actions.site.edit"/>
</cpp:editToolbar>
