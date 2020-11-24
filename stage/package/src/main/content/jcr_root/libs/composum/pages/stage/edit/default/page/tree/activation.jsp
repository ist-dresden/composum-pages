<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:treeAction icon="play" label="Activate Page" title="Activate Page"
                action="window.composum.pages.actions.page.activate"/>
<cpp:treeAction icon="pause" label="Revert Page" title="Revert Page"
                action="window.composum.pages.actions.page.revert"/>
<cpp:treeAction icon="stop" label="Deactivate Page" title="Deactivate Page"
                action="window.composum.pages.actions.page.deactivate"/>