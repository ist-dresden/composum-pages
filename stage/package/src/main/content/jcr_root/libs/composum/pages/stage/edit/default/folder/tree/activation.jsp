<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:treeAction icon="play" label="Activate Tree" title="Activate Tree"
                action="window.composum.pages.actions.folder.activate"/>
<cpp:treeAction icon="pause" label="Revert Tree" title="Revert Tree"
                action="window.composum.pages.actions.folder.revert"/>