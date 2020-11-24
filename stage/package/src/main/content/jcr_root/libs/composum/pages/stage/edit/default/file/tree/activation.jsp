<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:treeAction icon="play" label="Activate Asset" title="Activate Asset"
                action="window.composum.pages.actions.file.activate"/>
<cpp:treeMenu key="more" icon="cog" label="Release..." title="more release actions...">
    <cpp:menuItem icon="pause" label="Revert Asset" title="Revert Asset"
                  action="window.composum.pages.actions.file.revert"/>
    <cpp:menuItem icon="stop" label="Deactivate Asset" title="Deactivate Asset"
                  action="window.composum.pages.actions.file.deactivate"/>
</cpp:treeMenu>
